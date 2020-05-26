package com.cz.widgets.sample.textview.aniamtion.animator

import android.animation.ValueAnimator
import com.cz.widgets.textview.span.animation.AnimationTextSpan
import com.cz.widgets.textview.span.animation.controller.AbsTextController

/**
 * @author Created by cz
 * @date 2019-05-16 23:49
 * @email bingo110@126.com
 * The text loading controller.
 */
class TextLoad1Controller: AbsTextController() {

    override fun startAnimator(animationTextSpanList:List<AnimationTextSpan>) {
        animationTextSpanList.filter { it.word =='.' }.forEachIndexed { index, animationTextSpan ->
            val animate = animationTextSpan.propertyAnimator()
            animate.duration=600
            animate.startDelay=index*200L
            animate.repeatCount = ValueAnimator.INFINITE
            animate.repeatMode = ValueAnimator.REVERSE
            animate.translationY(8f)
            animate.start()
        }
    }
}