package com.cz.widgets.common.frame.transition;

import android.view.View;
import android.view.ViewGroup;

/**
 * @author Created by cz
 * @date 2020-05-22 11:12
 * @email bingo110@126.com
 *
 * The frame transition
 * When the view change its frame from one to another. If you want to changing with an animation.
 * This is for you.
 *
 * We use {@link android.view.ViewPropertyAnimator} to simplify the animation.
 * You use childView.animate() to deploy your animation I could call this function to change the animation at the same time.
 * Because it is the same object.
 * <pre>
 *     public ViewPropertyAnimator animate() {
 *         if (mAnimator == null) {
 *             mAnimator = new ViewPropertyAnimator(this);
 *         }
 *         return mAnimator;
 *     }
 * </pre>
 *
 * @see com.cz.widgets.common.frame.AbsFrameWrapper#setFrameTransition(FrameTransition)
 */
public abstract class FrameTransition {

    /**
     * When the frame view is going to appearing. Here you could play an appearing animation.
     * @param parent
     * @param child
     * @param frameId
     */
    public abstract void appearingAnimator(ViewGroup parent, View child,int frameId);

    /**
     * When the frame view is going to disappearing. Here you could play a disappearing animation.
     * @param parent
     * @param child
     * @param frameId
     */
    public abstract void disappearingAnimator(ViewGroup parent, View child,int frameId);

    /**
     * Play the animation sequentially.
     * If this method return true.
     * This means I will first play the disappearing animation after that play the appearing animation.
     * Otherwise I will play all the animations together.
     * @return
     */
    public boolean playSequentially(){
        return true;
    }

    public void onFrameDisappear(View child,int frameId){
        child.setVisibility(View.INVISIBLE);
    }
}
