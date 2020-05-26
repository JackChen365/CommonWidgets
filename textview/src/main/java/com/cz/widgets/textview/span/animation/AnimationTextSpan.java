package com.cz.widgets.textview.span.animation;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Created by cz
 * @date 2019-05-15 23:51
 * @email bingo110@126.com
 *
 * This is a text replacement span. It is not a normal style span.
 * Instead of a replacement span we use {@link TextPropertyHolder} to support animator properties
 * This replacement span usually cooperate with the widget {@link com.cz.widgets.textview.AnimationTextView}
 * Take a look at this widget and see more detail about how to implement your own text animation
 *
 * All the features.
 *
 * @see #setAlpha(float)
 * @see #setRotate(float)
 * @see #setScaleX(float)
 * @see #setScaleY(float)
 * @see #setTranslationX(float)
 * @see #setTranslationY(float)
 * @see #setX(float)
 * @see #setY(float)
 *
 * Trying to start an animation. You could use those two functions.
 *
 * @see #propertyAnimator() It's an object animation properties. So you could easily start a property animation
 * @see #objectAnimator() It's actually an object animator for this span. It's for you to use {@link android.animation.Keyframe} or something like that.
 *
 * To manipulate the animation outside. Call the method: {@link #animate()}
 */
public class AnimationTextSpan extends ReplacementSpan {
    private TextPropertyHolder propertyHolder=new TextPropertyHolder();
    private TextView textView;
    private TextAnimator textAnimator;
    private CharSequence text;
    private int start;
    private int end;
    private int color;
    private RectF clipRect;
    private Region.Op op;
    private boolean willNotDraw=false;
    private final Rect textBounds =new Rect();

    public AnimationTextSpan(TextView textView, CharSequence text, int start, int end) {
        this.textView = textView;
        this.text=text;
        this.start = start;
        this.end=end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public char getWord(){
        return text.charAt(start);
    }

    public CharSequence getText(){
        return text;
    }

    public CharSequence getSpanText(){
        return text.subSequence(start,end);
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public float getAlpha() {
        return this.propertyHolder.getAlpha();
    }

    public void setAlpha(float alpha) {
        this.propertyHolder.setAlpha(alpha);
    }

    public float getX() {
        return this.propertyHolder.getX();
    }

    public void setX(float x) {
        this.propertyHolder.setX(x);
    }

    public float getY() {
        return this.propertyHolder.getY();
    }

    public void setY(float y) {
        this.propertyHolder.setY(y);
    }

    public float getTranslationX() {
        return this.propertyHolder.getTranslationX();
    }

    public void setTranslationX(float translationX) {
        this.propertyHolder.setTranslationX(translationX);
    }

    public float getTranslationY() {
        return this.propertyHolder.getTranslationY();
    }

    public void setTranslationY(float translationY) {
        this.propertyHolder.setTranslationY(translationY);
    }

    public float getRotation() {
        return this.propertyHolder.getRotation();
    }

    public void setRotate(@FloatRange(from = 0,to=360) float rotate) {
        this.propertyHolder.setRotate(rotate);
    }

    public float getScaleX() {
        return this.propertyHolder.getScaleX();
    }

    public void setScaleX(float scaleX) {
        this.propertyHolder.setScaleX(scaleX);
    }

    public float getScaleY() {
        return this.propertyHolder.getScaleY();
    }

    public void setScaleY(float scaleY) {
        this.propertyHolder.setScaleY(scaleY);
    }

    public boolean isWillNotDraw() {
        return willNotDraw;
    }

    public void setWillNotDraw(boolean willNotDraw) {
        this.willNotDraw = willNotDraw;
    }

    /**
     * Setting the clip rect.
     * @param clipRect
     */
    public void setClipRect(RectF clipRect) {
        this.clipRect = clipRect;
        this.op= Region.Op.INTERSECT;
    }

    /**
     * Setting the clip rect.
     * @param clipRect
     * @see Region.Op#INTERSECT
     * @see Region.Op#DIFFERENCE
     */
    public void setClipRect(RectF clipRect, Region.Op op) {
        this.clipRect = clipRect;
        this.op=op;
    }

    public RectF getBounds() {
        return propertyHolder.getBounds();
    }

    public Rect getTextBounds() {
        return textBounds;
    }

    /**
     * Reset all animator properties
     */
    public void reset(){
        op=null;
        clipRect=null;
        propertyHolder.reset();
    }

    public TextAnimator animate(){
        return textAnimator;
    }

    /**
     * This method returns a TextSpanPropertyAnimator object, which can be used to propertyAnimator
     * specific properties on this View.
     *
     * @return ViewPropertyAnimator The ViewPropertyAnimator associated with this Span.
     */
    public TextSpanPropertyAnimator propertyAnimator() {
        if (textAnimator == null) {
            textAnimator = new TextSpanPropertyAnimator(textView,this);
        }
        return (TextSpanPropertyAnimator) textAnimator;
    }

    public ObjectAnimator objectAnimator() {
        if (textAnimator == null) {
            ObjectAnimator objectAnimator = new ObjectAnimator();
            objectAnimator.setTarget(this);
            objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    invalidate();
                }
            });
            textAnimator=new TextSpanAnimator(objectAnimator);
        }
        TextSpanAnimator textAnimator = (TextSpanAnimator) this.textAnimator;
        return textAnimator.getObjectAnimator();
    }

    public void invalidate(){
        textView.invalidate();
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
        paint.getTextBounds(text.toString(),start,end,textBounds);
        if(null!=fm){
            fm.top = textBounds.top;
            fm.ascent = textBounds.top;
            fm.descent = textBounds.bottom;
            fm.bottom = textBounds.bottom;
        }
        return (int) paint.measureText(text, start, end);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setColor(color);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float offsetX, int top, int offsetY, int bottom, @NonNull Paint paint) {
        //Update the span bounds location.
        RectF bounds = propertyHolder.getBounds();
        float textWidth = paint.measureText(text, start, end);
        bounds.set(offsetX+textBounds.left,
                offsetY+textBounds.top,
                offsetX+textBounds.left+textWidth,
                offsetY+textBounds.top+textBounds.height());
        //Drawing the text information.
        drawText(canvas,paint);
    }

    /**
     * Drawing the text information use all the fields inside.
     * This function actually support us to drawing the text outside by using the boundary of the span.
     * So no matter the span in the text view or not. we could draw this span anywhere.
     * This is for text transform or something like that.
     * @param canvas
     * @param paint
     */
    public void drawText(@NonNull Canvas canvas, @NonNull Paint paint){
        if(!isWillNotDraw()) {
            canvas.save();
            //All the animation properties.
            float alpha = propertyHolder.getAlpha();
            float rotate = propertyHolder.getRotate();
            float scaleX = propertyHolder.getScaleX();
            float scaleY = propertyHolder.getScaleY();
            float translationX = propertyHolder.getTranslationX();
            float translationY = propertyHolder.getTranslationY();
            RectF bounds = propertyHolder.getBounds();
            //We are using canvas behavior to support all of the animation features.
            canvas.rotate(rotate,bounds.centerX(),bounds.centerY());
            canvas.scale(scaleX,scaleY,bounds.centerX(),bounds.centerY());
            paint.setAlpha(Math.round(0xFF*alpha));
            if(null!=clipRect&&null!=op){
                canvas.clipRect(clipRect, op);
            }
            Rect textBounds = getTextBounds();
            float offsetX=bounds.left;
            float offsetY=bounds.top-textBounds.top;
            canvas.drawText(text,start,end,translationX+offsetX,translationY+offsetY,paint);
            canvas.restore();
        }
    }
}
