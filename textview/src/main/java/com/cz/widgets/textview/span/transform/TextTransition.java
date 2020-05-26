package com.cz.widgets.textview.span.transform;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Spanned;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * @author Created by cz
 * @date 2020-04-26 00:37
 * @email bingo110@126.com
 * The text transition. It is for a text view. When you change the text.
 * It does not just display the text in the view.
 * By using this interface we allow you to transform the text from old to the new text.
 *
 * Here is the code how I support the text transform.
 * <pre>
 *     @Override
 *     public void setText(CharSequence text, BufferType type) {
 *         SpannableString newText = new SpannableString(text);
 *         for(int i=0;i<newText.length();i++){
 *             AnimationTextSpan animationTextSpan = new AnimationTextSpan(this,text,i,i+1);
 *             animationTextSpan.setAlpha(0f);
 *             newText.setSpan(animationTextSpan,i,i+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
 *         }
 *         //Invoke The setText method.
 *         CharSequence oldText = getText();
 *         this.oldText = oldText;
 *         if(null!=oldText&&null!=newText){
 *             pendingFlag|=FLAG_TRANSITION;
 *         }
 *         //Setting the new text.
 *         super.setText(newText, BufferType.NORMAL);
 *         //This method will be invoked from the superclass.
 *         //So no matter if we initialize this field in this class. It will throw a null pointer exception.
 *         if(null==textController){
 *             textController=new DefaultTextController();
 *         }
 *         this.textController.attachToTextView(this);
 *         this.prepareAnimator();
 *     }
 * </pre>
 *
 */
public interface TextTransition {

    /**
     * Transform the text form old text to new text.
     * @param textView the text view.
     * @param newText the new text spannable.
     * @param oldText the old text spannable.
     */
    void transform(TextView textView, Spanned newText, Spanned oldText);

    /**
     * Drawing something when the text transform from old text to new text.
     * @param textView
     * @param canvas
     * @param paint
     */
    void onDraw(@NonNull TextView textView, @NonNull Canvas canvas, @NonNull Paint paint);
}
