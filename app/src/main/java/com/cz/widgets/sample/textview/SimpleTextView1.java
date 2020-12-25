package com.cz.widgets.sample.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SimpleTextView1 extends View {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint textPaint=new TextPaint();
    private final Path tmpPath=new Path();
    private final RectF tempRect=new RectF();
    private StaticLayout layout;
    private CharSequence text;

    public SimpleTextView1(@NonNull Context context) {
        super(context);
        initializeTextPaint();
    }

    public SimpleTextView1(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeTextPaint();
    }

    public SimpleTextView1(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    public StaticLayout getLayout() {
        return layout;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        if(null!=text&&(null==layout||text!=layout.getText())){
            int paddingLeft = getPaddingLeft();
            int paddingRight = getPaddingRight();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                layout = StaticLayout.Builder.obtain(text, 0, text.length(), textPaint, measuredWidth - paddingLeft - paddingRight).build();
            } else {
                layout = new StaticLayout(
                        text,
                        textPaint,
                        measuredWidth - paddingLeft - paddingRight,
                        Layout.Alignment.ALIGN_NORMAL,
                        0f,0f,false);
            }
        }
        if(null!=layout){
            int paddingTop = getPaddingTop();
            int paddingBottom = getPaddingBottom();
            int height = layout.getHeight();
            int width = layout.getWidth();
            setMeasuredDimension(Math.max(width,measuredWidth),paddingTop+height+paddingBottom);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(null==layout) return;
        //绘文本
        drawText(canvas);
        //绘边框
        drawTextBorder(canvas);
    }

    private void drawText(Canvas canvas) {
        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        canvas.save();
        canvas.translate(paddingLeft*1f,paddingTop*1f);
        layout.draw(canvas);
        canvas.restore();
    }

    private void drawTextBorder(Canvas canvas) {
        CharSequence text = getText();
        if(TextUtils.isEmpty(text)){
            return;
        }
        Layout layout = getLayout();
        for(int i=0;i<text.length();i++){
            int line = layout.getLineForOffset(i);
            float lineLeft=layout.getLineLeft(line);
            layout.getSelectionPath(i, i + 1, tmpPath);
            tmpPath.computeBounds(tempRect, false);
            tempRect.offset(getPaddingLeft()-lineLeft,getPaddingTop());
            canvas.drawRect(tempRect,paint);
        }
    }
}
