package com.cz.widgets.textview.span.animation.controller;

import android.graphics.Canvas;
import android.text.SpannedString;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.cz.widgets.textview.span.animation.AnimationTextSpan;
import com.cz.widgets.textview.span.animation.TextAnimator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author :Created by cz
 * @date 2019-05-15 21:36
 * @email bingo110@126.com
 * The text animation controller.
 *
 */
public abstract class AbsTextController {
    private TextView textView;
    private List<AnimationTextSpan> animationTextSpanList =new ArrayList<>();

    /**
     * Attach this controller to the text view.
     * @see #onAttachToView(TextView)
     * @param textView
     */
    public final void attachToTextView(TextView textView) {
        this.textView = textView;
        this.animationTextSpanList.clear();
        List<AnimationTextSpan> animationTextSpans = findAnimationTextSpanList();
        this.animationTextSpanList.addAll(animationTextSpans);
        onAttachToView(textView);
    }

    /**
     * When the controller attach to text view.
     * For a controller you could do something here.
     * @param textView
     */
    protected void onAttachToView(TextView textView){
    }

    protected TextView getTextView() {
        return textView;
    }

    /**
     * Refresh the host view which is the text view.
     */
    protected void invalidate(){
        if(null!= textView){
            textView.invalidate();
        }
    }

    private List<AnimationTextSpan> findAnimationTextSpanList(){
        List<AnimationTextSpan> elementSpans= Collections.emptyList();
        CharSequence text = textView.getText();
        if(!TextUtils.isEmpty(text) && text  instanceof SpannedString){
            final SpannedString spannableString=(SpannedString)text;
            AnimationTextSpan[] spans = spannableString.getSpans(0, text.length(), AnimationTextSpan.class);
            elementSpans = Arrays.asList(spans);
        }
        return elementSpans;
    }

    /**
     * Return the animation text span list.
     * We cached this list when the controller attached to the text view in order to save some memory.
     * @return the animation span list.
     */
    @NonNull
    protected List<AnimationTextSpan> getAnimationTextSpanList() {
        return animationTextSpanList;
    }

    /**
     * Calling this method from outside.
     */
    public final void prepare(){
        List<AnimationTextSpan> animationTextSpanList = getAnimationTextSpanList();
        //We loop the span list to reset the alpha of the animation.
        for(AnimationTextSpan animationTextSpan:animationTextSpanList){
            animationTextSpan.setAlpha(1f);
        }
        prepareAnimator(animationTextSpanList);
    }

    /**
     * This is for animation controller to preparing the animation
     * @param animationTextSpanList
     */
    protected void prepareAnimator(@NonNull List<AnimationTextSpan> animationTextSpanList){
    }

    /**
     * This is for animation controller to start the animation
     * which is where you define your own text animation.
     * @param animationTextSpanList
     */
    protected abstract void startAnimator(@NonNull List<AnimationTextSpan> animationTextSpanList);

    /**
     * Launch the text animation
     */
    public void start(){
        List<AnimationTextSpan> animationTextSpanList = getAnimationTextSpanList();
        startAnimator(animationTextSpanList);
    }

    /**
     * Pause the text animation.
     * It usually for view losing the window focus or the application turn to the background.
     */
    public final void pause(){
        List<AnimationTextSpan> animationTextSpanList = getAnimationTextSpanList();
        if(!animationTextSpanList.isEmpty()){
            for(AnimationTextSpan animationTextSpan:animationTextSpanList){
                TextAnimator animate = animationTextSpan.animate();
                if(null!=animate){
                    animate.pause();
                }
            }
        }
    }

    /**
     * When the animation is pausing.
     * We could resume the animation when we got the window focus again.
     */
    public final void resume(){
        List<AnimationTextSpan> animationTextSpanList = getAnimationTextSpanList();
        for(AnimationTextSpan animationTextSpan:animationTextSpanList){
            TextAnimator animate = animationTextSpan.animate();
            if(null!=animate){
                animate.resume();
            }
        }
    }

    /**
     * Cancel the animation.
     */
    public void cancel(){
        List<AnimationTextSpan> animationTextSpanList = getAnimationTextSpanList();
        for(AnimationTextSpan animationTextSpan:animationTextSpanList){
            animationTextSpan.reset();
            TextAnimator animate = animationTextSpan.animate();
            if(null!=animate){
                animate.cancel();
            }
        }
        invalidate();
    }

    protected int getWidth(){
        TextView textView = getTextView();
        int width=0;
        if(null!=textView){
            width=textView.getWidth();
        }
        return width;
    }

    protected int getHeight(){
        TextView textView = getTextView();
        int height=0;
        if(null!=textView){
            height=textView.getHeight();
        }
        return height;
    }

    /**
     * When you want to draw something that cooperates with your text replacement span.
     * You could do it here.
     * @param canvas
     */
    public void onDraw(@NonNull Canvas canvas){
    }
}
