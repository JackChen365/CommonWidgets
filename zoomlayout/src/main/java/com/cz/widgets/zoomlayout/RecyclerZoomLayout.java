package com.cz.widgets.zoomlayout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Created by cz
 * @date 2020-03-19 21:28
 * @email bingo110@126.com
 * todo
 */
public abstract class RecyclerZoomLayout extends ZoomLayout {
    private static final Rect tempRect=new Rect();
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;
    private final RecyclerBin recyclerBin=new RecyclerBin();
    private final ArrayList<ItemDecoration> itemDecorations = new ArrayList<>();

    public RecyclerZoomLayout(Context context) {
        super(context);
    }

    public RecyclerZoomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerZoomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Add an {@link ItemDecoration} to this RecyclerView. Item decorations can
     * affect both measurement and drawing of individual value views.
     *
     * <p>Item decorations are ordered. Decorations placed earlier in the list will
     * be run/queried/drawn first for their effects on value views. Padding added to views
     * will be nested; a padding added by an earlier decoration will mean further
     * value decorations in the list will be asked to draw/pad within the previous decoration's
     * given area.</p>
     *
     * @param decor Decoration to add
     * @param index Position in the decoration chain to insert this decoration at. If this value
     *              is negative the decoration will be added at the end.
     */
    public void addItemDecoration(@NonNull ItemDecoration decor, int index) {
        if (itemDecorations.isEmpty()) {
            setWillNotDraw(false);
        }
        decor.attachToView(this);
        if (index < 0) {
            itemDecorations.add(decor);
        } else {
            itemDecorations.add(index, decor);
        }
        markItemDecorInsetsDirty();
        requestLayout();
    }

    /**
     * Add an {@link ItemDecoration} to this RecyclerView. Item decorations can
     * affect both measurement and drawing of individual value views.
     *
     * <p>Item decorations are ordered. Decorations placed earlier in the list will
     * be run/queried/drawn first for their effects on value views. Padding added to views
     * will be nested; a padding added by an earlier decoration will mean further
     * value decorations in the list will be asked to draw/pad within the previous decoration's
     * given area.</p>
     *
     * @param decor Decoration to add
     */
    public void addItemDecoration(@NonNull ItemDecoration decor) {
        addItemDecoration(decor, -1);
    }

