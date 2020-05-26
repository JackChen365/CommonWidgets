package com.cz.widgets.common.frame.transition;

import android.view.View;
import android.view.ViewGroup;

/**
 * @author Created by cz
 * @date 2020-05-22 21:12
 * @email bingo110@126.com
 */
public class DefaultFrameTransition extends FrameTransition{

    @Override
    public void appearingAnimator(ViewGroup parent, View child,int frameId) {
        child.setAlpha(0f);
        child.animate().alpha(1f);
    }

    @Override
    public void disappearingAnimator(ViewGroup parent, View child,int frameId) {
        child.animate().alpha(0f);
    }
}
