package com.cz.widgets.sample.view.state;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.cz.widgets.sample.R;

/**
 * @author Created by cz
 * @date 2020-01-27 18:59
 * @email bingo110@126.com
 */
public class CheckedLayout extends LinearLayout {

    private OnCheckedChangeListener listener;

    public CheckedLayout(Context context) {
        this(context,null);
    }

    public CheckedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CheckedLayout);
        setCheckedArray(a.getTextArray(R.styleable.CheckedLayout_checked_array));
        a.recycle();
    }

    /**
     * set check group array.
     * @param textArray
     */
    public void setCheckedArray(CharSequence[] textArray) {
        removeAllViews();
        Context context = getContext();
        if(null!=textArray){
            for(int i=0;i<textArray.length;i++){
                CharSequence text=textArray[i];
                CheckBox checkBox = new CheckBox(context);
                checkBox.setId(i);
                checkBox.setText(text);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(null!=listener){
                            int index = indexOfChild(compoundButton);
                            listener.onCheckedChanged(compoundButton,index,b);
                        }
                    }
                });
                addView(checkBox);
            }
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener){
        this.listener=listener;
    }

    /**
     * interface responsible for receiving compoundButton's check change event
     */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(CompoundButton compoundButton, int index, boolean isChecked);
    }
}
