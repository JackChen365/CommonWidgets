package com.cz.widgets.textview.span.click;

import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * @author Created by cz
 * @date 2019-05-14 20:12
 * @email bingo110@126.com
 *
 * This object allow the span receive touch event.
 * It is seem like the {@link LinkMovementMethod }
 * You should use TextView#setMovementMethod(MovementMethod)
 * @see TouchableReplacementSpan we will be dispaching all the event to this ReplacementSpan.
 */
public class TouchableMovementMethod extends LinkMovementMethod {
    /**
     * The singleton object
     */
    private static TouchableMovementMethod sInstance;

    /**
     * Return the singleton object
     *
     * Be careful if you are using the MovementMethod inside another text view. It may cause the StackOverflowError.
     * 2020-04-27 17:03:08.013 9291-9291/com.cz.widgets.sample E/MessageQueue-JNI: java.lang.StackOverflowError: stack size 8MB
     *         at android.text.SpannableStringInternal.getSpans(SpannableStringInternal.java:356)
     *         at android.text.SpannableString.getSpans(SpannableString.java:24)
     *         at android.text.SpanSet.init(SpanSet.java:47)
     *         at android.text.TextLine.set(TextLine.java:169)
     *         at android.text.Layout.getHorizontal(Layout.java:1199)
     *         at android.text.Layout.getHorizontal(Layout.java:1178)
     *         at android.text.Layout.getPrimaryHorizontal(Layout.java:1149)
     *         at android.text.Layout.getPrimaryHorizontal(Layout.java:1139)
     *         at android.text.Layout.getHorizontal(Layout.java:1172)
     *         at android.text.Layout.getOffsetForHorizontal(Layout.java:1537)
     *         at android.text.Layout.getOffsetForHorizontal(Layout.java:1480)
     *         at com.cz.widgets.textview.span.click.TouchableMovementMethod.getTouchedOffset(TouchableMovementMethod.java:85)
     *         at com.cz.widgets.textview.span.click.TouchableMovementMethod.getTouchedSpan(TouchableMovementMethod.java:99)
     *         at com.cz.widgets.textview.span.click.TouchableMovementMethod.onTouchEvent(TouchableMovementMethod.java:42)
     */
    public static MovementMethod getInstance() {
        if (sInstance == null)
            sInstance = new TouchableMovementMethod();

        return sInstance;
    }

    private TouchableReplacementSpan touchedTarget;

    @Override
    public boolean onTouchEvent(final TextView textView, final Spannable spannable, MotionEvent event) {
        int action = event.getActionMasked();
        TouchableReplacementSpan touchableReplacementSpan = getTouchedSpan(textView, spannable, event);
        if(null!=touchableReplacementSpan){
            if (action == MotionEvent.ACTION_DOWN) {
                touchedTarget=touchableReplacementSpan;
            } else if(null!=touchedTarget&&touchedTarget!=touchableReplacementSpan){
                //When the finger keep moving. It may be changing the span.
                MotionEvent newEvent = MotionEvent.obtain(event);
                newEvent.setAction(MotionEvent.ACTION_CANCEL);
                touchedTarget.onTouchEvent(textView,newEvent);
                touchedTarget=touchableReplacementSpan;
            }
            touchableReplacementSpan.onTouchEvent(textView,event);
        } else if(action==MotionEvent.ACTION_UP||action==MotionEvent.ACTION_CANCEL){
            if(null!=touchedTarget){
                MotionEvent newEvent = MotionEvent.obtain(event);
                newEvent.setAction(MotionEvent.ACTION_CANCEL);
                touchedTarget.onTouchEvent(textView,newEvent);
                touchedTarget=null;
            }
        }
        return true;
    }

    /**
     * Return the touch offset position.
     * @param textView
     * @param event
     * @return
     */
    private int getTouchedOffset(TextView textView, MotionEvent event){
        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= textView.getTotalPaddingLeft();
        y -= textView.getTotalPaddingTop();

        x += textView.getScrollX();
        y += textView.getScrollY();

        final Layout layout = textView.getLayout();
        int line = layout.getLineForVertical(y);
        int off=0;
        try {
            off=layout.getOffsetForHorizontal(line, x*1f);
        } catch (IndexOutOfBoundsException e) {
        }
        return off;
    }

    /**
     * Getting the touched span object from the touch event.
     * @param widget
     * @param spannable
     * @param event
     * @return
     */
    private TouchableReplacementSpan getTouchedSpan(TextView widget, Spannable spannable, MotionEvent event){
        int touchedOffset = getTouchedOffset(widget, event);
        TouchableReplacementSpan[] spans = spannable.getSpans(touchedOffset, touchedOffset, TouchableReplacementSpan.class);
        if (0 < spans.length){
            return spans[0];
        }
        return null;
    }
}
