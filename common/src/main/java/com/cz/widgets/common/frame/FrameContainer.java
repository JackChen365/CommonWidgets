package com.cz.widgets.common.frame;

import android.view.View;

import androidx.annotation.IdRes;

/**
 * @author Created by cz
 * @date 2020-05-22 23:04
 * @email bingo110@126.com
 * The interface is for any View to customize their own frame layout.
 *
 * @see AbsFrameWrapper
 * @see FrameViewLayout the implementation for the interface.
 */
public interface FrameContainer {
    /**
     * Return true this mean you want to handle this frame view.
     * @param id
     * @return
     */
    boolean applyFrame(@IdRes int id);

    /**
     * Return the content view inside.
     * @return
     */
    View getContentView();
}
