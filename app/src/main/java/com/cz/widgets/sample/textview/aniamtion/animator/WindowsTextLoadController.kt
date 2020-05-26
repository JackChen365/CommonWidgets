package com.cz.textview.sample.ui.aniamtion.animator

import android.animation.Keyframe
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import com.cz.widgets.textview.span.animation.AnimationTextSpan
import com.cz.widgets.textview.span.animation.controller.AbsTextController

/**
 * @author Created by cz
 * @date 2019-05-16 22:41
 * @email bingo110@126.com
 * The text loading animation imitates the windows system loading animation.
 */
class WindowsTextLoadController: AbsTextController() {

    override fun prepareAnimator(animationTextSpanList: MutableList<AnimationTextSpan>) {
        super.prepareAnimator(animationTextSpanList)
        animationTextSpanList.forEach{ animationTextSpan->
            val bounds = animationTextSpan.bounds
            animationTextSpan.x = -bounds.width()
        }
        invalidate()
    }

    override fun startAnimator(animationTextSpanList:List<AnimationTextSpan>) {
        animationTextSpanList.forEachIndexed{ index,animationTextSpan->
            val bounds = animationTextSpan.bounds
            val left=bounds.left
            val frame1= Keyframe.ofFloat(0f,-bounds.width())
            val frame2= Keyframe.ofFloat(0.2f,left-60)
            val frame3= Keyframe.ofFloat(0.8f,left+60)
            val frame4= Keyframe.ofFloat(1.0f, left+width)

            val objectAnimator = animationTextSpan.objectAnimator()
            objectAnimator.setValues(PropertyValuesHolder.ofKeyframe("x", frame1, frame2, frame3,frame4))
            objectAnimator.interpolator= LinearInterpolator()
            objectAnimator.duration=4000
            objectAnimator.startDelay=(animationTextSpanList.size-index)*100L
            objectAnimator.repeatCount=ValueAnimator.INFINITE
            objectAnimator.repeatMode= ValueAnimator.RESTART
            objectAnimator.start()
        }
    }
}