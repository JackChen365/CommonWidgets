package com.cz.widgets.sample.textview.aniamtion.controller;

import android.graphics.RectF;
import android.graphics.Region;

import androidx.annotation.NonNull;

import com.cz.widgets.textview.span.animation.controller.AbsTextController;
import com.cz.widgets.textview.span.animation.AnimationTextSpan;

import java.util.List;

/**
 * @author Created by cz
 * @date 2020-04-18 21:29
 * @email bingo110@126.com
 */
public class TransitionXTextController extends AbsTextController {
    @Override
    protected void prepareAnimator(@NonNull List<AnimationTextSpan> animationTextSpanList) {
        super.prepareAnimator(animationTextSpanList);
        for (int i=0;i<animationTextSpanList.size();i++) {
            AnimationTextSpan animationTextSpan = animationTextSpanList.get(i);
            RectF bounds = animationTextSpan.getBounds();
            animationTextSpan.setClipRect(bounds, Region.Op.INTERSECT);
            animationTextSpan.setTranslationX(-bounds.width());
        }
    }

    @Override
    public void startAnimator(List<AnimationTextSpan> animationTextSpanList) {
        for (int i=0;i<animationTextSpanList.size();i++) {
            AnimationTextSpan animationTextSpan = animationTextSpanList.get(i);
            animationTextSpan.propertyAnimator().translationX(0);
        }
    }
}
