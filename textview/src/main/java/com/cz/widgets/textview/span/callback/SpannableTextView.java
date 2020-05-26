package com.cz.widgets.textview.span.callback;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author Created by cz
 * @date 2020-04-17 23:57
 * @email bingo110@126.com
 */
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
