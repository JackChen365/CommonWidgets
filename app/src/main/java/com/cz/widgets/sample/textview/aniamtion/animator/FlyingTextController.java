package com.cz.widgets.sample.textview.aniamtion.animator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.cz.widgets.textview.span.animation.AnimationTextSpan;
import com.cz.widgets.textview.span.animation.controller.AbsTextController;

import java.util.List;


/**
 * Created by cz
 * @date 2020-04-20 21:14
 * @email bingo110@126.com
 * Demonstrate all the words flying to the view.
 */
public class FlyingTextController extends AbsTextController {
    private static final int[] OUT_LOCATION=new int[2];
    private static final int TYPING_SPEED =10;
    private final Paint dividerPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final EditText editText;
    private int index;

    private Runnable flingRunnable =new Runnable() {
        @Override
        public void run() {
            List<AnimationTextSpan> animationTextSpanList = getAnimationTextSpanList();
            if(index <animationTextSpanList.size()){
                final AnimationTextSpan animationTextSpan = animationTextSpanList.get(index);
                animationTextSpan.setAlpha(1f);
                RectF bounds = animationTextSpan.getBounds();
                TextView textView = getTextView();
                textView.getLocationInWindow(OUT_LOCATION);
                int left = OUT_LOCATION[0];
                int top = OUT_LOCATION[1];
                editText.getLocationInWindow(OUT_LOCATION);
                editText.setText(String.valueOf(animationTextSpan.getSpanText()));
                animationTextSpan.setX(OUT_LOCATION[0]+editText.getPaddingLeft()-left);
                animationTextSpan.setY(OUT_LOCATION[1]+editText.getPaddingTop()-top);
                animationTextSpan.propertyAnimator().x(bounds.left).y(bounds.top);
                textView.postDelayed(this, TYPING_SPEED);
                index++;
            }
        }
    };

    public FlyingTextController(Context context, EditText editText) {
        this.editText=editText;
        TypedArray a = context.obtainStyledAttributes(null, new int[]{android.R.attr.colorAccent});
        int accentColor = a.getColor(0, Color.RED);
        dividerPaint.setColor(accentColor);
        a.recycle();
    }

    @Override
    public void onAttachToView(TextView hostView) {
        super.onAttachToView(hostView);
        ViewParent parent = hostView.getParent();
        if(parent instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) parent;
            viewGroup.setClipChildren(false);
        }
    }

    @Override
    protected void prepareAnimator(@NonNull List<AnimationTextSpan> animationTextSpanList) {
        super.prepareAnimator(animationTextSpanList);
        for(AnimationTextSpan animationTextSpan:animationTextSpanList){
            animationTextSpan.setAlpha(0f);
        }
        invalidate();
    }

    @Override
    protected void startAnimator(List<AnimationTextSpan> animationTextSpanList) {
        TextView hostView = getTextView();
        hostView.post(flingRunnable);
    }

    @Override
    public void cancel() {
        super.cancel();
        TextView hostView = getTextView();
        hostView.removeCallbacks(flingRunnable);
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawParagraphDivider(canvas);
    }

    private void drawParagraphDivider(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        TextView hostView = getTextView();
        int totalPaddingTop = hostView.getTotalPaddingTop();
        int rightPadding = hostView.getTotalPaddingRight();
        int bottomPadding = hostView.getTotalPaddingBottom();
        Layout layout = hostView.getLayout();
        int lineCount = layout.getLineCount();
        for(int i=0;i<lineCount;i++){
            int lineBottom = layout.getLineBottom(i);
            if(i==lineCount-1){
                canvas.drawLine(0, height-bottomPadding,width-rightPadding,height-bottomPadding,dividerPaint);
            } else {
                canvas.drawLine(0,lineBottom-totalPaddingTop,width-rightPadding,lineBottom-totalPaddingTop,dividerPaint);
            }
        }
    }
}
