package com.cz.widgets.textview.span.table;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.cz.widgets.textview.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cz
 * @date 2020-04-22 21:36
 * @email bingo110@126.com
 *
 * The table layout.
 *
 * All the features:
 * 1. Optimize for the measure and layout.
 * 2. Support merge table cell.
 *
 * @see #setAdapter(Adapter) The data adapter of the table.
 *
 * Also take a look at {@link com.cz.widgets.textview.span.view.ClickableViewSpan}
 * You could put this view into the view span to support table in the text view.
 *
 * todo Need more time to support view scroll.
 *
 */
public class TableLayout extends ViewGroup implements ViewParent {
    /**
     * The table data adapter.
     */
    private Adapter adapter;
    /**
     * The cached view recycler pool
     */
    private final RecyclerBin recyclerBin=new RecyclerBin();
    /**
     * The borderDrawable drawable.
     */
    private Drawable borderDrawable;

    public TableLayout(Context context) {
        this(context,null, R.attr.tableLayout);
    }

    public TableLayout(Context context, AttributeSet attrs) {
        this(context, attrs,R.attr.tableLayout);
    }

    public TableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        TypedArray typedArray = context.obtainStyledAttributes(null,R.styleable.TableLayout,R.attr.tableLayout,R.style.TableLayout);
        setBorderDrawable(typedArray.getDrawable(R.styleable.TableLayout_table_border));
        typedArray.recycle();
    }

    private void setBorderDrawable(@NonNull Drawable drawable) {
        this.borderDrawable =drawable;
    }

    /**
     * Setting a new adapter.
     * @param adapter
     */
    public void setAdapter(@NonNull Adapter adapter) {
        this.adapter = adapter;
        recyclerBin.detachAndScrapAttachedViews();
        //We initialize all the table cell all at once.
        int rowCount = adapter.getRowCount();
        int columnCount = adapter.getColumnCount();
        List<Rect> mergedTableCell=new ArrayList<>();
        for(int row=0;row<rowCount;row++) {
            for (int column = 0; column < columnCount; column++) {
                if(!inMergeTableCell(mergedTableCell,column,row)){
                    int columnSpan = adapter.getColumnSpan(row, column);
                    int rowSpan = adapter.getRowSpan(row, column);
                    if(0 >= columnSpan || 0 >= rowSpan){
                        throw new IllegalArgumentException("The table cell row:"+row+" column:"+column+" has a unexpected error! Please check the table cell span. The row span:"+rowSpan+" and the column span:"+columnSpan);
                    }
                    if(1<columnSpan||1<rowSpan){
                        //The merge table cell
                        mergedTableCell.add(new Rect(column,row,column+columnSpan,row+rowSpan));
                    }
                    //Asking for a view either from scrap list or adapter.
                    View adapterView = recyclerBin.getAdapterView(row, column);
                    LayoutParams layoutParams= (LayoutParams) adapterView.getLayoutParams();
                    layoutParams.columnSpan=columnSpan;
                    layoutParams.rowSpan=rowSpan;
                    //Binding the view information
                    adapter.onBindView(adapterView, row, column);
                    //Adding the view to the ViewGroup
                    super.addView(adapterView,-1,adapterView.getLayoutParams());
                }
            }
        }
    }

    /**
     * Check if this table cell is inside the merged table cell.
     * @param mergedTableCell
     * @param column
     * @param row
     * @return
     */
    private boolean inMergeTableCell(List<Rect> mergedTableCell, int column, int row){
        for(Rect rect:mergedTableCell){
            if(rect.contains(column,row)){
                return true;
            }
        }
        return false;
    }

    /**
     * Forbid adding a view from outside.
     * @param child
     * @param index
     * @param params
     */
    @Override
    @Deprecated
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
       //Nothing to do.
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int rowCount = adapter.getRowCount();
        int columnCount = adapter.getColumnCount();
        int[] cellRowArray=new int[rowCount];
        int[] cellColumnArray=new int[columnCount];
        //First step: measure the table cell.
        measureTableCell(cellRowArray, cellColumnArray);

        //The weight of the table column.
        measureTableColumnWeight(cellColumnArray,widthMeasureSpec);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int[] columnOffsetArray =new int[cellColumnArray.length];
        int[] rowOffsetArray =new int[cellRowArray.length];
        int columnOffset = paddingLeft;
        for (int i = 0; i < cellColumnArray.length; i++) {
            columnOffsetArray[i]=columnOffset;
            columnOffset += cellColumnArray[i];
        }
        int rowOffset = paddingTop;
        for (int i = 0; i < cellRowArray.length; i++) {
            rowOffsetArray[i]=rowOffset;
            rowOffset += cellRowArray[i];
        }
        int childCount = getChildCount();
        for(int i=0;i<childCount;i++){
            View childView=getChildAt(i);
            LayoutParams layoutParams= (LayoutParams) childView.getLayoutParams();
            layoutParams.columnOffset=columnOffsetArray[layoutParams.column];
            layoutParams.rowOffset=rowOffsetArray[layoutParams.row];

            if(1<layoutParams.columnSpan||1<layoutParams.rowSpan){
                //The merge table cell
                int tableCellWidth;
                if(1 == layoutParams.columnSpan){
                    tableCellWidth = cellColumnArray[layoutParams.column];
                } else {
                    tableCellWidth = columnOffsetArray[layoutParams.column+layoutParams.columnSpan]-columnOffsetArray[layoutParams.column];
                }
                int tableCellHeight;
                if(1 == layoutParams.rowSpan){
                    tableCellHeight = cellRowArray[layoutParams.row];
                } else {
                    tableCellHeight = rowOffsetArray[layoutParams.row+layoutParams.rowSpan]-rowOffsetArray[layoutParams.row];
                }
                childView.measure(MeasureSpec.makeMeasureSpec(tableCellWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(tableCellHeight, MeasureSpec.EXACTLY));
            } else {
                //The default table cell
                childView.measure(MeasureSpec.makeMeasureSpec(cellColumnArray[layoutParams.column], MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(cellRowArray[layoutParams.row], MeasureSpec.EXACTLY));
            }
        }
        int tableMeasuredWidth = paddingLeft+paddingRight;
        for (int i = 0; i < cellColumnArray.length;tableMeasuredWidth+=cellColumnArray[i++]);
        //Sum the table cell height.
        int tableMeasuredHeight = paddingTop+paddingBottom;
        for (int i = 0; i < cellRowArray.length;tableMeasuredHeight+=cellRowArray[i++]);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if(MeasureSpec.EXACTLY==widthMode){
            int measuredWidth = getMeasuredWidth();
            setMeasuredDimension(Math.max(measuredWidth,tableMeasuredWidth), tableMeasuredHeight);
        } else {
            setMeasuredDimension(tableMeasuredWidth, tableMeasuredHeight);
        }
    }

    /**
     * First step: measure the table cell.
     * @param rowArray
     * @param columnArray
     */
    @SuppressLint("Range")
    private void measureTableCell(int[] rowArray, int[] columnArray) {
        int childCount = getChildCount();
        for(int i=0;i<childCount;i++){
            View childView = getChildAt(i);
            LayoutParams layoutParams= (LayoutParams) childView.getLayoutParams();
            int row = layoutParams.row;
            int column = layoutParams.column;
            //Always measure the table cell in wrapped mode. Because we can't let a table cell measured in fill mode.
            measureChild(childView, MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.AT_MOST));
            int viewMeasuredWidth = childView.getMeasuredWidth();
            int viewMeasuredHeight = childView.getMeasuredHeight();
            float columnWidth = adapter.getColumnWidth(row, column);
            int childColumnWidth;
            if(0f!=columnWidth){
                //Store the column width from the adapter.
                childColumnWidth=(int)columnWidth;
            } else {
                //Store the column width from the view measured width.
                childColumnWidth=viewMeasuredWidth;
            }
            if(columnArray[column]<childColumnWidth){
                columnArray[column]=childColumnWidth;
            }
            if(rowArray[row]<viewMeasuredHeight){
                rowArray[row]=viewMeasuredHeight;
            }
        }
    }

    /**
     * Measure the extra space by table cell's weight.
     * When the measurement mode was EXACTLY. it usually have extras space. Here we allocate the available space by the cell's weight.
     * @param cellColumnArray
     * @param widthMeasureSpec
     */
    private void measureTableColumnWeight(int[] cellColumnArray, int widthMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if(MeasureSpec.EXACTLY==widthMode){
            float totalWeight = 0;
            int columnCount = adapter.getColumnCount();
            float[] columnWeightArray=new float[cellColumnArray.length];
            float availableSpace = getMeasuredWidth()-getPaddingLeft()-getPaddingRight();
            for(int i=0;i<columnCount;i++){
                float columnWeight = adapter.getColumnWeight(i);
                columnWeightArray[i]=columnWeight;
                //Calculate the total weight of the table.
                totalWeight+=columnWeight;
                //We subtract the size of the table column cell.
                availableSpace-=cellColumnArray[i];
            }
            //If we still have available space. Allocate all the space by the table cell weight.
            if(0 < availableSpace){
                for (int column = 0; column < columnCount; column++) {
                    float columnWeight = columnWeightArray[column];
                    if(0!=columnWeight){
                        //We re-calculate the header column by its weight.
                        cellColumnArray[column] += (int) (columnWeight/totalWeight*availableSpace);
                    }
                }
            }
        }
    }

    /**
     * Layout the table cells.
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for(int i=0;i<childCount;i++){
            View childView = getChildAt(i);
            int childMeasuredWidth = childView.getMeasuredWidth();
            int childMeasuredHeight = childView.getMeasuredHeight();
            LayoutParams layoutParams= (LayoutParams) childView.getLayoutParams();
            int left = layoutParams.columnOffset;
            int top=layoutParams.rowOffset;
            //Before we layout the table cell. We actually measured the table cell. So here we just use the measured size.
            childView.layout(left,top,left+childMeasuredWidth,top+childMeasuredHeight);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawTableBorder(canvas);
    }

    /**
     * Drawing the table border
     * @param canvas
     */
    private void drawTableBorder(Canvas canvas) {
        if(null!=borderDrawable){
            //Draw the table border inside.
            int right=0;
            int bottom=0;
            int childCount = getChildCount();
            int intrinsicWidth = borderDrawable.getIntrinsicWidth();
            int intrinsicHeight = borderDrawable.getIntrinsicHeight();
            for(int i=0;i<childCount;i++){
                View childView = getChildAt(i);
                int childLeft = childView.getLeft();
                int childTop = childView.getTop();
                int childRight = childView.getRight();
                int childBottom = childView.getBottom();
                //Draw table border on the left side.
                borderDrawable.setBounds(childLeft,childTop,childLeft+intrinsicHeight/2,childBottom);
                borderDrawable.draw(canvas);

                //Draw table border on the top.
                borderDrawable.setBounds(childLeft,childTop,childRight,childTop+intrinsicWidth/2);
                borderDrawable.draw(canvas);

                //Draw table border on the right side.
                borderDrawable.setBounds(childRight-intrinsicHeight/2,childTop,childRight,childBottom);
                borderDrawable.draw(canvas);

                //Draw table border on the bottom.
                borderDrawable.setBounds(childLeft,childBottom-intrinsicWidth/2,childRight,childBottom);
                borderDrawable.draw(canvas);

                if(right<childRight){
                    right=childRight;
                }
                if(bottom<childBottom){
                    bottom=childBottom;
                }
            }
            //Draw table board outside.
            int left=getPaddingLeft();
            int top=getPaddingTop();
            //Draw table border on the left side.
            borderDrawable.setBounds(left,top,left+intrinsicHeight/2,bottom);
            borderDrawable.draw(canvas);

            //Draw table border on the top.
            borderDrawable.setBounds(left,top,right,top+intrinsicWidth/2);
            borderDrawable.draw(canvas);

            //Draw table border on the right side.
            borderDrawable.setBounds(right-intrinsicHeight/2,top,right,bottom);
            borderDrawable.draw(canvas);

            //Draw table border on the bottom.
            borderDrawable.setBounds(left,bottom-intrinsicWidth/2,right,bottom);
            borderDrawable.draw(canvas);
        }
    }

    //------------------------------------------------------------
    // Customize our layout params.
    //------------------------------------------------------------
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        Context context = getContext();
        return new LayoutParams(context,attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    class LayoutParams extends MarginLayoutParams{
        public int viewType;
        public int row;
        public int rowSpan=1;
        public int rowOffset;
        public int column;
        public int columnSpan=1;
        public int columnOffset;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }


    class RecyclerBin{
        /**
         * The scrap list sorted by different view types.
         */
        SparseArray<LinkedList<View>> scrapViews= new SparseArray<>();

        /**
         * Add a specific view to the scrap list.
         * @param view
         */
        void addScarpView(@NonNull View view){
            LayoutParams layoutParams= (LayoutParams) view.getLayoutParams();
            LinkedList<View> viewList = scrapViews.get(layoutParams.viewType);
            if(null==viewList){
                viewList=new LinkedList<>();
                scrapViews.put(layoutParams.viewType,viewList);
            }
            viewList.add(view);
        }

        /**
         * Detach all the child view in this ViewGroup and put all the child view into the scrap list.
         */
        void detachAndScrapAttachedViews(){
            while(0 < getChildCount()) {
                View childView = getChildAt(0);
                removeView(childView);
                LayoutParams layoutParams= (LayoutParams) childView.getLayoutParams();
                LinkedList<View> viewList = scrapViews.get(layoutParams.viewType);
                if(null==viewList){
                    viewList=new LinkedList<>();
                    scrapViews.put(layoutParams.viewType,viewList);
                }
                viewList.add(childView);
            }
        }

        /**
         * clear all the child view in this recycler pool.
         */
        void recyclerAll(){
            scrapViews.clear();
        }

        /**
         * Return a new view from adapter.
         * @param row
         * @param column
         * @return
         */
        View newViewFromAdapter(int row, int column){
            if(null==adapter) throw new NullPointerException("获取View时Adapter不能为空!");
            int viewType = adapter.getViewType(row, column);
            View view = adapter.getView(TableLayout.this, viewType);
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.row=row;
            layoutParams.column=column;
            layoutParams.viewType=viewType;
            return view;
        }

        /**
         * Return a new view either from the adapter or the recycler pool
         * @param row
         * @param column
         * @return
         */
        View getAdapterView(int row, int column){
            if(null==adapter) throw new NullPointerException("The adapter is null!");
            View view=null;
            if(0 < scrapViews.size()){
                int viewType = adapter.getViewType(row, column);
                LinkedList<View> views = scrapViews.get(viewType);
                if(!views.isEmpty()){
                    view=views.removeFirst();
                    //Update the table information.
                    LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                    layoutParams.row=row;
                    layoutParams.column=column;
                    layoutParams.viewType=viewType;
                }
            }
            if(null==view){
                //Ask for a new view from the adapter.
                view = newViewFromAdapter(row,column);
            }
            return view;
        }
    }

    /**
     * The table adapter.
     */
    public static abstract class Adapter {
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
        public abstract View getView(ViewGroup parent, int viewType);

        /**
         * Binding the view by the specific row and column of the table.
         * @param view
         */
        public abstract void onBindView(View view, int row, int column);

    }
}
