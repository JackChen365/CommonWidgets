## TextAnimation

> Trying to support text animation is not an easy work. I've tryed a few times.
But there are few things you should know.

* What kind of animation we should have?
* The location of each letters.
* How to organize all the text animation together or sequentially.


About the first question.

I actually try to calculate all the letter all at once. It turns out it is not a good idea.
By changing all the letter to a AnimationTextSpan. We could let the TextView calculate the letter for us.

```
paint.getTextBounds(text.toString(),start,end,textBounds);
if(null!=fm){
    fm.top = textBounds.top;
    fm.ascent = textBounds.top;
    fm.descent = textBounds.bottom;
    fm.bottom = textBounds.bottom;
}
return (int) paint.measureText(text, start, end);
```

The performance consumption was we calculate the letters one by one.
But for a AnimationTextView we usually don't have too much letters.

This is where we draw the letter.

```
@Override
public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float offsetX, int top, int offsetY, int bottom, @NonNull Paint paint) {
    //Update the span bounds location.
    RectF bounds = propertyHolder.getBounds();
    float textWidth = paint.measureText(text, start, end);
    bounds.set(offsetX+textBounds.left,
            offsetY+textBounds.top,
            offsetX+textBounds.left+textWidth,
            offsetY+textBounds.top+textBounds.height());
    //Drawing the text information.
    drawText(canvas,paint);
}
```


I've tried to use the RenderNode directly for each letters.
It will make the text animation just like the View. But because of the version limitation of this Class. I can not do that.


This is Animation property holder.

I was play to build a object pool for this Class.

```
public class TextPropertyHolder {
    private final RectF bounds =new RectF();
    private float alpha=1f;
    private float translationX;
    private float translationY;
    private float scaleX=1f;
    private float scaleY=1f;
    private float rotate=0f;
}
```

This is all the animations the the Text.

![text_animation](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/text_animation.gif?raw=true)

By implementing your own TextController you are able to do any text animations.

This is what I have done.

* ![](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/text_controller.gif?raw=true)

* ![](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/text_flying.gif?raw=true)

* ![](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/text_typing.gif?raw=true)

That all.




