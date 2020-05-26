package com.cz.textview.sample.ui.aniamtion.animator

import android.animation.ValueAnimator
import com.cz.widgets.textview.span.animation.AnimationTextSpan
import com.cz.widgets.textview.span.animation.controller.AbsTextController

/**
 * @author Created by cz
 * @date 2019-05-16 22:49
 * @email bingo110@126.com
 * The text loading controller.
 */
class TextLoad2Controller: AbsTextController() {

    override fun prepareAnimator(animationTextSpanList: MutableList<AnimationTextSpan>) {
        super.prepareAnimator(animationTextSpanList)
        animationTextSpanList.filter { it.word =='.' }.forEach { animationTextSpan ->
            animationTextSpan.alpha=0f
        }
    }

    override fun startAnimator(animationTextSpanList:List<AnimationTextSpan>) {
        animationTextSpanList.filter { it.word =='.' }.forEachIndexed { index, animationTextSpan ->
            val animator = animationTextSpan.propertyAnimator()
            animator.alpha(1f)
            animator.duration=600
            animator.startDelay=index*200L
            animator.repeatCount=ValueAnimator.INFINITE
            animator.repeatMode= ValueAnimator.REVERSE
            animator.start()
        }
    }
}