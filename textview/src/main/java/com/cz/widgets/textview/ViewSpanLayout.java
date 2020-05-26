package com.cz.widgets.textview;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * @author Created by cz
 * @date 2020-04-25 11:42
 * @email bingo110@126.com
 *
 * We use this view group to receive the drawable state changed event.
 * @see #childDrawableStateChanged(View)
 * @see #drawableStateChanged()
 * @see #refreshDrawableState()
 *
 */
public class ViewSpanLayout extends FrameLayout {
    private final TextView textView;

    public ViewSpanLayout(@NonNull Context context,@NonNull TextView textView) {
        super(context);
        this.textView=textView;
    }

    @Override
    public void childDrawableStateChanged(View child) {
        super.childDrawableStateChanged(child);
        invalidate();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    @Override
    public void refreshDrawableState() {
        super.refreshDrawableState();
        invalidate();
    }

    @Override
    public void invalidate() {
        textView.invalidate();
    }
}
