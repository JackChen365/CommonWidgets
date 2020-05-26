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

import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author Created by cz
 * @date 2020-02-16 20:36
 * @email bingo110@126.com
 */
public class MarkedTextView extends AppCompatTextView {
    /**
     * Text selected path
     */
    private final Path selectPath= new Path();
    /**
     * The paint
     */
    private final Paint selectPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * Text selected color span
     */
    private ForegroundColorSpan selectForegroundColorSpan=null;

    public MarkedTextView(Context context) {
        this(context,null,0);
    }

    public MarkedTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MarkedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
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

    private void addSelection(float x,float y) {
        Layout layout = getLayout();
        if (null != layout) {
            CharSequence text = layout.getText();
            int line = layout.getLineForVertical((int) y);
            int off = layout.getOffsetForHorizontal(line, x);
            char c = text.charAt(off);
            boolean needInvalidate=false;
            //Remove the marked foreground text color
            if(text instanceof Spannable&&null != selectForegroundColorSpan) {
                Spannable spannable = (Spannable) text;
                needInvalidate=true;
                spannable.removeSpan(selectForegroundColorSpan);
            }
            //We only check the latter.
            if (Character.isLetter(c) || c == '-' || c == '\'') {
                int lineStart = layout.getLineStart(line);
                //move from the left side and check the left bound of the word
                int start = off;
                while (0<start && (Character.isLetter(text.charAt(start - 1)) ||
                        text.charAt(start - 1) == '-' ||
                        text.charAt(start - 1) == '\'') && start >= lineStart) {
                    start--;
                }
                //move from the right side and check the left bound of the word
                int end = off;
                int lineEnd = layout.getLineEnd(line);
                while ((Character.isLetter(text.charAt(end + 1)) ||
                        text.charAt(end + 1) == '-' ||
                        text.charAt(end + 1) == '\'') && end <= lineEnd) {
                    end++;
                }
                layout.getSelectionPath(start, end+1, selectPath);
                //reset the selected text foreground color
                if(text instanceof Spannable){
                    Spannable spannable = (Spannable) text;
                    selectForegroundColorSpan=new ForegroundColorSpan(Color.WHITE);
                    spannable.setSpan(selectForegroundColorSpan, start, end+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                needInvalidate=true;
            } else {
                //If the selected text is not blank. We plus it the single latter the select path
                CharSequence selectText=text.subSequence(off,off+1);
                if(0!=selectText.toString().trim().length()){
                    needInvalidate=true;
                    layout.getSelectionPath(off, off + 1, selectPath);
                }
            }
            if(needInvalidate){
                invalidate();
            }
        }
    }

    /**
     * Highlight the text programmatically.
     * @param start
     * @param end
     */
    public void setMarkedText(int start, int end){
        Layout layout = getLayout();
        if(null!=layout){
            layout.getSelectionPath(start, end, selectPath);
            //reset the selected text foreground color
            CharSequence text = getText();
            if(text instanceof Spannable){
                Spannable spannable = (Spannable) text;
                selectForegroundColorSpan=new ForegroundColorSpan(Color.WHITE);
                spannable.setSpan(selectForegroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if(MotionEvent.ACTION_DOWN==action){
            float x = event.getX();
            float y = event.getY();
            addSelection(x-getPaddingLeft(), y-getPaddingTop());
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //Draws selected char sequence
        canvas.save();
        canvas.translate(getPaddingLeft()*1f,getPaddingTop()*1f);
        canvas.drawPath(selectPath,selectPaint);
        canvas.restore();
        super.onDraw(canvas);
    }



}
