package com.cz.widgets.sample.textview.aniamtion.controller;

import androidx.annotation.NonNull;

import com.cz.widgets.textview.span.animation.AnimationTextSpan;
import com.cz.widgets.textview.span.animation.controller.AbsTextController;

import java.util.List;

/**
 * @author Created by cz
 * @date 2020-04-18 21:29
 * @email bingo110@126.com
 */
public class ScaleYTextController extends AbsTextController {
    @Override
    protected void prepareAnimator(@NonNull List<AnimationTextSpan> animationTextSpanList) {
        super.prepareAnimator(animationTextSpanList);
        for (int i=0;i<animationTextSpanList.size();i++) {
            AnimationTextSpan animationTextSpan = animationTextSpanList.get(i);
            animationTextSpan.setScaleY(1f);
        }
    }

    @Override
    public void startAnimator(List<AnimationTextSpan> animationTextSpanList) {
        for (int i=0;i<animationTextSpanList.size();i++) {
            AnimationTextSpan animationTextSpan = animationTextSpanList.get(i);
            animationTextSpan.propertyAnimator().scaleY(1.4f);
        }
    }
}
