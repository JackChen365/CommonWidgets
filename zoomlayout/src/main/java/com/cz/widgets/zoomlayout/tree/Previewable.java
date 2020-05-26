package com.cz.widgets.zoomlayout.tree;

import android.graphics.Canvas;
import android.view.View;

/**
 * @author Created by cz
 * @date 2020-05-12 20:08
 * @email bingo110@126.com
 */
public interface Previewable {
    View newPreview();
    void onChildChange(Canvas canvas, View child);
}
