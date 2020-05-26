package com.cz.widgets.textview.span.click;

import android.text.style.ReplacementSpan;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * The touchable replacement span.
 * @see android.text.style.ClickableSpan
 * @see TouchableMovementMethod this movement method will dispatch all the event to the ReplacementSpan.
 */
public abstract class TouchableReplacementSpan extends ReplacementSpan {
    /**
     * This method will receive all the touch events from the TextView.
     * @param textView
     * @param event
     */
    public abstract void onTouchEvent(final TextView textView, MotionEvent event);
}
