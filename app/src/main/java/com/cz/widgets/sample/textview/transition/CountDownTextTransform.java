package com.cz.widgets.sample.textview.transition;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.cz.widgets.textview.span.animation.AnimationTextSpan;
import com.cz.widgets.textview.span.animation.TextSpanPropertyAnimator;
import com.cz.widgets.textview.span.transform.TextTransition;

/**
 * @author Created by cz
 * @date 2020-04-26 00:47
 * @email bingo110@126.com
 */
public class CountDownTextTransform implements TextTransition {
    private AnimationTextSpan[] oldTextSpanArray;

    @Override
    public void transform(TextView textView, Spanned newText, Spanned oldText) {
        oldTextSpanArray = oldText.getSpans(0, oldText.length(), AnimationTextSpan.class);
        AnimationTextSpan[] newTextSpanArray = newText.getSpans(0, newText.length(), AnimationTextSpan.class);
        int length = Math.max(oldTextSpanArray.length, newTextSpanArray.length);
        for(int i=0;i<length;i++){
            AnimationTextSpan oldAnimationTextSpan=null;
            if(i < oldTextSpanArray.length){
                oldAnimationTextSpan = oldTextSpanArray[i];
            }
            AnimationTextSpan newAnimationTextSpan=null;
            if(i < newTextSpanArray.length){
                newAnimationTextSpan = newTextSpanArray[i];
            }
            if(null!=oldAnimationTextSpan&&null!=newAnimationTextSpan){
                if(oldAnimationTextSpan.getWord()!=newAnimationTextSpan.getWord()){
                    transformOldSpan(oldAnimationTextSpan);
                    transformNewSpan(newAnimationTextSpan);
                }
            } else if(null!=oldAnimationTextSpan){
                transformOldSpan(oldAnimationTextSpan);
            } else if(null!=newAnimationTextSpan){
                transformNewSpan(newAnimationTextSpan);
            }
        }
    }

    private void transformNewSpan(AnimationTextSpan newAnimationTextSpan) {
        RectF newBounds = newAnimationTextSpan.getBounds();
        newAnimationTextSpan.setTranslationY(-newBounds.height());
        newAnimationTextSpan.setClipRect(newBounds, Region.Op.INTERSECT);
        TextSpanPropertyAnimator textSpanPropertyAnimator = newAnimationTextSpan.propertyAnimator();
        textSpanPropertyAnimator.translationY(0);
    }

    private void transformOldSpan(AnimationTextSpan oldAnimationTextSpan) {
        RectF oldBounds = oldAnimationTextSpan.getBounds();
        oldAnimationTextSpan.setClipRect(oldBounds, Region.Op.INTERSECT);
        oldAnimationTextSpan.setTranslationY(0);
        TextSpanPropertyAnimator textSpanPropertyAnimator = oldAnimationTextSpan.propertyAnimator();
        textSpanPropertyAnimator.translationY(oldBounds.height());
    }

    @Override
    public void onDraw(@NonNull TextView textView,@NonNull Canvas canvas, @NonNull Paint paint) {
        if(null!=oldTextSpanArray){
            for(AnimationTextSpan animationTextSpan:oldTextSpanArray){
                animationTextSpan.drawText(canvas,paint);
            }
        }
    }
}
