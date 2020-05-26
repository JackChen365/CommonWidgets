package com.cz.widgets.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.cz.widgets.textview.span.animation.AnimationTextSpan;
import com.cz.widgets.textview.span.animation.controller.AbsTextController;
import com.cz.widgets.textview.span.animation.controller.DefaultTextController;
import com.cz.widgets.textview.span.transform.TextTransition;

/**
 * @author Created by cz
 * @date 2019-05-15 23:33
 * @email bingo110@126.com
 *
 * The text animation view. This widget is for me to support all the animation features.
 * Take a look at {@link AnimationTextSpan} We turn all the character to a replacement span to support animation.
 * Then we abstract the text behavior as a text controller to let you totally manipulate all the animation span.
 *
 * We use {@link AbsTextController#prepare()}} to prepare the animation elements and call {@link AbsTextController#start()} to start the animation.
 *
 * About text transition. When you are changing the text from old text to new text. We could use text transition.
 * It is actually collect all the old text animation span and cooperates with the new text animation span.
 *
 * @see AbsTextController The text controller.
 * @see TextTransition The text transition.
 *
 */
public class AnimationTextView extends AppCompatTextView {
    /**
     * The flag to mark different drawings behavior.
     * Because the text animation should wait until the layout is drawing.
     * So when you are calling {@link #prepareAnimator()} We are not going to do it directly.
     * Instead of doing the method we actually mark a flag and call the method in the method {@link #onDraw(Canvas)}
     */
    private static final int FLAG_PREPARE=0x01;
    private static final int FLAG_ANIMATOR=0x02;
    private static final int FLAG_TRANSITION=0x04;
    /**
     * The text controller.
     */
    private AbsTextController textController;
    /**
     * The text transition.
     */
    private TextTransition textTransition;
    /**
     * When transform the text. We hold the old text here.
     */
    private CharSequence oldText;
    /**
     * All the drawing flag.
     */
    private int pendingFlag;

    public AnimationTextView(Context context) {
        this(context,null,0);
    }

    public AnimationTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AnimationTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        SpannableString newText = new SpannableString(text);
        for(int i=0;i<newText.length();i++){
            AnimationTextSpan animationTextSpan = new AnimationTextSpan(this,text,i,i+1);
            animationTextSpan.setAlpha(0f);
            newText.setSpan(animationTextSpan,i,i+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        //Invoke The setText method.
        CharSequence oldText = getText();
        this.oldText = oldText;
        if(null!=oldText&&null!=newText){
            pendingFlag|=FLAG_TRANSITION;
        }
        //Setting the new text.
        super.setText(newText, BufferType.NORMAL);
        //This method will be invoked from the superclass.
        //So no matter if we initialize this field in this class. It will throw a null pointer exception.
        if(null==textController){
            textController=new DefaultTextController();
        }
        this.textController.attachToTextView(this);
        this.prepareAnimator();
    }

    /**
     * Setting a new text transition
     * @param textTransition
     */
    public void setTextTransition(@Nullable TextTransition textTransition){
        this.textTransition=textTransition;
    }

    public TextTransition getTextTransition() {
        return textTransition;
    }

    /**
     * Setting a new text controller.
     * @param textController
     */
    public void setTextController(@NonNull final AbsTextController textController){
        if(null!=textController){
            this.textController = textController;
            this.textController.attachToTextView(this);
            this.prepareAnimator();
        }
    }

    /**
     * Prepare the animation before we launch the animation.
     */
    public void prepareAnimator(){
        pendingFlag|=FLAG_PREPARE;
        invalidate();
    }

    /**
     * Start the animation.
     */
    public void startAnimator(){
        pendingFlag|=FLAG_ANIMATOR;
        invalidate();
    }

    /**
     * When the window focus changed.
     * We pause the animation when we lost the window focus.
     * On the contrary, We resume the animation when we got the window focus again.
     * @param hasWindowFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        AbsTextController textController = getTextController();
        if(null!=textController){
            if(hasWindowFocus){
                textController.resume();
            } else {
                textController.pause();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        //Cancel the animation when the view detach from the window.
        AbsTextController textController = getTextController();
        if(null!=textController){
            textController.cancel();
        }
        super.onDetachedFromWindow();
    }

    public void cancelAnimator() {
        AbsTextController layoutController = getTextController();
        layoutController.cancel();
    }

    public AbsTextController getTextController() {
        return textController;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        AbsTextController textController = getTextController();
        //Prepare the text controller
        if(0!=(pendingFlag&FLAG_PREPARE)&&null!=textController){
            pendingFlag^=FLAG_PREPARE;
            textController.prepare();
        }
        //Start the the text controller
        if(0!=(pendingFlag&FLAG_ANIMATOR)&&null!=textController){
            pendingFlag^=FLAG_ANIMATOR;
            textController.start();
        }
        //Start the text transform.
        if(0!=(pendingFlag&FLAG_TRANSITION)){
            pendingFlag^=FLAG_TRANSITION;
            if(null!=textTransition && null!= oldText){
                CharSequence newText = getText();
                textTransition.transform(this, (Spanned) newText, (Spanned) oldText);
            }
        }
        //Drawing the text transition.
        drawTransformText(canvas,textTransition);
        //Drawing the text controller.
        drawTextController(canvas, textController);
    }

    /**
     * Drawing the text transition.
     * @param canvas
     * @param textTransform
     */
    private void drawTransformText(Canvas canvas,TextTransition textTransform) {
        if(null!=textTransform){
            canvas.save();
            int totalPaddingLeft = getTotalPaddingLeft();
            int totalPaddingTop = getTotalPaddingTop();
            canvas.translate(totalPaddingLeft,totalPaddingTop);
            TextPaint paint = getPaint();
            textTransform.onDraw(this,canvas,paint);
            canvas.restore();
        }
    }

    /**
     * Drawing the text controller.
     * @param canvas
     * @param textController
     */
    private void drawTextController(Canvas canvas, AbsTextController textController) {
        if(null!=textController){
            canvas.save();
            canvas.translate(getPaddingLeft(),getPaddingTop());
            textController.onDraw(canvas);
            canvas.restore();
        }
    }
}
