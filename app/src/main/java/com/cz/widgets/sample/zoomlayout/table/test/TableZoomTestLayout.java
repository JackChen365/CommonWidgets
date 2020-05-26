package com.cz.widgets.sample.zoomlayout.table.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.cz.widgets.sample.R;
import com.cz.widgets.zoomlayout.RecyclerZoomLayout;
import com.cz.widgets.zoomlayout.ZoomOrientationHelper;
import com.cz.widgets.zoomlayout.ZoomLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Created by cz
 * @date 2020-03-12 20:41
 * @email bingo110@126.com
 *
 * Just a example for {@link ZoomLayout}
 */
public class TableZoomTestLayout extends RecyclerZoomLayout {
    private static final String TAG="TableZoomLayout";
    private static final int DIRECTION_START = -1;
    private static final int DIRECTION_END = 1;
    private final ZoomOrientationHelper orientationHelper;
    private LayoutState layoutState = new LayoutState();
    private Adapter adapter;
    private int columnCount;
    private int columnPadding;

    public TableZoomTestLayout(Context context) {
        this(context,null, R.attr.tableZoomLayout);
    }

    public TableZoomTestLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.tableZoomLayout);
    }

    public TableZoomTestLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        this.orientationHelper = ZoomOrientationHelper.createOrientationHelper(this,ZoomOrientationHelper.VERTICAL);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TableZoomLayout, defStyleAttr, R.style.GridZoomLayout);
        setGridColumnPadding(a.getDimensionPixelOffset(R.styleable.TableZoomLayout_grid_columnPadding,0));
        a.recycle();
    }


    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        this.layoutState.structureChanged=true;
        removeAllViews();
        clearRecyclerPool();
        updateLayoutStateFromEnd();
        requestLayout();
    }

    @Override
    protected View newAdapterView(Context context, ViewGroup parent, int viewType) {
        if(null==adapter){
            throw new NullPointerException("The adapter is null!");
        }
        return adapter.getView(context,parent,viewType);
    }

    private void setGridColumnCount(int columnCount) {
        this.columnCount=columnCount;
    }

    private void setGridColumnPadding(int columnPadding) {
        this.columnPadding=columnPadding;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(null!=adapter&&layoutState.structureChanged){
            //The first step: measure the first row of the table.
            measureTableColumn(layoutState,adapter);
            //The second step: measure the table column by its column weight.
            measureTableColumnWeight(layoutState,widthMeasureSpec);
            layoutState.updateTableColumn();
        }
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if(MeasureSpec.UNSPECIFIED==widthMode){
            int paddingLeft = getPaddingLeft();
            int paddingRight = getPaddingRight();
            int tableMeasuredWidth = paddingLeft+paddingRight;
            int[] tableColumn = layoutState.tableColumn;
            for (int i = 0; i < tableColumn.length;tableMeasuredWidth+=tableColumn[i++]);
            int measuredHeight = getMeasuredHeight();
            int measuredWidth = getMeasuredWidth();
            setMeasuredDimension(Math.max(measuredWidth,tableMeasuredWidth), measuredHeight);
        }
    }

    /**
     * First step: measure the table cell.
     */
    @SuppressLint("Range")
    private void measureTableColumn(@NonNull LayoutState layoutState, @NonNull Adapter adapter) {
        int columnCount = adapter.getColumnCount();
        layoutState.tableColumn=new int[columnCount];
        for(int i=0;i<columnCount;i++){
            float columnWidth = adapter.getColumnWidth(0, i);
            int childColumnWidth;
            if(0f!=columnWidth){
                //Store the column width from the adapter.
                childColumnWidth=(int)columnWidth;
            } else {
                //Store the column width from the view measured width.
                int viewType = adapter.getViewType(0, i);
                View childView = getView(viewType);
                adapter.onBindView(childView,0,i);
                measureChild(childView);
                childColumnWidth=childView.getMeasuredWidth();
            }
            layoutState.tableColumn[i]=childColumnWidth;
        }
    }

    /**
     * Measure the extra space by table cell's weight.
     * When the measurement mode was EXACTLY. it usually have extras space. Here we allocate the available space by the cell's weight.
     * @param layoutState
     * @param widthMeasureSpec
     */
    private void measureTableColumnWeight(LayoutState layoutState, int widthMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if(MeasureSpec.EXACTLY==widthMode){
            float totalWeight = 0;
            int columnCount = adapter.getColumnCount();
            float[] columnWeightArray=new float[layoutState.tableColumn.length];
            float availableSpace = getMeasuredWidth()-getPaddingLeft()-getPaddingRight();
            for(int i=0;i<columnCount;i++){
                float columnWeight = adapter.getColumnWeight(i);
                columnWeightArray[i]=columnWeight;
                //Calculate the total weight of the table.
                totalWeight+=columnWeight;
                //We subtract the size of the table column cell.
                availableSpace-=layoutState.tableColumn[i];
            }
            //If we still have available space. Allocate all the space by the table cell weight.
            if(0 < availableSpace){
                for (int column = 0; column < columnCount; column++) {
                    float columnWeight = columnWeightArray[column];
                    if(0!=columnWeight){
                        //We re-calculate the header column by its weight.
                        layoutState.tableColumn[column] += (int) (columnWeight/totalWeight*availableSpace);
                    }
                }
            }
        }
    }

    @Override
    protected void removeAdapterView(View view) {
        super.removeView(view);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(null!=adapter&&layoutState.structureChanged){
            //Change the bool to avoid do all the operation many times.
            layoutState.structureChanged=false;
            //Fill the table.
            detachAndScrapAttachedViews();
            updateLayoutStateFromEnd();
            fillTableAndRecycle(adapter,layoutState);
        }
    }

    @Override
    protected int scrollHorizontallyBy(int dx,boolean isScaleDragged) {
        super.scrollHorizontallyBy(dx,isScaleDragged);
        if (0==dx||null==adapter||0==adapter.getRowCount()) {
            return 0;
        }
        return offsetChildrenHorizontal(-dx);
    }

    @Override
    protected int scrollVerticallyBy(int dy,boolean isScaleDragged) {
        super.scrollVerticallyBy(dy,isScaleDragged);
        if (0==dy||null==adapter||0==adapter.getRowCount()) {
            return 0;
        }
        int scrolled = dy;
        Log.i(TAG,"scrollVerticallyBy:"+dy+" isScaleDragged:"+isScaleDragged);
        if(null!=adapter){
            int layoutDirection = dy > 0 ? DIRECTION_END : DIRECTION_START;
            int absDy = Math.abs(dy);
            //Update the layout state.
            updateLayoutState(layoutState,layoutDirection,absDy);
            int consumed=layoutState.scrollingOffset + fillTableAndRecycle(adapter,layoutState);
            if (consumed >= 0) {
                consumed = absDy > consumed ? layoutDirection * consumed : dy;
                scrolled = offsetChildrenVertical(-consumed);
            }
            if(isScaleDragged){
                updateLayoutState(layoutState,-layoutDirection,absDy);
                fillTable(adapter,layoutState);
            }
        }
        return scrolled;
    }

    /**
     * Update the layout state by layout direction.
     * @param layoutDirection the layout direction.
     * @param requiredSpace
     */
    protected void updateLayoutState(LayoutState layoutState,int layoutDirection,int requiredSpace) {
        int scrollingOffset=0;
        if(layoutDirection== DIRECTION_END){
            int childCount = getChildCount();
            View view=getChildAt(childCount-1);
            if(null!=view){
                layoutState.itemDirection= DIRECTION_END;
                layoutState.position= getTableRow(view) + layoutState.itemDirection;
                layoutState.layoutOffset = orientationHelper.getDecoratedEnd(view);
                View startChildView = getChildAt(0);
                if(null!=startChildView){
                    layoutState.layoutStartOffset=orientationHelper.getDecoratedStartInOther(startChildView);
                }
                //Layout from the bottom. so the scroll offset is the view's bottom minus the window height.
                scrollingOffset=orientationHelper.getDecoratedEnd(view) - orientationHelper.getEndAfterPadding();
            }
        } else {
            View childView = getChildAt(0);
            if(null!=childView){
                layoutState.itemDirection= DIRECTION_START;
                layoutState.position= getTableRow(childView) + layoutState.itemDirection;
                layoutState.layoutOffset =orientationHelper.getDecoratedStart(childView);
                View startChildView = getChildAt(0);
                if(null!=startChildView){
                    layoutState.layoutStartOffset=orientationHelper.getDecoratedStartInOther(startChildView);
                }
                //Layout from the top. The first view's top plus the window top.
                scrollingOffset= -orientationHelper.getDecoratedStart(childView) + orientationHelper.getStartAfterPadding();
            }
        }
        layoutState.available= requiredSpace - scrollingOffset;
        layoutState.scrollingOffset = scrollingOffset;
    }

    protected void updateLayoutStateFromEnd() {
        layoutState.layoutOffset = 0;
        layoutState.position = 0;
        layoutState.itemDirection = DIRECTION_END;
        layoutState.available = orientationHelper.getTotalSpace();
    }

    private int getTableRow(View child) {
        LayoutParams layoutParams= (LayoutParams) child.getLayoutParams();
        return layoutParams.row;
    }

    private int getTableColumn(View child) {
        LayoutParams layoutParams= (LayoutParams) child.getLayoutParams();
        return layoutParams.column;
    }

    /**
     * Fill the content. If we have available space. We fillTableAndRecycle it.
     * @see LayoutState#available
     * @return
     */
    public int fillTableAndRecycle(@NonNull Adapter adapter, @NonNull LayoutState layoutState){
        //The available space.
        if(0>layoutState.available){
            layoutState.scrollingOffset+=layoutState.available;
        }
        //We recycler the view when they out of the window.
        recycleByLayoutState(layoutState);
        //Fill the table.
        return fillTable(adapter,layoutState);
    }

    /**
     * Fill the content. If we have available space. We fillTableAndRecycle it.
     * @see LayoutState#available
     * @return
     */
    @SuppressLint("Range")
    public int fillTable(@NonNull Adapter adapter, @NonNull LayoutState layoutState){
        //The available space.
        int start=layoutState.available;
        if(0>layoutState.available){
            layoutState.scrollingOffset+=layoutState.available;
        }
        int remainingSpace=layoutState.available;
        int columnCount = adapter.getColumnCount();
        View[] columnViewArray=null;
        List<Rect> mergedTableCell=new ArrayList<>();
        while(0<remainingSpace&&hasMore()){
            //Ask for a view.
            int row=layoutState.position;
            int consumed=0;
            if(null==columnViewArray){
                columnViewArray=new View[columnCount];
            }
            if(null==mergedTableCell){
                mergedTableCell=new ArrayList<>();
            }
            int offsetX = layoutState.layoutStartOffset;
            for(int column=0;column<columnCount;column++){
                if(!inMergedTableCell(mergedTableCell,row,column)){
                    int viewType = adapter.getViewType(row, column);
                    View view = getView(viewType);
                    adapter.onBindView(view,row,column);
                    int columnWidth = layoutState.tableColumn[column];
                    view.measure(MeasureSpec.makeMeasureSpec(columnWidth,MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT,MeasureSpec.AT_MOST));
                    LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                    layoutParams.row=row;
                    layoutParams.column=column;
//                    int columnSpan = adapter.getColumnSpan(row, column);
//                    int rowSpan = adapter.getRowSpan(row, column);
//                    if(0 >= columnSpan || 0 >= rowSpan){
//                        throw new IllegalArgumentException("The table cell row:"+row+" column:"+column+" has a unexpected error! Please check the table cell span. The row span:"+rowSpan+" and the column span:"+columnSpan);
//                    }
//                    if(1<columnSpan||1<rowSpan){
//                        //The merge table cell
//                        mergedTableCell.add(new Rect(column,row,column+columnSpan,row+rowSpan));
//                        if(1 < rowSpan){
//                            //Need merge table cell. We need to calculate the extra table cell in order to know the exactly size of the cell.
//
//                        }
//                    } else {
//                    }
                    int childViewConsumed= layoutChildView(view,offsetX);
                    if(consumed<childViewConsumed){
                        consumed=childViewConsumed;
                    }
                    offsetX+=orientationHelper.getDecoratedMeasurementInOther(view);
                    columnViewArray[column]=view;
                }
            }
            //Adding all the view in order.
            if (DIRECTION_END == layoutState.itemDirection) {
                for(int i=0;i<columnViewArray.length;i++){
                    View childView = columnViewArray[i];
                    addView(childView);
                }
            } else if (DIRECTION_START == layoutState.itemDirection) {
                for(int i=columnViewArray.length-1;i>=0;i--){
                    View childView = columnViewArray[i];
                    addView(childView, 0);
                }
            }
            Arrays.fill(columnViewArray,null);
            layoutState.layoutOffset +=consumed*layoutState.itemDirection;
            layoutState.position+=layoutState.itemDirection;
            layoutState.available-=consumed;
            remainingSpace-=consumed;
        }
        return start-layoutState.available;
    }

    /**
     * Check if this table cell is inside the merged table cell.
     * @param mergedTableCell
     * @param column
     * @param row
     * @return
     */
    private boolean inMergedTableCell(List<Rect> mergedTableCell,int row,int column){
        for(Rect rect:mergedTableCell){
            if(rect.contains(column,row)){
                return true;
            }
        }
        return false;
    }

    /**
     * Layout the child view.
     * @param view
     * @return
     */
    protected int layoutChildView(View view,int left){
        int top;
        int right;
        int bottom;
        int consumed = orientationHelper.getDecoratedMeasurement(view);
        right = left + orientationHelper.getDecoratedMeasurementInOther(view);
        if (layoutState.itemDirection == DIRECTION_START) {
            bottom = layoutState.layoutOffset;
            top = layoutState.layoutOffset - consumed;
        } else {
            top = layoutState.layoutOffset;
            bottom = layoutState.layoutOffset + consumed;
        }
        layoutDecorated(view, left, top, right, bottom);
        return consumed;
    }

    /**
     * Recycle the view by the layout state.
     */
    protected void recycleByLayoutState(LayoutState layoutState) {
        if(layoutState.itemDirection== DIRECTION_START){
            //Recycle the view from the end.
            recycleViewsFromEnd(layoutState.scrollingOffset);
        } else if(layoutState.itemDirection== DIRECTION_END){
            //Recycle the view from the start.
            recycleViewsFromStart(layoutState.scrollingOffset);
        }
    }

    private void recycleViewsFromStart(int dt) {
        if (dt < 0) {
            return;
        }
        int limit = dt;
        int childCount = getChildCount();
        for (int i=0;i<childCount-1;i++) {
            View child = getChildAt(i);
            if (orientationHelper.getDecoratedEnd(child) > limit) {// stop here
                recycleChildren(0, i);
                break;
            }
        }
    }

    private void recycleViewsFromEnd(int dt) {
        int childCount = getChildCount();
        if (dt < 0) {
            return;
        }
        int limit = orientationHelper.getEnd() - dt;
        for (int i=childCount-1;i>=0;i--) {
            View child = getChildAt(i);
            if (orientationHelper.getDecoratedStart(child) < limit) {// stop here
                recycleChildren(childCount - 1, i);
                break;
            }
        }
    }

    private void recycleChildren(int startIndex,int endIndex) {
        if (endIndex > startIndex) {
            for (int i=endIndex-1;i>=0;i--) {
                removeAndRecycleViewAt(i);
            }
        } else if(endIndex < startIndex){
            for (int i=startIndex;i>=endIndex + 1;i--) {
                removeAndRecycleViewAt(i);
            }
        }
    }

    public void removeAndRecycleViewAt(int index) {
        View childView = getChildAt(index);
        removeAndRecycleView(childView);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return super.canScrollHorizontally(direction);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return super.canScrollVertically(direction);
    }


    protected boolean hasMore(){
        int itemCount = adapter.getRowCount();
        return 0 <= layoutState.position && layoutState.position < itemCount;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        Context context = getContext();
        return new LayoutParams(context,attrs);
    }

    /**
     * {@link MarginLayoutParams LayoutParams} subclass for children of
     * {@link RecyclerZoomLayout}. All the sub-class of this View are encouraged
     * to create their own subclass of this <code>LayoutParams</code> class
     * to store any additional required per-child view metadata about the layout.
     */
    public static class LayoutParams extends RecyclerZoomLayout.LayoutParams {
        public int row;
        public int rowSpan=1;
        public int column;
        public int columnSpan=1;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(RecyclerZoomLayout.LayoutParams source) {
            super((ViewGroup.LayoutParams) source);
        }

        @NonNull
        @Override
        public String toString() {
            return "row:"+row+" column:"+column;
        }
    }

    /**
     * The abstract data adapter.
     * @param <E> the generic type of the object.
     */
    public static abstract class Adapter<E>{
        /**
         * Return the row count of the table.
         * @return
         */
        public abstract int getRowCount();
        /**
         * Return the column count of the table.
         * @return
         */
        public abstract int getColumnCount();
        /**
         * Return the column's width weight.
         * For the table column, if we still have extra space. You could use a column weight to ask for more space.
         * @param column
         * @return
         */
        public float getColumnWeight(int column){
            return 0f;
        }

        /**
         * Return the table column width.
         * @return
         */
        public float getColumnWidth(int row, int column){ return 0f; }

        /**
         * For a merge table cell. Here return the row span count.
         * @param row
         * @param column
         * @return
         */
        public int getRowSpan(int row,int column){
            return 1;
        }

        /**
         * For a merge table cell. Here return the column span count.
         * @param row
         * @param column
         * @return
         */
        public int getColumnSpan(int row,int column){
            return 1;
        }

        /**
         * Return the view type by the specific row and column of the table.
         * @param row
         * @param column
         * @return
         */
        public int getViewType(int row,int column){
            return 0;
        }
        /**
         * Return a view by the given view type.
         * @param viewType
         * @return
         */
        public abstract View getView(Context context,ViewGroup parent, int viewType);

        /**
         * Binding the view by the specific row and column of the table.
         * @param view
         */
        public abstract void onBindView(View view, int row, int column);

    }

    private class LayoutState {
        int[] tableColumn;
        int[] tableColumnArray;
        /**
         * If the data structure has changed. We will fillTableAndRecycle the content again.
         */
        boolean structureChanged = false;
        /**
         * The available space
         */
        int available = 0;
        /**
         * Current layout offset.
         */
        int layoutOffset = 0;
        /**
         * The scroll offset
         */
        int scrollingOffset = 0;
        /**
         * The table start cell layout offset position.
         * It's always the first view in the group. We use this offset value to layout the cell.
         */
        int layoutStartOffset=0;
        /**
         * Current position.
         */
        int position = 0;
        /**
         * The direction of the layout.
         */
        int itemDirection = DIRECTION_END;

        private void updateTableColumn() {
            if (null != tableColumn) {
                tableColumnArray = new int[tableColumn.length + 1];
                tableColumnArray[0] = 0;
                int sum = 0;
                for (int i = 0; i < tableColumn.length; i++) {
                    sum += tableColumn[i];
                    tableColumnArray[i + 1] = sum;
                }
            }
        }
    }
}