    /**
     * Returns an {@link ItemDecoration} previously added to this RecyclerView.
     *
     * @param index The index position of the desired ItemDecoration.
     * @return the ItemDecoration at index position
     * @throws IndexOutOfBoundsException on invalid index
     */
    @NonNull
    public ItemDecoration getItemDecorationAt(int index) {
        final int size = getItemDecorationCount();
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index + " is an invalid index for size " + size);
        }

        return itemDecorations.get(index);
    }

    /**
     * Returns the number of {@link ItemDecoration} currently added to this RecyclerView.
     *
     * @return number of ItemDecorations currently added added to this RecyclerView.
     */
    public int getItemDecorationCount() {
        return itemDecorations.size();
    }

    /**
     * Removes the {@link ItemDecoration} associated with the supplied index position.
     *
     * @param index The index position of the ItemDecoration to be removed.
     */
    public void removeItemDecorationAt(int index) {
        final int size = getItemDecorationCount();
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(index + " is an invalid index for size " + size);
        }
        removeItemDecoration(getItemDecorationAt(index));
    }

    /**
     * Remove an {@link ItemDecoration} from this RecyclerView.
     *
     * <p>The given decoration will no longer impact the measurement and drawing of
     * value views.</p>
     *
     * @param decor Decoration to remove
     * @see #addItemDecoration(ItemDecoration)
     */
    public void removeItemDecoration(@NonNull ItemDecoration decor) {
        itemDecorations.remove(decor);
        decor.detachFromView(this);
        if (itemDecorations.isEmpty()) {
            setWillNotDraw(getOverScrollMode() == View.OVER_SCROLL_NEVER);
        }
        markItemDecorInsetsDirty();
        requestLayout();
    }

    protected void clearRecyclerPool(){
        this.recyclerBin.clear();
    }

    public void detachAndScrapAttachedViews(){
        this.recyclerBin.detachAndScrapAttachedViews();
    }

    public void removeAndRecycleView(View childView) {
        recyclerBin.addScarpView(childView);
    }

    /**
     * Create a new view by its view type.
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract View newAdapterView(Context context, ViewGroup parent, int viewType);

    protected View getView(int viewType){
        return recyclerBin.getView(viewType);
    }

    protected void addAdapterView(View childView,int index){
        RecyclerZoomLayout.LayoutParams layoutParams = (RecyclerZoomLayout.LayoutParams) childView.getLayoutParams();
        if(layoutParams.cachedView){
            attachViewToParent(childView,index,layoutParams);
        } else {
            addView(childView, index);
        }
    }

    /**
     * We will remove the view if it still attach in the group.
     * @param view
     */
    protected void removeAdapterView(View view){
        ViewParent parent = view.getParent();
        if(null!=parent){
            detachViewFromParent(view);
        }
    }

    protected void measureChild(View view){
        recyclerBin.measureChild(view);
    }

    public void layoutDecorated(@NonNull View child, int left, int top, int right, int bottom) {
        LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        final Rect insets = layoutParams.decorInsets;
        child.layout(left + insets.left, top + insets.top, right - insets.right, bottom - insets.bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        recyclerBin.setMeasureSpecs(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    public int getLayoutScrollX(){
        return 0;
    }

    public int getLayoutScrollY(){
        return 0;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        overDrawItemDecoration(canvas);
    }

    @Override
    public void draw(Canvas canvas) {
        long st = SystemClock.elapsedRealtime();
        super.draw(canvas);
        drawItemDecoration(canvas);
        if(BuildConfig.DEBUG){
            drawDebugText(canvas,st);
        }
    }

    /**
     * Over draw the value decorations.
     * @param canvas
     */
    protected void overDrawItemDecoration(Canvas canvas) {
        int childCount = getChildCount();
        float matrixScaleX = getLayoutScaleX();
        float matrixScaleY = getLayoutScaleY();
        for(int i=0;i<childCount;i++){
            View childView = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
            for (ItemDecoration itemDecoration:itemDecorations) {
                if(layoutParams.insetsDirty){
                    calculateItemDecorationsForChild(childView,layoutParams.decorInsets);
                }
                itemDecoration.onDraw(canvas,childView,layoutParams.decorInsets,matrixScaleX,matrixScaleY);
            }
        }
        for (ItemDecoration itemDecoration:itemDecorations) {
            itemDecoration.onDraw(canvas,matrixScaleX,matrixScaleY);
        }
    }

    /**
     * Draw the value decoration.
     * @param canvas
     */
    protected void drawItemDecoration(Canvas canvas) {
        int childCount = getChildCount();
        float matrixScaleX = getLayoutScaleX();
        float matrixScaleY = getLayoutScaleY();
        for(int i=0;i<childCount;i++){
            View childView = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
            for (ItemDecoration itemDecoration:itemDecorations) {
                if(layoutParams.insetsDirty){
                    calculateItemDecorationsForChild(childView,layoutParams.decorInsets);
                }
                itemDecoration.onDrawOver(canvas,childView,layoutParams.decorInsets,matrixScaleX,matrixScaleY);
            }
        }
        for (ItemDecoration itemDecoration:itemDecorations) {
            itemDecoration.onDrawOver(canvas,matrixScaleX,matrixScaleY);
        }
    }

    private void drawDebugText(Canvas canvas,long st) {
        Paint textPaint=new Paint();
        textPaint.setColor(Color.WHITE);
        Resources resources = getResources();
        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,16,resources.getDisplayMetrics()));


        int scrapSize=0;
        int viewTypeSize = recyclerBin.scrapViews.size();
        for(int i=0;i<viewTypeSize;i++){
            LinkedList<View> views = recyclerBin.scrapViews.valueAt(i);
            if(null!=views){
                scrapSize+=views.size();
            }
        }
        int childCount = getChildCount();
        long time = SystemClock.elapsedRealtime() - st;
        float layoutScaleX = getLayoutScaleX();
        float layoutScaleY = getLayoutScaleY();
        String text = "scrapSize:" + scrapSize + " childCount:" + childCount+" time:"+time;
        float v = drawText(canvas, textPaint, 0,text);
        text="scaleX:"+String.format("%.2f", layoutScaleX)+" scaleY:"+String.format("%.2f", layoutScaleY);
        drawText(canvas, textPaint, v,text);
    }

    private float drawText(Canvas canvas, Paint textPaint,float offset, String text) {
        int width = getWidth();
        int height = getHeight();
        float textWidth = textPaint.measureText(text, 0, text.length());
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        float textHeight = fontMetricsInt.bottom-fontMetricsInt.top;
        float x = width-textWidth;
        float y = height-textHeight-offset;
        canvas.drawText(text,x,y,textPaint);
        return textHeight;
    }

    private class RecyclerBin{
        SparseArray<LinkedList<View>> scrapViews= new SparseArray<>();
        private int widthMode, heightMode;
        private int width, height;

        void addScarpView(View view){
            removeAdapterView(view);
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.cachedView=true;
            layoutParams.insetsDirty=true;
            int viewType = layoutParams.viewType;
            LinkedList<View> cachedList = scrapViews.get(viewType);
            if(null==cachedList){
                cachedList=new LinkedList<>();
                scrapViews.put(viewType,cachedList);
            }
            cachedList.add(view);
        }

        void detachAndScrapAttachedViews(){
            while(0<getChildCount()){
                View childView = getChildAt(0);
                addScarpView(childView);
            }
        }

        void setMeasureSpecs(int wSpec, int hSpec) {
            width = MeasureSpec.getSize(wSpec);
            widthMode = MeasureSpec.getMode(wSpec);
            if (widthMode == MeasureSpec.UNSPECIFIED) {
                width = 0;
            }
            height = MeasureSpec.getSize(hSpec);
            heightMode = MeasureSpec.getMode(hSpec);
            if (heightMode == MeasureSpec.UNSPECIFIED) {
                height = 0;
            }
        }

        void clear(){
            scrapViews.clear();
        }

        void measureChild(View view){
            int parentWidthMeasureSpec= View.MeasureSpec.makeMeasureSpec(width, widthMode);
            int parentHeightMeasureSpec= View.MeasureSpec.makeMeasureSpec(height, heightMode);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            //Create a new measure spec for the child view.
            int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, 0, layoutParams.width);
            int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec, 0, layoutParams.height);
            view.measure(childWidthMeasureSpec,childHeightMeasureSpec);
        }

        View getView(int viewType){
            View view;
            LinkedList<View> cachedList = scrapViews.get(viewType);
            if(null!=cachedList&&!cachedList.isEmpty()){
                view=cachedList.pollFirst();
            } else{
                Context context = getContext();
                view= newAdapterView(context, RecyclerZoomLayout.this, viewType);
            }
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            calculateItemDecorationsForChild(view,layoutParams.decorInsets);
            layoutParams.viewType=viewType;
            return view;
        }
    }

    Rect getItemDecorInsetsForChild(View child) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (!lp.insetsDirty) {
            return lp.decorInsets;
        }
        final Rect insets = lp.decorInsets;
        insets.set(0, 0, 0, 0);
        final int decorCount = itemDecorations.size();
        for (int i = 0; i < decorCount; i++) {
            tempRect.set(0, 0, 0, 0);
            itemDecorations.get(i).getItemOffsets(tempRect, child);
            insets.left += tempRect.left;
            insets.top += tempRect.top;
            insets.right += tempRect.right;
            insets.bottom += tempRect.bottom;
        }
        lp.insetsDirty = false;
        return insets;
    }

    void markItemDecorInsetsDirty() {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            layoutParams.insetsDirty=true;
        }
    }

    /**
     * Returns the measured width of the given child, plus the additional size of
     * any insets applied by {@link ItemDecoration ItemDecorations}.
     *
     * @param child Child view to query
     * @return child's measured width plus <code>ItemDecoration</code> insets
     *
     * @see View#getMeasuredWidth()
     */
    public int getDecoratedMeasuredWidth(@NonNull View child) {
        final Rect insets = ((LayoutParams) child.getLayoutParams()).decorInsets;
        return child.getMeasuredWidth() + insets.left + insets.right;
    }

    /**
     * Returns the measured height of the given child, plus the additional size of
     * any insets applied by {@link ItemDecoration ItemDecorations}.
     *
     * @param child Child view to query
     * @return child's measured height plus <code>ItemDecoration</code> insets
     *
     * @see View#getMeasuredHeight()
     */
    public int getDecoratedMeasuredHeight(@NonNull View child) {
        final Rect insets = ((LayoutParams) child.getLayoutParams()).decorInsets;
        return child.getMeasuredHeight() + insets.top + insets.bottom;
    }

    /**
     * Returns the left edge of the given child view within its parent, offset by any applied
     * {@link ItemDecoration ItemDecorations}.
     *
     * @param child Child to query
     * @return Child left edge with offsets applied
     * @see #getLeftDecorationWidth(View)
     */
    public int getDecoratedLeft(@NonNull View child) {
        return child.getLeft() - getLeftDecorationWidth(child);
    }

    /**
     * Returns the top edge of the given child view within its parent, offset by any applied
     * {@link ItemDecoration ItemDecorations}.
     *
     * @param child Child to query
     * @return Child top edge with offsets applied
     * @see #getTopDecorationHeight(View)
     */
    public int getDecoratedTop(@NonNull View child) {
        return child.getTop() - getTopDecorationHeight(child);
    }

    /**
     * Returns the right edge of the given child view within its parent, offset by any applied
     * {@link ItemDecoration ItemDecorations}.
     *
     * @param child Child to query
     * @return Child right edge with offsets applied
     * @see #getRightDecorationWidth(View)
     */
    public int getDecoratedRight(@NonNull View child) {
        return child.getRight() + getRightDecorationWidth(child);
    }

    /**
     * Returns the bottom edge of the given child view within its parent, offset by any applied
     * {@link ItemDecoration ItemDecorations}.
     *
     * @param child Child to query
     * @return Child bottom edge with offsets applied
     * @see #getBottomDecorationHeight(View)
     */
    public int getDecoratedBottom(@NonNull View child) {
        return child.getBottom() + getBottomDecorationHeight(child);
    }

    /**
     * Returns the total height of value decorations applied to child's top.
     * <p>
     * Note that this value is not updated until the View is measured or
     * {@link #calculateItemDecorationsForChild(View, Rect)} is called.
     *
     * @param child Child to query
     * @return The total height of value decorations applied to the child's top.
     * @see #getDecoratedTop(View)
     * @see #calculateItemDecorationsForChild(View, Rect)
     */
    public int getTopDecorationHeight(@NonNull View child) {
        return ((LayoutParams) child.getLayoutParams()).decorInsets.top;
    }

    /**
     * Returns the total height of value decorations applied to child's bottom.
     * <p>
     * Note that this value is not updated until the View is measured or
     * {@link #calculateItemDecorationsForChild(View, Rect)} is called.
     *
     * @param child Child to query
     * @return The total height of value decorations applied to the child's bottom.
     * @see #getDecoratedBottom(View)
     * @see #calculateItemDecorationsForChild(View, Rect)
     */
    public int getBottomDecorationHeight(@NonNull View child) {
        return ((LayoutParams) child.getLayoutParams()).decorInsets.bottom;
    }

    /**
     * Returns the total width of value decorations applied to child's left.
     * <p>
     * Note that this value is not updated until the View is measured or
     * {@link #calculateItemDecorationsForChild(View, Rect)} is called.
     *
     * @param child Child to query
     * @return The total width of value decorations applied to the child's left.
     * @see #getDecoratedLeft(View)
     * @see #calculateItemDecorationsForChild(View, Rect)
     */
    public int getLeftDecorationWidth(@NonNull View child) {
        return ((LayoutParams) child.getLayoutParams()).decorInsets.left;
    }

    /**
     * Returns the total width of value decorations applied to child's right.
     * <p>
     * Note that this value is not updated until the View is measured or
     * {@link #calculateItemDecorationsForChild(View, Rect)} is called.
     *
     * @param child Child to query
     * @return The total width of value decorations applied to the child's right.
     * @see #getDecoratedRight(View)
     * @see #calculateItemDecorationsForChild(View, Rect)
     */
    public int getRightDecorationWidth(@NonNull View child) {
        return ((LayoutParams) child.getLayoutParams()).decorInsets.right;
    }

    /**
     * Calculates the value decor insets applied to the given child and updates the provided
     * Rect instance with the inset values.
     * <ul>
     *     <li>The Rect's left is set to the total width of left decorations.</li>
     *     <li>The Rect's top is set to the total height of top decorations.</li>
     *     <li>The Rect's right is set to the total width of right decorations.</li>
     *     <li>The Rect's bottom is set to total height of bottom decorations.</li>
     * </ul>
     * <p>
     * Note that value decorations are automatically calculated when one of the LayoutManager's
     * measure child methods is called. If you need to measure the child with custom specs via
     * {@link View#measure(int, int)}, you can use this method to get decorations.
     *
     * @param child The child view whose decorations should be calculated
     * @param outRect The Rect to hold result values
     */
    public void calculateItemDecorationsForChild(@NonNull View child, @NonNull Rect outRect) {
        Rect insets = getItemDecorInsetsForChild(child);
        outRect.set(insets);
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
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        Context context = getContext();
        return new LayoutParams(context,attrs);
    }

    /**
     * {@link android.view.ViewGroup.MarginLayoutParams LayoutParams} subclass for children of
     * {@link RecyclerZoomLayout}. All the sub-class of this View are encouraged
     * to create their own subclass of this <code>LayoutParams</code> class
     * to store any additional required per-child view metadata about the layout.
     */
    public static class LayoutParams extends ZoomLayout.LayoutParams {
        public final Rect decorInsets = new Rect();
        public boolean insetsDirty=true;
        public boolean cachedView=false;
        public int viewType;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super((ViewGroup.LayoutParams) source);
        }
    }

}
