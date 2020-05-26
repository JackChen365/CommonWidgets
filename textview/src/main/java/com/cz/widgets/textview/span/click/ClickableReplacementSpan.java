package com.cz.widgets.textview.span.click;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * Created by cz
 * @date 2020-04-01 23:12
 * @email bingo110@126.com
 * We combine the Clickable object with the replacement span.
 * So the sub-classes support both view and click behavior.
 * @see android.text.style.ClickableSpan
 */
public abstract class ClickableReplacementSpan extends TouchableReplacementSpan {
    private static int sIdCounter = 0;
    private int mId = sIdCounter++;
    private View.OnClickListener listener;
    private View.OnLongClickListener longClickListener;

    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    /**
     * Change the long click listener.
     * @param listener
     */
    public void setOnLongClickListener(View.OnLongClickListener listener){
        this.longClickListener=listener;
    }
    /**
     * Performs the click action associated with this span.
     */
    public void performClick(@NonNull View widget) {
        if(null!=listener){
            listener.onClick(widget);
        }
    }

    public void setPressed(boolean pressed) {
    }

    public void setEnabled(boolean enabled) {
    }

    public void setChecked(boolean checked){
    }

    public void setActivated(boolean activated) {
    }

    public void setSelected(boolean selected){
    }

    public boolean isPressed(){
        return false;
    }

    public boolean isEnabled(){
        return false;
    }

    public boolean isChecked(){
        return false;
    }

    public boolean isActivated(){
        return false;
    }

    public boolean isSelected(){
        return false;
    }

    /**
     * Set long click event
     * @param textView
     */
    public void performLongClick(TextView textView) {
        if(null!=longClickListener){
            longClickListener.onLongClick(textView);
        }
    }

    /**
     * set id to this span
     * @param id
     */
    public void setId(int id){
        this.mId=id;
    }

    /**
     * Get the unique ID for this span.
     *
     * @return The unique ID.
     * @hide
     */
    public int getId() {
        return mId;
    }
}
