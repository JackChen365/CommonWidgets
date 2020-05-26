package com.cz.widgets.sample.textview.other;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.cz.widgets.sample.R;

/**
 * @author Created by cz
 * @date 2020-04-20 23:40
 * @email bingo110@126.com
 */
public class CountDownTextView extends AppCompatTextView {
    private final CountDownTextController textController;
    public CountDownTextView(Context context) {
        this(context,null,R.attr.countDownTextView);
    }

    public CountDownTextView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.countDownTextView);
    }

    public CountDownTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        textController=new CountDownTextController(context,attrs,defStyleAttr);
        textController.attachToTextView(this);
    }

    public void start(){
        textController.start();
    }

    public void cancel(){
        textController.cancel();
    }

    public void setOnCountDownListener(CountDownTextController.OnCountDownListener listener){
        this.textController.setOnCountDownListener(listener);
    }
}
