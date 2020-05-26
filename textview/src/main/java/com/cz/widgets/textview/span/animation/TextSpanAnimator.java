package com.cz.widgets.textview.span.animation;

import android.animation.ObjectAnimator;

import androidx.annotation.NonNull;

/**
 * @author Created by cz
 * @date 2020-04-19 18:45
 * @email bingo110@126.com
 *
 * This text animation support this use {@link ObjectAnimator} to manipulate the animation span.
 */
public class TextSpanAnimator implements TextAnimator{
    private final ObjectAnimator objectAnimator;

    public TextSpanAnimator(@NonNull ObjectAnimator objectAnimator) {
        this.objectAnimator = objectAnimator;
    }

    @Override
    public void start() {
        this.objectAnimator.start();
    }

    @Override
    public void resume() {
        this.objectAnimator.resume();
    }

    @Override
    public void pause() {
        this.objectAnimator.pause();
    }

    @Override
    public void cancel() {
        this.objectAnimator.removeAllUpdateListeners();
        this.objectAnimator.removeAllListeners();
        this.objectAnimator.cancel();
    }

    public ObjectAnimator getObjectAnimator() {
        return objectAnimator;
    }
}
