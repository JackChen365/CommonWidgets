package com.cz.widgets.sample.view.frame;

import android.view.ViewGroup;

import com.cz.widgets.common.frame.AbsFrameWrapper;
import com.cz.widgets.sample.R;

/**
 * @author Created by cz
 * @date 2020-05-22 17:09
 * @email bingo110@126.com
 */
public class FrameWrapper extends AbsFrameWrapper {
    public static final int FRAME_CONTAINER=0;
    public static final int FRAME_PROGRESS=R.id.frameProgress;
    public static final int FRAME_EMPTY=R.id.frameEmpty;
    public static final int FRAME_ERROR=R.id.frameError;

    static{
        registerFrame(R.id.frameProgress, R.layout.view_frame_progress);
        registerFrame(R.id.frameEmpty, R.layout.view_frame_load_empty);
        registerFrame(R.id.frameError, R.layout.view_frame_load_error);
    }

    public FrameWrapper(ViewGroup hostView) {
        super(hostView, 0);
    }

    public FrameWrapper(ViewGroup hostView, int style) {
        super(hostView, style);
    }



}
