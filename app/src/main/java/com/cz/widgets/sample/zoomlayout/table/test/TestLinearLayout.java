package com.cz.widgets.sample.zoomlayout.table.test;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.LinearLayoutCompat;

/**
 * @author Created by cz
 * @date 2020-05-06 18:27
 * @email bingo110@126.com
 */
public class TestLinearLayout extends LinearLayoutCompat {
    private static final String TAG="TestLinearLayout";
    public TestLinearLayout(Context context) {
        super(context);
    }

    public TestLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
