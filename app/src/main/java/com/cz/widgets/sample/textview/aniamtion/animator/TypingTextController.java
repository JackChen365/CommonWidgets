package com.cz.widgets.sample.textview.aniamtion.animator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.TextPaint;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.cz.widgets.textview.span.animation.AnimationTextSpan;
import com.cz.widgets.textview.span.animation.controller.AbsTextController;

import java.util.List;


/**
 * Created by cz
 * @date 2020-04-20 21:14
 * @email bingo110@126.com
 * Demonstrate typing word.
 */
public class TypingTextController extends AbsTextController {
    private static final int TYPING_SPEED =10;
    private final Paint dividerPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Drawable cursorDrawable;
    private int index;

    private Runnable typingRunnable=new Runnable() {
        @Override
        public void run() {
            List<AnimationTextSpan> animationTextSpanList = getAnimationTextSpanList();
            if(index<animationTextSpanList.size()){
                AnimationTextSpan animationTextSpan = animationTextSpanList.get(index);
                animationTextSpan.propertyAnimator().alpha(1f);

                index++;
                TextView hostView = getTextView();
                hostView.postDelayed(this, TYPING_SPEED);
            }
        }
    };
    public TypingTextController(Context context) {
        TypedArray a = context.obtainStyledAttributes(null, new int[]{android.R.attr.textCursorDrawable});
        cursorDrawable = a.getDrawable(0);
        a.recycle();

        a = context.obtainStyledAttributes(null, new int[]{android.R.attr.colorAccent});
        int accentColor = a.getColor(0, Color.RED);
        dividerPaint.setColor(accentColor);
        if(null!=cursorDrawable&&Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            cursorDrawable.setTint(accentColor);
        }
        a.recycle();
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
        hostView.post(typingRunnable);
    }

    @Override
    public void cancel() {
        super.cancel();
        TextView hostView = getTextView();
        hostView.removeCallbacks(typingRunnable);
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawParagraphDivider(canvas);
        drawTypingCursor(canvas);
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

    private void drawTypingCursor(Canvas canvas) {
        TextView hostView = getTextView();
        Layout layout = hostView.getLayout();
        TextPaint paint = layout.getPaint();
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float lineHeight = fontMetrics.descent - fontMetrics.ascent;
        List<AnimationTextSpan> animationTextSpanList = getAnimationTextSpanList();
        if(index<animationTextSpanList.size()){
            AnimationTextSpan animationTextSpan = animationTextSpanList.get(index);
            RectF bounds = animationTextSpan.getBounds();
            int line = layout.getLineForOffset(index);
            int lineTop = layout.getLineTop(line);
            if (null != cursorDrawable) {
                cursorDrawable.setBounds((int)(bounds.left),
                        lineTop,
                        (int) (bounds.left  + cursorDrawable.getIntrinsicWidth()),
                        (int) (lineTop+lineHeight));
                cursorDrawable.draw(canvas);
            }
        }
    }
}
