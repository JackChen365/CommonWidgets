package com.cz.widgets.sample.zoomlayout.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.cz.widgets.sample.R;
import com.cz.widgets.zoomlayout.ItemDecoration;

/**
 * @author Created by cz
 * @date 2020-05-07 20:02
 * @email bingo110@126.com
 */
public class TableDecoration extends ItemDecoration {
    private Drawable tableDivider;
    public TableDecoration(Context context){
        tableDivider = ContextCompat.getDrawable(context, R.drawable.zoom_table_divider);
    }

    @Override
    public void onDraw(@NonNull Canvas c, View child, Rect insetRect, float scaleX, float scaleY) {
        super.onDraw(c, child, insetRect, scaleX, scaleY);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, View child, Rect outRect, float scaleX, float scaleY) {
        super.onDrawOver(c, child, outRect, scaleX, scaleY);
        int intrinsicWidth = Math.round(tableDivider.getIntrinsicWidth()*scaleX);
        int intrinsicHeight = Math.round(tableDivider.getIntrinsicHeight()*scaleY);
        int left = Math.round(child.getLeft()*scaleX);
        int top = Math.round(child.getTop()*scaleY);
        int right = Math.round(child.getRight()*scaleX);
        int bottom = Math.round(child.getBottom()*scaleY);
        //The left side.
        tableDivider.setBounds(left-intrinsicWidth, top-intrinsicHeight, left, bottom+intrinsicHeight);
        tableDivider.draw(c);

        //The top side.
        tableDivider.setBounds(left-intrinsicWidth, top-intrinsicHeight, right+intrinsicWidth, top);
        tableDivider.draw(c);

        //The right side.
        tableDivider.setBounds(right, top-intrinsicHeight, right+intrinsicWidth, bottom+intrinsicHeight);
        tableDivider.draw(c);

        //The bottom side.
        tableDivider.setBounds(left-intrinsicWidth, bottom, right+intrinsicWidth, bottom+intrinsicHeight);
        tableDivider.draw(c);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, View item) {
        super.getItemOffsets(outRect, item);
        int intrinsicWidth = tableDivider.getIntrinsicWidth();
        int intrinsicHeight = tableDivider.getIntrinsicHeight();
        outRect.set(intrinsicWidth,intrinsicHeight,intrinsicWidth,intrinsicHeight);
    }
}
