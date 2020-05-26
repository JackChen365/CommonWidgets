package com.cz.widgets.zoomlayout.theater;

import android.util.SparseIntArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cz.widgets.zoomlayout.tree.TreeNode;

/**
 * @author Created by cz
 * @date 2020-05-09 22:53
 * @email bingo110@126.com
 *
 * The table location indexer.
 *
 * When you are scroll on the screen. You could use the location:x/y to return the specific table cell.
 * We do not just traverse all the table cell to check if the table cell contain the point.
 * But calculate an table instead.
 *
 * Just imaging your table. Each cell has its own row and column.
 * We will build a table by the node's row and column.
 *
 *
 * For example:
 * The location could be: 35x35. Each cell's side could be:20x20
 * The table we build are:
 * depth horizontally:[20,40,60]
 * vertically:[20,40,60,80]
 * So for location:35x35. First we check the point:x is 35. This actually in position 1, and the point:y is the same.
 * The node would be the one who is row:1 and row:1.
 */
public class TableIndexer {
    private final SparseIntArray tableRowArray;
    private final SparseIntArray tableColumnArray;
    private int measuredWidth;
    private int measuredHeight;

    public TableIndexer(@NonNull TableAdapter adapter, int nodeWidth, int nodeHeight) {
        tableRowArray =new SparseIntArray();
        tableColumnArray =new SparseIntArray();
        //Traverse all the table cells to build an indexer.
        traversalTable(adapter,nodeWidth,nodeHeight);
    }

    private void traversalTable(TableAdapter adapter, int tableCellWidth, int tableCellHeight) {
        int rowCount = adapter.getRowCount();
        int columnCount = adapter.getColumnCount();
        if(0 == rowCount||0 == columnCount){
            throw new IllegalArgumentException("The table data is invalid. Please check your row size or column size!");
        }
        //We calculate the row of the table.
        int topOffset=0;
        tableRowArray.append(0,topOffset);
        for(int row=1; row<=rowCount; row++){
            int spacing = adapter.getVerticalSpacing(row);
            topOffset+=tableCellHeight;
            if(0 != row){
                topOffset+=spacing;
            }
            tableRowArray.append(row,topOffset);
        }
        //We calculate the column of the table.
        int leftOffset=0;
        tableColumnArray.append(0,leftOffset);
        for(int column=1; column<=columnCount; column++){
            int spacing = adapter.getHorizontalSpacing(column);
            leftOffset+=tableCellWidth;
            if(0 != column){
                leftOffset+=spacing;
            }
            tableColumnArray.append(column,leftOffset);
        }
        this.measuredWidth=leftOffset;
        this.measuredHeight=topOffset;
    }

    public int getMeasuredWidth() {
        return measuredWidth;
    }

    public int getMeasuredHeight() {
        return measuredHeight;
    }

    public int findTableCellColumn(float x){
        return binarySearchStartIndex(tableColumnArray, x);
    }

    public int getTableCellOffsetY(int row){
        return tableRowArray.get(row);
    }

    public int getTableCellOffsetX(int column){
        return tableColumnArray.get(column);
    }

    public int findTableCellRow(float y){
        return binarySearchStartIndex(tableRowArray, y);
    }

    private int binarySearchStartIndex(SparseIntArray array, float value){
        int start = 0;
        int result = -1;
        int end = array.size() - 1;
        while (start <= end) {
            int middle = (start + end) / 2;
            int middleValue = array.get(middle);
            if (value == middleValue) {
                result = middle;
                break;
            } else if (value < middleValue) {
                end = middle - 1;
            } else {
                start = middle + 1;
            }
        }
        if (-1 == result) {
            result = start-1;
        }
        return result;
    }
}
