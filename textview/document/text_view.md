## View span

This is a view span. We actually could draw any views in a ReplacementSpan. Like Table or Image to support other functions.

#### Pictures

![](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/text_span_table.gif?raw=true)

![](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/text_span_view.gif?raw=true)


#### Usage

The TouchableViewSpan/ClickableViewSpan:
```
val tableLayout= TableLayout(this)
tableLayout.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
if (MATCH_PARENT == tableSizeMode) {
    //fill the content
    tableLayout.layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT
} else {
    //wrap the content.
    tableLayout.layoutParams.width=ViewGroup.LayoutParams.WRAP_CONTENT
}
val tableSpan = TouchableViewSpan(textView, tableLayout)
spannableString.setSpan(tableSpan, index, index + 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
```


* The difference between TouchableViewSpan and ClickableViewSpan.
The TouchableViewSpan is I will dispatch all the touch events to this span. So you could handle the events for the more complicated gestures.
The ClickableViewSpan is only for the view to handle the click event.


#### Questions
* The different between ClickableViewSpan and TouchableViewSpan.
They actually do the same thing. Draw the view in the ReplacementSpan. But how can we handle the touch event?
The ClickableViewSpan usually handles the view that does not have to handle the drawable status. We handle that for you.
For the TouchableViewSpan is for us to dispatch the touch event. We imitate the event like a normal view in the hierarchy view tree.
We do that by using an interface:SpannableComponent

```
/**
 * The spannable component. It's for the TextView to support the span invalidate the rect and do something relate to View
 */
public interface SpannableComponent {
    /**
     * Attach to the textView.
     */
    void attachToView(TextView hostView);
}
```


We use this component to do everything we do in the ViewGroup.
Then we could dispatch all the touch event.

```
public class SpannableTextView extends AppCompatTextView implements SpannableComponent {
    private View hostView;

    public SpannableTextView(Context context) {
        super(context);
    }

    public SpannableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void attachToView(TextView hostView) {
        this.hostView=hostView;
    }

    @Override
    public void invalidate() {
        if(null!=hostView){
            hostView.invalidate();
        } else {
            super.invalidate();
        }
    }

    @Override
    public void postInvalidate() {
        if(null!=hostView){
            hostView.postInvalidate();
        } else {
            super.postInvalidate();
        }
    }

    /**
     * To support those functions.
     * View#PerformClick
     * View#UnsetPressedState
     * View#CheckForTap
     * View#CheckForLongPress
     *
     * @param action
     * @return
     */
    @Override
    public boolean post(Runnable action) {
        if(null!=hostView){
            return hostView.post(action);
        } else {
            return super.post(action);
        }
    }


    @Override
    public boolean postDelayed(Runnable action, long delayMillis) {
        if(null!=hostView){
            return hostView.postDelayed(action, delayMillis);
        } else {
            return super.postDelayed(action,delayMillis);
        }
    }

    @Override
    public boolean removeCallbacks(Runnable action) {
        if(null!=hostView){
            return hostView.removeCallbacks(action);
        } else {
            return super.removeCallbacks(action);
        }
    }

    @Override
    public void postOnAnimation(Runnable action) {
        if(null!=hostView){
            hostView.postOnAnimation(action);
        } else {
            super.postOnAnimation(action);
        }
    }

    @Override
    public void postInvalidateOnAnimation() {
        if(null!=hostView){
            hostView.postInvalidateOnAnimation();
        } else {
            super.postInvalidateOnAnimation();
        }
    }

    @Override
    public void requestLayout() {
        if(null!=hostView){
            hostView.requestLayout();
        } else {
            super.requestLayout();
        }
    }
}
```

As you can see. We use text view do all the things. Under this circumstance you could dispatch touch event.

Notice:
We have to override post or postDelayed to support the message queue inside the View.

* View#PerformClick
* View#UnsetPressedState
* View#CheckForTap
* View#CheckForLongPress

This all depends on the message queue.


