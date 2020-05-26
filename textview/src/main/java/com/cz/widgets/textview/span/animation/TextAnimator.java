package com.cz.widgets.textview.span.animation;

/**
 * @author Created by cz
 * @date 2020-04-19 18:42
 * @email bingo110@126.com
 * This interface is for animation object.
 * @see TextSpanAnimator
 * @see TextSpanPropertyAnimator
 */
public interface TextAnimator {
    /**
     * Start the animation.
     */
    void start();

    /**
     * Pause the text animation.
     * It usually for view losing the window focus or the application turn to the background.
     */
    void resume();
    /**
     * When the animation is pausing.
     * We could resume the animation when we got the window focus again.
     */
    void pause();

    /**
     * Cancel the animation.
     */
    void cancel();
}
