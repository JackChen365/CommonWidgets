package com.cz.widgets.sample.view.frame.transition;

import android.view.View;
import android.view.ViewGroup;

import com.cz.widgets.common.frame.transition.DefaultFrameTransition;
import com.cz.widgets.sample.view.frame.FrameWrapper;

/**
 * @author Created by cz
 * @date 2020-05-23 23:19
 * @email bingo110@126.com
 */
public class ContentFrameTransition extends DefaultFrameTransition {

    @Override
    public void appearingAnimator(ViewGroup parent, View child, int frameId) {
        if(FrameWrapper.FRAME_CONTAINER!=frameId){
            super.appearingAnimator(parent, child, frameId);
        }
    }

    @Override
    public void disappearingAnimator(ViewGroup parent, View child, int frameId) {
        if(FrameWrapper.FRAME_CONTAINER!=frameId){
            super.disappearingAnimator(parent, child, frameId);
        }
    }

    @Override
    public void onFrameDisappear(View child, int frameId) {
        if(FrameWrapper.FRAME_CONTAINER!=frameId){
            super.onFrameDisappear(child, frameId);
        }
    }
}
