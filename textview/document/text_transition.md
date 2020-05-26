## Drawable span

The text transition is when you re-set text into the widget. How we transform the text.

The picture above show you how we transform the text.

![text_transition](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/text_transition.gif?raw=true)


#### Usage

```
//In XML file.
<com.cz.widgets.textview.AnimationTextView
        android:id="@+id/textView"
        android:layout_width="match_parent"
        android:layout_height="120dp"
        android:layout_gravity="center_horizontal"
        android:textColor="@android:color/white"
        android:textSize="64sp"
        android:gravity="center"
        android:background="@color/colorPrimaryDark"/>

//In your code.
textView.textTransition = CountDownTextTransform()
```

#### The interface prototype

```
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
```

#### How to implement your own text transition

```
@Override
public void transform(TextView textView, Spanned newText, Spanned oldText) {
    //Holding the old text span array
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
            //Transform both old character and the new character.
            if(oldAnimationTextSpan.getWord()!=newAnimationTextSpan.getWord()){
                transformOldSpan(oldAnimationTextSpan);
                transformNewSpan(newAnimationTextSpan);
            }
        } else if(null!=oldAnimationTextSpan){
            //Transform old character and the new character.
            transformOldSpan(oldAnimationTextSpan);
        } else if(null!=newAnimationTextSpan){
            //Transform new character and the new character.
            transformNewSpan(newAnimationTextSpan);
        }
    }
}
```

Afterward you could draw the old text span object.
Because we already changed the text in the TextView. It just not exist anymore. Here we draw the span ourselves to make it looks like a transition process

```
 @Override
public void onDraw(@NonNull TextView textView,@NonNull Canvas canvas, @NonNull Paint paint) {
    if(null!=oldTextSpanArray){
        for(AnimationTextSpan animationTextSpan:oldTextSpanArray){
            animationTextSpan.drawText(canvas,paint);
        }
    }
}
```


That's all.
