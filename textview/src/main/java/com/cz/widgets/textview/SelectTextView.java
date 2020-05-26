package com.cz.widgets.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author Created by cz
 * @date 2020-02-16 21:20
 * @email bingo110@126.com
 */
public class SelectTextView extends AppCompatTextView {
    /**
     * The start offset position
     */
    private int selectStart=0;
    /**
     * Select path
     */
    private Path selectPath=new Path();
    /**
     * The paint
     */
    private Paint selectPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * Select text foreground span
     */
    private ForegroundColorSpan selectForegroundColorSpan=null;

    public SelectTextView(Context context) {
        this(context,null,0);
    }

    public SelectTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SelectTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(null, new int[] { R.attr.colorAccent });
        int accentColor = a.getColor(0, 0);
        a.recycle();
        selectPaint.setColor(accentColor);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if(null!=text&&!(text instanceof Spanned)){
            //Marked this text as a spannable. In this case we will be able to change the span
            super.setText(new SpannableString(text), BufferType.SPANNABLE);
        } else {
            super.setText(text, type);
        }
    }

    public void setSelectText(int start,int end){
        Layout layout = getLayout();
        if(null!=layout){
            layout.getSelectionPath(start, end, selectPath);
            CharSequence text = getText();
            if(text instanceof Spannable){
                Spannable spannable = (Spannable) text;
                selectForegroundColorSpan=new ForegroundColorSpan(Color.WHITE);
                spannable.setSpan(selectForegroundColorSpan,start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        float x = event.getX();
        float y = event.getY();
        if(MotionEvent.ACTION_DOWN==action){
            ViewParent parent = getParent();
            parent.requestDisallowInterceptTouchEvent(true);
            addSelection(x-getPaddingLeft(), y-getPaddingTop());
        } else if(MotionEvent.ACTION_MOVE==action){
            updateSelection(x-getPaddingLeft(), y-getPaddingTop());
        }
        return true;
    }

    private void addSelection(float x,float y) {
        Layout layout = getLayout();
        if (null != layout) {
            CharSequence text=getText();
            if(text instanceof Spannable &&null!=selectForegroundColorSpan){
                Spannable spannable = (Spannable) text;
                spannable.removeSpan(selectForegroundColorSpan);
            }
            int line = layout.getLineForVertical((int) y);
            selectStart = layout.getOffsetForHorizontal(line, x);
            layout.getSelectionPath(selectStart, selectStart + 1, selectPath);
            //reset the selected span object
            if(text instanceof Spannable){
                Spannable spannable = (Spannable) text;
                selectForegroundColorSpan=new ForegroundColorSpan(Color.WHITE);
                spannable.setSpan(selectForegroundColorSpan, selectStart, selectStart+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            invalidate();
        }
    }

    /**
     * Update the selected range
     */
    private void updateSelection(float x,float y) {
        Layout layout=getLayout();
        if (null != layout) {
            CharSequence text = getText();
            if(text instanceof Spannable&&null!=selectForegroundColorSpan){
                Spannable spannable = (Spannable) text;
                spannable.removeSpan(selectForegroundColorSpan);
            }
            int line = layout.getLineForVertical((int) y);
            int off = layout.getOffsetForHorizontal(line, x);
            layout.getSelectionPath(selectStart, off, selectPath);

            if(text instanceof Spannable){
                Spannable spannable = (Spannable) text;
                selectForegroundColorSpan=new ForegroundColorSpan(Color.WHITE);
                int offset=Math.min(off,text.length());
                int start=Math.min(selectStart,offset);
                int end=Math.max(selectStart,offset);
                spannable.setSpan(selectForegroundColorSpan,start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            invalidate();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft()*1f,getPaddingTop()*1f);
        canvas.drawPath(selectPath,selectPaint);
        canvas.restore();
        super.onDraw(canvas);
    }


}
