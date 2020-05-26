package com.cz.widgets.zoomlayout.theater;

import android.view.View;
import android.view.ViewGroup;

import com.cz.widgets.zoomlayout.ZoomLayout;

/**
 * The table data adapter.
 */
public abstract class TableAdapter {
    /**
     * Create a new view with a specific view type.
     */
    public abstract View onCreateView(ViewGroup parent,int viewType);
    /**
     * Binding data to the view.
     * @param parent
     * @param view
     * @param row
     * @param column
     */
    public abstract void onBindView(ZoomLayout parent, View view, int row, int column);

    public int getViewType(int row,int column){
        return 0;
    }
    /**
     * Return the size of the row of the table.
     */
    public abstract int getRowCount();
    /**
     * Return the size of the column of the table.
     */
    public abstract int getColumnCount();
    /**
     * Return the extra space of this column
     */
    public abstract int getHorizontalSpacing(int column);
    /**
     * Return the extra space of this row
     */
    public abstract int getVerticalSpacing(int row);

    /**
     * If the specific cell is enabled.
     */
    public boolean isDisable(int row, int column){
        return true;
    }
}