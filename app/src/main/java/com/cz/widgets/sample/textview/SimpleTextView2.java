package com.cz.widgets.sample.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SimpleTextView2 extends View {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint textPaint=new TextPaint();
    private final RectF tempRect=new RectF();
    private CharSequence text;

    public SimpleTextView2(@NonNull Context context) {
        super(context);
        initializeTextPaint();
    }

    public SimpleTextView2(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeTextPaint();
    }

    public SimpleTextView2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeTextPaint();
    }

    private void initializeTextPaint() {
        //todo configure the value in attrs.xml
        paint.setStrokeWidth(1);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 24, displayMetrics));
        textPaint.setColor(Color.BLUE);
    }

    public void setText(CharSequence text){
        this.text=text;
        requestLayout();
    }

    public CharSequence getText() {
        return text;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(null!=text){
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int paddingRight = getPaddingRight();
            int paddingBottom = getPaddingBottom();
            int measuredWidth = getMeasuredWidth();
            int textWidth = (int) (textPaint.measureText(text, 0, text.length())+0.5f);
            Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
            int textHeight = fontMetricsInt.descent - fontMetricsInt.ascent;
            setMeasuredDimension(Math.max(paddingLeft+textWidth+paddingRight,measuredWidth),
                    paddingTop+textHeight+paddingBottom);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        CharSequence text = getText();
        if(TextUtils.isEmpty(text)){
            return;
        }
        //绘文本
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        canvas.save();
        canvas.translate(paddingLeft*1f,paddingTop*1f);
        int leftOffset=0;
        float topOffset=-textPaint.ascent();
        float textHeight = textPaint.descent() - textPaint.ascent();
        for(int i=0;i<text.length();i++){
            //绘文本
            canvas.drawText(text,i,i+1,leftOffset,topOffset,textPaint);
            float textWidth = textPaint.measureText(text, i, i + 1);
            //绘边界
            tempRect.set(leftOffset,topOffset-textHeight,leftOffset+textWidth,topOffset);
            canvas.drawRect(tempRect,paint);
            //递增
            leftOffset+=textWidth;
        }
        canvas.restore();
    }
}
