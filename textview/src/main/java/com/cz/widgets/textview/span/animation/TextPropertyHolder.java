package com.cz.widgets.textview.span.animation;

import android.graphics.RectF;

import androidx.annotation.FloatRange;

/**
 * @author Created by cz
 * @date 2020-04-19 22:54
 * @email bingo110@126.com
 *
 * All the text animation properties.
 *
 * @see AnimationTextSpan
 */
public class TextPropertyHolder {
    private final RectF bounds =new RectF();
    private float alpha=1f;
    private float translationX;
    private float translationY;
    private float scaleX=1f;
    private float scaleY=1f;
    private float rotate=0f;

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getX() {
        return bounds.left+translationX;
    }

    public void setX(float x) {
        this.translationX = x - bounds.left;
    }

    public float getY() {
        return bounds.top+translationY;
    }

    public void setY(float y) {
        this.translationY = y - bounds.top;
    }

    public float getTranslationX() {
        return translationX;
    }

    public void setTranslationX(float translationX) {
        this.translationX = translationX;
    }

    public float getTranslationY() {
        return translationY;
    }

    public void setTranslationY(float translationY) {
        this.translationY = translationY;
    }

    public float getRotation() {
        return rotate;
    }

    public void setRotate(@FloatRange(from = 0,to=360) float rotate) {
        this.rotate = rotate;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getRotate() {
        return rotate;
    }

    public void setBounds(float left,float top,float right,float bottom){
        this.bounds.set(left,top,right,bottom);
    }

    public RectF getBounds() {
        return bounds;
    }

    /**
     * reset all the animator properties.
     */
    public void reset(){
        rotate=0;
        alpha=1f;
        scaleX=1f;
        scaleY=1f;
        translationX=0;
        translationY=0;
        bounds.setEmpty();
    }
}
