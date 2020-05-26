package com.cz.widgets.common.frame.listener;

import android.view.View;

import com.cz.widgets.common.frame.AbsFrameWrapper;

/**
 * @author Created by cz
 * @date 2020-05-22 13:26
 * @email bingo110@126.com
 */
public interface OnLayoutFrameChangeListener {

    void onFrameShown(AbsFrameWrapper frameWrapper, int id, View view);

    void onFrameHidden(AbsFrameWrapper frameWrapper, int id, View view);
}
