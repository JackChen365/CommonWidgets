package com.cz.widgets.zoomlayout.theater;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.cz.widgets.zoomlayout.R;
import com.cz.widgets.zoomlayout.RecyclerZoomLayout;
import com.cz.widgets.zoomlayout.ZoomOrientationHelper;
import com.cz.widgets.zoomlayout.tree.Previewable;

import org.jetbrains.annotations.NotNull;

/**
 * @author Created by cz
 * @date 2020-05-14 21:47
 * @email bingo110@126.com
 */
public class SeatTable extends RecyclerZoomLayout implements Previewable {
    private static final String TAG="SeatTable";
    private static final int TABLE_CELL=0x00;
    private static final int TABLE_OTHER=0x01;
    private static final int DIRECTION_START = -1;
    private static final int DIRECTION_END = 1;
    
    private final ZoomOrientationHelper orientationHelper;
    private LayoutState layoutState = new LayoutState();
    private TableIndexer tableIndexer=null;
    private TableAdapter adapter;

    public SeatTable(Context context) {
        this(context,null,R.attr.seatTable);
    }

    public SeatTable(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.seatTable);
    }

    public SeatTable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.orientationHelper=ZoomOrientationHelper.createOrientationHelper(this,ZoomOrientationHelper.VERTICAL);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeatTable, defStyleAttr, R.style.SeatTable);
        a.recycle();
    }

    public void setAdapter(@NonNull TableAdapter adapter) {
        this.adapter = adapter;
        this.layoutState.structureChanged=true;
        removeAllViews();
        clearRecyclerPool();
        requestLayout();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(layoutState.structureChanged&&null!=adapter){
            int rowCount = adapter.getRowCount();
            int columnCount = adapter.getColumnCount();
            if(0 < rowCount && 0 < columnCount){
                //Change the bool to avoid do all the operation many times.
                layoutState.structureChanged=false;
                detachAndScrapAttachedViews();
                //Step1: Add the first baseline view.
                //We don't have to scroll to anywhere. Because we just offset the location of all the child views.
                //So here the only thing we have to do is layout the first row and column in the table..
                View view = getViewAndMeasured(0,0);
                int decoratedMeasuredWidth = getDecoratedMeasuredWidth(view);
                int decoratedMeasuredHeight = getDecoratedMeasuredHeight(view);
                //Initialize the indexer
                tableIndexer = new TableIndexer(adapter,decoratedMeasuredWidth,decoratedMeasuredHeight);

                int paddingLeft = getPaddingLeft();
                int measuredWidth = tableIndexer.getMeasuredWidth();
                int totalSpaceInOther = orientationHelper.getTotalSpaceInOther();
                int left=paddingLeft+(measuredWidth-totalSpaceInOther)/2;
                //Step2: Fill the content.
                //Update the scroll location. We move the node to the vertical center.
                dispatchScrollChange(left,0);
                layoutState.scrollTo(left,0);
                //Fill the window
                fillTableLayout();
            }
        }
    }

    private void fillTableLayout() {
        int totalSpace = orientationHelper.getTotalSpace();
        int totalSpaceInOther = orientationHelper.getTotalSpaceInOther();

        int left = tableIndexer.findTableCellColumn(layoutState.scrollX);
        int right = tableIndexer.findTableCellColumn(layoutState.scrollX+totalSpaceInOther);

        int top = tableIndexer.findTableCellRow(layoutState.scrollY);
        int bottom = tableIndexer.findTableCellRow(layoutState.scrollY+totalSpace);
        //Update the layout state.
        layoutState.scrollRect.set(left,top,right,bottom);

        //Fill the layout.
        detachAndScrapAttachedViews();
        fillTableLayout(left,top,right,bottom);
    }

    /**
     * Fill the layout by the new rectangle value.
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    private void fillTableLayout(int left, int top, int right, int bottom){
        for(int column= left;column<=right;column++){
            for(int row=top;row<=bottom;row++){
                if(!adapter.isDisable(row,column)){
                    addTableCell(row,column);
                }
            }
        }
    }

    private View addTableCell(int row, int column) {
        View child = getViewAndMeasured(row,column);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int decoratedMeasuredWidth = getDecoratedMeasuredWidth(child);
        int decoratedMeasuredHeight = getDecoratedMeasuredHeight(child);

        int left=paddingLeft+tableIndexer.getTableCellOffsetX(column);
        int top=paddingTop+tableIndexer.getTableCellOffsetY(row);
        layoutDecorated(child,left,top,left+decoratedMeasuredWidth,top+decoratedMeasuredHeight);
        offsetChild(child,-layoutState.scrollX,-layoutState.scrollY);
        addAdapterView(child,-1);
        return child;
    }

    @NotNull
    private View getViewAndMeasured(int row,int column) {
        int viewType = adapter.getViewType(row, column);
        View view = getView(viewType);
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        layoutParams.row=row;
        layoutParams.column=column;
        adapter.onBindView(this, view,row,column);
        measureChild(view);
        return view;
    }

    @Override
    protected View newAdapterView(Context context, ViewGroup parent, int viewType) {
        if(null==adapter){
            throw new NullPointerException("The adapter is null!");
        }
        return adapter.onCreateView(this,viewType);
    }


    @Override
    protected int scrollHorizontallyBy(int dx, boolean isScaleDragged) {
        int totalSpaceInOther = orientationHelper.getTotalSpaceInOther();
        //If the screen range contains the table cell. like: h:1-3 v-1-2
        int paddingLeft = getPaddingLeft();
        int left = dx + layoutState.scrollX;
        int right = left + totalSpaceInOther;
        int measuredWidth = tableIndexer.getMeasuredWidth();
        //Check the boundary of the screen horizontally
        int scrolled = dx;
        if(left < paddingLeft){
            //to left
            scrolled=-layoutState.scrollX;
        } else if (right > measuredWidth) {
            //to right
            if(measuredWidth<totalSpaceInOther){
                scrolled = 0;
            } else {
                scrolled = measuredWidth-(right-dx);
            }
        }
        left = scrolled + layoutState.scrollX;
        right = left + totalSpaceInOther;
        int leftHierarchyDepthIndex = tableIndexer.findTableCellColumn(left);
        int rightHierarchyDepthIndex = tableIndexer.findTableCellColumn(right);
        int layoutDirection = dx > 0 ? DIRECTION_END : DIRECTION_START;
        if(isScaleDragged||DIRECTION_START==layoutDirection){
            //Move backward.
            if(layoutState.scrollRect.right>rightHierarchyDepthIndex){
                layoutState.scrollRect.right=rightHierarchyDepthIndex;
            }
            if(layoutState.scrollRect.left<leftHierarchyDepthIndex){
                //When scaling the content. The right side of the hierarchy depth will less than the current depth.
                layoutState.scrollRect.left=leftHierarchyDepthIndex;
            } else if(layoutState.scrollRect.left>leftHierarchyDepthIndex){
                int oldHierarchyDepthIndex = layoutState.scrollRect.left;
                layoutState.scrollRect.left=leftHierarchyDepthIndex;
                //Move forward.
                fillTableLayout(leftHierarchyDepthIndex,layoutState.scrollRect.top,oldHierarchyDepthIndex-1,layoutState.scrollRect.bottom);
            }
        }
        if(isScaleDragged||DIRECTION_END==layoutDirection){
            if(layoutState.scrollRect.left<leftHierarchyDepthIndex){
                layoutState.scrollRect.left=leftHierarchyDepthIndex;
            }
            if(layoutState.scrollRect.right>rightHierarchyDepthIndex){
                //When scaling the content. The right side of the hierarchy depth will less than the current depth.
                layoutState.scrollRect.right=rightHierarchyDepthIndex;
            } else if(layoutState.scrollRect.right<rightHierarchyDepthIndex){
                //Moving forward.
                int oldHierarchyDepthIndex = layoutState.scrollRect.right;
                layoutState.scrollRect.right=rightHierarchyDepthIndex;
                fillTableLayout(oldHierarchyDepthIndex+1,layoutState.scrollRect.top,rightHierarchyDepthIndex,layoutState.scrollRect.bottom);
            }
        }
        //After calculate with the offset value. we update the offset value.
        layoutState.scrollX+=scrolled;
        //Recycler view by layout state.
        recycleByLayoutState(layoutState);
        //Offset all the child views.
        return offsetChildrenHorizontal(-scrolled);
    }

    @Override
    protected int scrollVerticallyBy(int dy, boolean isScaleDragged) {
        int totalSpace = orientationHelper.getTotalSpace();
        //If the screen range contains the table cell. like: h:1-3 v-1-2
        int paddingTop = getPaddingTop();
        int top = dy+layoutState.scrollY;
        int bottom = top + totalSpace;
        int measuredHeight = tableIndexer.getMeasuredHeight();
        int scrolled = dy;
        if(top < paddingTop){
            //to top
            scrolled=-layoutState.scrollY;
        } else if (bottom > measuredHeight) {
            //to bottom
            if(measuredHeight<totalSpace){
                scrolled = 0;
            } else {
                scrolled = measuredHeight-(bottom-dy);
            }
        }
        top = scrolled+layoutState.scrollY;
        bottom = top + totalSpace;
        int topHierarchyBreadthIndex = tableIndexer.findTableCellRow(top);
        int bottomHierarchyBreadthIndex = tableIndexer.findTableCellRow(bottom);
        int layoutDirection = dy > 0 ? DIRECTION_END : DIRECTION_START;
        if(isScaleDragged||DIRECTION_START==layoutDirection){
            //Move backward.
            if(layoutState.scrollRect.bottom>bottomHierarchyBreadthIndex){
                layoutState.scrollRect.bottom=bottomHierarchyBreadthIndex;
            }
            if(layoutState.scrollRect.top>topHierarchyBreadthIndex){
                int oldHierarchyBreadthIndex = layoutState.scrollRect.top;
                layoutState.scrollRect.top=topHierarchyBreadthIndex;
                //Move forward.
                fillTableLayout(layoutState.scrollRect.left,topHierarchyBreadthIndex,layoutState.scrollRect.right,oldHierarchyBreadthIndex-1);
            }
        }
        if(isScaleDragged||DIRECTION_END==layoutDirection){
            if(layoutState.scrollRect.top<topHierarchyBreadthIndex){
                layoutState.scrollRect.top=topHierarchyBreadthIndex;
            }
            if(layoutState.scrollRect.bottom>bottomHierarchyBreadthIndex){
                //When scaling the content. The right side of the hierarchy depth will less than the current depth.
                layoutState.scrollRect.bottom=bottomHierarchyBreadthIndex;
            } else if(layoutState.scrollRect.bottom<bottomHierarchyBreadthIndex){
                int oldHierarchyBreadthIndex = layoutState.scrollRect.bottom;
                layoutState.scrollRect.bottom=bottomHierarchyBreadthIndex;
                //Move forward.
                fillTableLayout(layoutState.scrollRect.left,oldHierarchyBreadthIndex+1,layoutState.scrollRect.right,bottomHierarchyBreadthIndex);
            }
        }
        //After calculate with the offset value. we update the offset value.
        layoutState.scrollY+=scrolled;
        //Recycler view by layout state.
        recycleByLayoutState(layoutState);
        //Offset all the child views.
        return offsetChildrenVertical(-scrolled);
    }

    @Override
    public int getLayoutScrollX() {
        return layoutState.scrollX;
    }

    @Override
    public int getLayoutScrollY() {
        return layoutState.scrollY;
    }

    @Override
    public void childDrawableStateChanged(View child) {
        super.childDrawableStateChanged(child);
        LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        int row = layoutParams.row;
        int column = layoutParams.column;
        boolean selected = child.isSelected();
        //Update the preview layout.
    }

    @Override
    public View newPreview() {
        Context context = getContext();
        //We lightly change the class to draw the preview efficiently.
        SeatTable layout = new SeatTable(context) {
            private View childView;

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                //Measure all the tree nodes completely. This is generally for the preview layout.
                if (null != adapter) {
                    int rowCount = adapter.getRowCount();
                    int columnCount = adapter.getColumnCount();
                    if (0 < rowCount && 0 < columnCount) {
                        childView = getViewAndMeasured(0, 0);
                        int decoratedMeasuredWidth = getDecoratedMeasuredWidth(childView);
                        int decoratedMeasuredHeight = getDecoratedMeasuredHeight(childView);
                        //Initialize the indexer
                        TableIndexer tableIndexer = new TableIndexer(adapter, decoratedMeasuredWidth, decoratedMeasuredHeight);
                        int measuredWidth = tableIndexer.getMeasuredWidth();
                        int measuredHeight = tableIndexer.getMeasuredHeight();
                        setMeasuredDimension(measuredWidth, measuredHeight);
                    }
                }
            }

            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
            }

            @Override
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if(null != adapter){
                    int rowCount = adapter.getRowCount();
                    int columnCount = adapter.getColumnCount();
                    if (0 < rowCount && 0 < columnCount) {
                        for (int row = 0; row < rowCount; row++) {
                            for (int column = 0; column < columnCount; column++) {
                                if(!adapter.isDisable(row,column)){
                                    drawTableCell(canvas, adapter, childView, row, column);
                                }
                            }
                        }
                    }
                }
            }
            /**
             * Draw all the table cell all at once.
             *
             */
            private void drawTableCell(Canvas canvas, TableAdapter adapter, View child, int row, int column) {
                //This will add additional extra drawing time from 197 to 691
//                adapter.onBindView(this,childView,row,column);
                int paddingLeft = getPaddingLeft();
                int paddingTop = getPaddingTop();
                int decoratedMeasuredWidth = getDecoratedMeasuredWidth(child);
                int decoratedMeasuredHeight = getDecoratedMeasuredHeight(child);

                int left=paddingLeft+tableIndexer.getTableCellOffsetX(column);
                int top=paddingTop+tableIndexer.getTableCellOffsetY(row);
                layoutDecorated(child,left,top,left+decoratedMeasuredWidth,top+decoratedMeasuredHeight);
                canvas.save();
                canvas.translate(left, top);
                child.draw(canvas);
                canvas.restore();
            }
        };
        return layout;
    }

    @Override
    public void onChildChange(Canvas canvas, View child) {
        LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        int row = layoutParams.row;
        int column = layoutParams.column;
        int left = tableIndexer.getTableCellOffsetX(column);
        int top = tableIndexer.getTableCellOffsetY(row);
        canvas.save();
        canvas.translate(left,top);
        child.draw(canvas);
        canvas.restore();
    }

    /**
     * Recycler all the child views that out of the screen.
     * @param layoutState
     */
    private void recycleByLayoutState(LayoutState layoutState) {
        Rect rect = layoutState.scrollRect;
        for(int i=0;i<getChildCount();){
            View childView = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
            if(TABLE_CELL==layoutParams.viewType){
                int row = layoutParams.row;
                int column = layoutParams.column;
                //Check the rect including the empty rect. This is not like the class:Rect#contains(x,y)
                if(rect.left <= rect.right && rect.top <= rect.bottom
                        && column >= rect.left && column <= rect.right && row >= rect.top && row <= rect.bottom){
                    //Still in this screen.
                    i++;
                } else {
                    //This view is out of screen.
                    removeAndRecycleView(childView);
                }
            }
        }
    }


    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
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
     * {@link android.view.ViewGroup.MarginLayoutParams LayoutParams} subclass for children of
     * {@link RecyclerZoomLayout}. All the sub-class of this View are encouraged
     * to create their own subclass of this <code>LayoutParams</code> class
     * to store any additional required per-child view metadata about the layout.
     */
    public static class LayoutParams extends RecyclerZoomLayout.LayoutParams {
        public int viewType;
        public int column;
        public int row;

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
    }

    private class LayoutState {
        /**
         * If the data structure has changed. We will fill table and recycle the content again.
         */
        boolean structureChanged = false;
        /**
         * The rectangle represents the whole screen scroll value.
         * But not the dimension size. We use the scrollRect as a table size.
         * Like both left and right is represent the depth of tree.
         */
        final Rect scrollRect =new Rect();
        /**
         * The scroll distance horizontally.
         */
        int scrollX;
        /**
         * The scroll distance vertically.
         */
        int scrollY;

        private void scrollTo(int scrollX,int scrollY){
            this.scrollX=scrollX;
            this.scrollY=scrollY;
        }
    }
}
