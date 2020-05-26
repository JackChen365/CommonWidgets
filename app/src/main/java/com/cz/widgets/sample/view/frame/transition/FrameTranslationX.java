package com.cz.widgets.sample.view.frame.transition;

import android.view.View;
import android.view.ViewGroup;

import com.cz.widgets.common.frame.transition.DefaultFrameTransition;
import com.cz.widgets.common.frame.transition.FrameTransition;
import com.cz.widgets.sample.view.frame.FrameWrapper;

/**
 * @author Created by cz
 * @date 2020-05-23 23:19
 * @email bingo110@126.com
 */
public class FrameTranslationX extends FrameTransition {

    @Override
    public void appearingAnimator(ViewGroup parent, View child, int frameId) {
        child.setTranslationX(parent.getWidth());
        child.animate().translationX(0);
    }

    @Override
    public void disappearingAnimator(ViewGroup parent, View child, int frameId) {
        child.animate().translationX(-parent.getWidth());
    }

    @Override
    public boolean playSequentially() {
        return false;
    }
}
