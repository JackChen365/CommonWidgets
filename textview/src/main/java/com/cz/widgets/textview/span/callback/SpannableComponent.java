package com.cz.widgets.textview.span.callback;

import android.widget.TextView;

/**
 * Created by cz
 * @date 2020-04-01 21:01
 * @email bingo110@126.com
 * The spannable component. It's for the TextView to support the span invalidate the rect and do something relate to View
 */
public interface SpannableComponent {
    /**
     * Attach to the textView.
     */
    void attachToView(TextView hostView);
}
