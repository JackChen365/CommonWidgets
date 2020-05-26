## The other TextView

* SelectTextView

![text_select](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/text_select.gif?raw=true)

This is a TextView it will highlight all the area you touched.

```
@Override
public boolean onTouchEvent(MotionEvent event) {
    int action = event.getActionMasked();
    float x = event.getX();
    float y = event.getY();
    if(MotionEvent.ACTION_DOWN==action){
        ViewParent parent = getParent();
        parent.requestDisallowInterceptTouchEvent(true);
        addSelection(x-getPaddingLeft(), y-getPaddingTop());
    } else if(MotionEvent.ACTION_MOVE==action){
        updateSelection(x-getPaddingLeft(), y-getPaddingTop());
    }
    return true;
}

private void addSelection(float x,float y) {
    Layout layout = getLayout();
    if (null != layout) {
        CharSequence text=getText();
        if(text instanceof Spannable &&null!=selectForegroundColorSpan){
            Spannable spannable = (Spannable) text;
            spannable.removeSpan(selectForegroundColorSpan);
        }
        int line = layout.getLineForVertical((int) y);
        selectStart = layout.getOffsetForHorizontal(line, x);
        layout.getSelectionPath(selectStart, selectStart + 1, selectPath);
        //reset the selected span object
        if(text instanceof Spannable){
            Spannable spannable = (Spannable) text;
            selectForegroundColorSpan=new ForegroundColorSpan(Color.WHITE);
            spannable.setSpan(selectForegroundColorSpan, selectStart, selectStart+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        invalidate();
    }
}

/**
 * Update the selected range
 */
private void updateSelection(float x,float y) {
    Layout layout=getLayout();
    if (null != layout) {
        CharSequence text = getText();
        if(text instanceof Spannable&&null!=selectForegroundColorSpan){
            Spannable spannable = (Spannable) text;
            spannable.removeSpan(selectForegroundColorSpan);
        }
        int line = layout.getLineForVertical((int) y);
        int off = layout.getOffsetForHorizontal(line, x);
        layout.getSelectionPath(selectStart, off, selectPath);

        if(text instanceof Spannable){
            Spannable spannable = (Spannable) text;
            selectForegroundColorSpan=new ForegroundColorSpan(Color.WHITE);
            int offset=Math.min(off,text.length());
            int start=Math.min(selectStart,offset);
            int end=Math.max(selectStart,offset);
            spannable.setSpan(selectForegroundColorSpan,start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        invalidate();
    }
}

```

* MarkedTextView

![text_mark](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/text_mark.gif?raw=true)

This is a markable TextView. If you understand how the TextView work correctly.
It's easily to have a markable View. After you touch in the TextView we use the point x/y to find the text line and the location in this line.

```
Layout layout = getLayout();
if (null != layout) {
    CharSequence text = layout.getText();
    int line = layout.getLineForVertical((int) y);
    int off = layout.getOffsetForHorizontal(line, x);
    ...
}
```


* The AnimationTextView

This is a TextView to support animation for each letter inside TextView.


```
@Override
public void setText(CharSequence text, BufferType type) {
    SpannableString newText = new SpannableString(text);
    //---------
    //Here we turn all the char to a AnimationTextSpan to support Animation.
    for(int i=0;i<newText.length();i++){
        AnimationTextSpan animationTextSpan = new AnimationTextSpan(this,text,i,i+1);
        animationTextSpan.setAlpha(0f);
        newText.setSpan(animationTextSpan,i,i+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }
    //---------

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
```

Know more detail you could see [TextAnimation](./text_animation.md)
