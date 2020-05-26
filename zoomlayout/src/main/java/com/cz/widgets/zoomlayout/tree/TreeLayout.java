package com.cz.widgets.zoomlayout.tree;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.cz.widgets.zoomlayout.ItemDecoration;
import com.cz.widgets.zoomlayout.R;
import com.cz.widgets.zoomlayout.RecyclerZoomLayout;
import com.cz.widgets.zoomlayout.ZoomOrientationHelper;

import org.jetbrains.annotations.NotNull;

/**
 * @author Created by cz
 * @date 2020-05-13 20:45
 * @email bingo110@126.com
 */
public class TreeLayout extends RecyclerZoomLayout implements Previewable {
    private static final String TAG="HierarchyLayout";
    private static final int DIRECTION_START = -1;
    private static final int DIRECTION_END = 1;
    private final ZoomOrientationHelper orientationHelper;
    private LayoutState layoutState = new LayoutState();
    private TreeNodeIndexer treeNodeIndexer;
    private final Rect hintRect=new Rect();
    /**
     * It has a stable size. This means all the node size is the same.
     * So I won't have to measure too many times. And I could use one node's dimension to pre-layout all the nodes.
     */
    private boolean hasFixSize=true;
    /**
     * The hierarchy tree adapter.
     */
    private Adapter adapter;


    public TreeLayout(Context context) {
        this(context,null, R.attr.hierarchyLayout);
    }

    public TreeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.hierarchyLayout);
    }

    public TreeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        this.orientationHelper = ZoomOrientationHelper.createOrientationHelper(this,ZoomOrientationHelper.VERTICAL);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HierarchyLayout, defStyleAttr, R.style.HierarchyLayout);
        a.recycle();
    }

    public void setAdapter(@NonNull Adapter adapter) {
        this.adapter = adapter;
        this.layoutState.structureChanged=true;
        removeAllViews();
        clearRecyclerPool();
        requestLayout();
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public void setHasFixSize(boolean hasStableSize){
        this.hasFixSize =hasStableSize;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(null!=adapter&&layoutState.structureChanged){
            //Change the bool to avoid do all the operation many times.
            layoutState.structureChanged=false;
            detachAndScrapAttachedViews();
            //Step1: Add the first baseline view.
            //We don't have to scroll to anywhere. Because we just offset the location of all the child views.
            //So here the only thing we have to do is layout the root node.
            TreeNode hierarchyNode = adapter.root;
            View view = getViewAndMeasured(hierarchyNode);
            int paddingTop = getPaddingTop();
            int paddingLeft = getPaddingLeft();
            int decoratedMeasuredWidth = getDecoratedMeasuredWidth(view);
            int decoratedMeasuredHeight = getDecoratedMeasuredHeight(view);
            //Initialize the indexer
            treeNodeIndexer=new TreeNodeIndexer(hierarchyNode,TreeNodeIndexer.VERTICAL,decoratedMeasuredWidth,decoratedMeasuredHeight);

            int left=paddingLeft+hierarchyNode.centerBreadth*decoratedMeasuredWidth;
            int top=paddingTop+hierarchyNode.depth * decoratedMeasuredHeight;

            layoutDecorated(view,left,top,left+decoratedMeasuredWidth,top+decoratedMeasuredHeight);
            addAdapterView(view,-1);
            //Step2: Fill the content.
            int totalSpaceInOther = orientationHelper.getTotalSpaceInOther();
            //Update the scroll location. We move the node to the vertical center.
            dispatchScrollChange(left-(totalSpaceInOther-decoratedMeasuredWidth)/2,0);
            layoutState.scrollTo(left-(totalSpaceInOther-decoratedMeasuredWidth)/2,0);
            //Fill the window
            fillHierarchyLayout();
        }
    }

    @NotNull
    private View getViewAndMeasured(TreeNode hierarchyNode) {
        Context context = getContext();
        int viewType = adapter.getViewType(hierarchyNode);
        View view = getView(viewType);
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        layoutParams.treeNode=hierarchyNode;
        adapter.onBindView(context, view, hierarchyNode, hierarchyNode.value);
        measureChild(view);
        return view;
    }

    private<E> View addHierarchyViewInternal(TreeNode<E> hierarchyNode){
        View child = getViewAndMeasured(hierarchyNode);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int decoratedMeasuredWidth = getDecoratedMeasuredWidth(child);
        int decoratedMeasuredHeight = getDecoratedMeasuredHeight(child);

        int left=paddingLeft+hierarchyNode.centerBreadth*decoratedMeasuredWidth;
        int top=paddingTop+hierarchyNode.depth * decoratedMeasuredHeight;
        layoutDecorated(child,left,top,left+decoratedMeasuredWidth,top+decoratedMeasuredHeight);
        offsetChild(child,-layoutState.scrollX,-layoutState.scrollY);
        addAdapterView(child,-1);
        return child;
    }

    /**
     * Totally fill the hierarchy layout.
     */
    private void fillHierarchyLayout() {
        int totalSpace = orientationHelper.getTotalSpace();
        int totalSpaceInOther = orientationHelper.getTotalSpaceInOther();
        hintRect.set(layoutState.scrollX,layoutState.scrollY, layoutState.scrollX+totalSpaceInOther, layoutState.scrollY+totalSpace);
        //If the screen range contains the table cell. like: h:1-3 v-1-2
        int left = treeNodeIndexer.findHierarchyBreadthIndex(hintRect.left);
        int right = treeNodeIndexer.findHierarchyBreadthIndex(hintRect.right);

        int top = treeNodeIndexer.findHierarchyDepthIndex(hintRect.top);
        int bottom = treeNodeIndexer.findHierarchyDepthIndex(hintRect.bottom);

        //Update the layout state.
        layoutState.rect.set(left,top,right,bottom);

        //Fill the layout.
        detachAndScrapAttachedViews();
        fillHierarchyLayout(left,top,right,bottom);
    }

    @Override
    protected int scrollHorizontallyBy(int dx, boolean isScaleDragged) {
        int totalSpaceInOther = orientationHelper.getTotalSpaceInOther();
        //If the screen range contains the table cell. like: h:1-3 v-1-2
        int paddingLeft = getPaddingLeft();
        int left = dx + layoutState.scrollX;
        int right = left + totalSpaceInOther;
        int dimensionWidth = treeNodeIndexer.getTreeMeasuredWidth();
        //Check the boundary of the screen horizontally
        int scrolled = dx;
        if(left < paddingLeft){
            //to left
            scrolled=-layoutState.scrollX;
        } else if (right > dimensionWidth) {
            //to right
            scrolled = dimensionWidth-(right-dx);
        }
        if(0 == scrolled){
            return scrolled;
        }
        left = scrolled + layoutState.scrollX;
        right = left + totalSpaceInOther;
        int leftHierarchyDepthIndex = treeNodeIndexer.findHierarchyBreadthIndex(left);
        int rightHierarchyDepthIndex = treeNodeIndexer.findHierarchyBreadthIndex(right);
        int layoutDirection = dx > 0 ? DIRECTION_END : DIRECTION_START;
        if(isScaleDragged||DIRECTION_START==layoutDirection){
            //Move backward.
            if(layoutState.rect.right>rightHierarchyDepthIndex){
                layoutState.rect.right=rightHierarchyDepthIndex;
            }
            if(layoutState.rect.left<leftHierarchyDepthIndex){
                //When scaling the content. The right side of the hierarchy depth will less than the current depth.
                layoutState.rect.left=leftHierarchyDepthIndex;
            } else if(layoutState.rect.left>leftHierarchyDepthIndex){
                int oldHierarchyDepthIndex = layoutState.rect.left;
                layoutState.rect.left=leftHierarchyDepthIndex;
                //Move forward.
                fillHierarchyLayout(leftHierarchyDepthIndex,layoutState.rect.top,oldHierarchyDepthIndex-1,layoutState.rect.bottom);
            }
        }
        if(isScaleDragged||DIRECTION_END==layoutDirection){
            if(layoutState.rect.left<leftHierarchyDepthIndex){
                layoutState.rect.left=leftHierarchyDepthIndex;
            }
            if(layoutState.rect.right>rightHierarchyDepthIndex){
                //When scaling the content. The right side of the hierarchy depth will less than the current depth.
                layoutState.rect.right=rightHierarchyDepthIndex;
            } else if(layoutState.rect.right<rightHierarchyDepthIndex){
                //Moving forward.
                int oldHierarchyDepthIndex = layoutState.rect.right;
                layoutState.rect.right=rightHierarchyDepthIndex;
                fillHierarchyLayout(oldHierarchyDepthIndex+1,layoutState.rect.top,rightHierarchyDepthIndex,layoutState.rect.bottom);
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
        int treeMeasuredHeight = treeNodeIndexer.getTreeMeasuredHeight();
        int scrolled = dy;
        if(top < paddingTop){
            //to top
            scrolled=-layoutState.scrollY;
        } else if (bottom > treeMeasuredHeight) {
            //to bottom
            scrolled = treeMeasuredHeight-(bottom-dy);
        }
        if(0 == scrolled){
            return scrolled;
        }
        top = scrolled+layoutState.scrollY;
        bottom = top + totalSpace;
        int topHierarchyBreadthIndex = treeNodeIndexer.findHierarchyDepthIndex(top);
        int bottomHierarchyBreadthIndex = treeNodeIndexer.findHierarchyDepthIndex(bottom);
        int layoutDirection = dy > 0 ? DIRECTION_END : DIRECTION_START;
        if(isScaleDragged||DIRECTION_START==layoutDirection){
            //Move backward.
            if(layoutState.rect.bottom>bottomHierarchyBreadthIndex){
                layoutState.rect.bottom=bottomHierarchyBreadthIndex;
            }
            if(layoutState.rect.top>topHierarchyBreadthIndex){
                int oldHierarchyBreadthIndex = layoutState.rect.top;
                layoutState.rect.top=topHierarchyBreadthIndex;
                //Move forward.
                fillHierarchyLayout(layoutState.rect.left,topHierarchyBreadthIndex,layoutState.rect.right,oldHierarchyBreadthIndex-1);
            }
        }
        if(isScaleDragged||DIRECTION_END==layoutDirection){
            if(layoutState.rect.top<topHierarchyBreadthIndex){
                layoutState.rect.top=topHierarchyBreadthIndex;
            }
            if(layoutState.rect.bottom>bottomHierarchyBreadthIndex){
                //When scaling the content. The right side of the hierarchy depth will less than the current depth.
                layoutState.rect.bottom=bottomHierarchyBreadthIndex;
            } else if(layoutState.rect.bottom<bottomHierarchyBreadthIndex){
                int oldHierarchyBreadthIndex = layoutState.rect.bottom;
                layoutState.rect.bottom=bottomHierarchyBreadthIndex;
                //Move forward.
                fillHierarchyLayout(layoutState.rect.left,oldHierarchyBreadthIndex+1,layoutState.rect.right,bottomHierarchyBreadthIndex);
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

    /**
     * Recycler all the child views that out of the screen.
     * @param layoutState
     */
    private void recycleByLayoutState(LayoutState layoutState) {
        Rect rect = layoutState.rect;
        for(int i=0;i<getChildCount();){
            View childView = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
            TreeNode treeNode = layoutParams.treeNode;
            if(null!=treeNode){
                int x = treeNode.centerBreadth;
                //We use center breadth to layout.
                int y = treeNode.depth;
                //Check the rect including the empty rect. This is not like the class:Rect#contains(x,y)
                if(rect.left <= rect.right && rect.top <= rect.bottom
                        && x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom){
                    //Still in this screen.
                    i++;
                } else {
                    //This view is out of screen.
                    removeAndRecycleView(childView);
                }
            }
        }
    }

    /**
     * Fill the layout by the new rectangle value.
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    private void fillHierarchyLayout(int left,int top,int right,int bottom){
        for(int depth=top;depth<=bottom;depth++){
            for(int breadth= left;breadth<=right;breadth++){
                TreeNode treeNode = treeNodeIndexer.findTreeNode(breadth,depth);
                if(null!=treeNode){
                    addHierarchyViewInternal(treeNode);
                }
            }
        }
    }

    @Override
    protected View newAdapterView(Context context, ViewGroup parent, int viewType) {
        if(null==adapter){
            throw new NullPointerException("The adapter is null!");
        }
        return adapter.onCreateView(parent,viewType);
    }


    @Override
    public void draw(Canvas canvas) {
        long st= SystemClock.elapsedRealtime();
        super.draw(canvas);
        Log.i(TAG,"draw:"+(SystemClock.elapsedRealtime()-st));
    }

    @Override
    public View newPreview() {
        Context context = getContext();
        //We lightly change the class to draw the preview efficiently.
        TreeLayout layout=new TreeLayout(context){
            private View childView;

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                //Measure all the tree nodes completely. This is generally for the preview layout.
                if(null!=adapter&&null!=adapter.root){
                    TreeNode hierarchyNode = adapter.root;
                    View child = getViewAndMeasured(hierarchyNode);
                    int decoratedMeasuredWidth = getDecoratedMeasuredWidth(child);
                    int decoratedMeasuredHeight = getDecoratedMeasuredHeight(child);
                    this.childView=child;
                    //Initialize the indexer
                    TreeNodeIndexer treeNodeIndexer=new TreeNodeIndexer(hierarchyNode,TreeNodeIndexer.VERTICAL,decoratedMeasuredWidth,decoratedMeasuredHeight);
                    int treeMeasuredWidth = treeNodeIndexer.getTreeMeasuredWidth();
                    int treeMeasuredHeight = treeNodeIndexer.getTreeMeasuredHeight();
                    setMeasuredDimension(treeMeasuredWidth,treeMeasuredHeight);
                }
            }

            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
            }

            @Override
            public void addItemDecoration(@NonNull ItemDecoration decor, int index) {
            }

            @Override
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                Adapter adapter = getAdapter();
                if (null != childView && null != adapter && null != adapter.root) {
                    TreeNode hierarchyNode = adapter.root;
                    drawHierarchyTree(canvas, adapter, hierarchyNode, childView);
                }
            }

            /**
             * Draw all the tree nodes all at once.
             * @param hierarchyNode
             * @param <E>
             */
            private<E> void drawHierarchyTree(Canvas canvas,Adapter adapter,TreeNode<E> hierarchyNode, View child) {
                //This will add additional extra drawing time from 197 to 691
//                Context context = getContext();
//                adapter.onBindView(context,childView,hierarchyNode,hierarchyNode.value);
                int paddingLeft = getPaddingLeft();
                int paddingTop = getPaddingTop();
                int decoratedMeasuredWidth = getDecoratedMeasuredWidth(child);
                int decoratedMeasuredHeight = getDecoratedMeasuredHeight(child);
                int left=paddingLeft+hierarchyNode.centerBreadth*decoratedMeasuredWidth;
                int top=paddingTop+hierarchyNode.depth * decoratedMeasuredHeight;
                layoutDecorated(child,left,top,left+decoratedMeasuredWidth,top+decoratedMeasuredHeight);
                canvas.save();
                canvas.translate(left,top);
                child.draw(canvas);
                canvas.restore();
                //Continue loop;
                for(TreeNode<E> childNode:hierarchyNode.children){
                    drawHierarchyTree(canvas,adapter,childNode,child);
                }
            }
        };
        layout.setAdapter(adapter);
        return layout;
    }

    @Override
    public void onChildChange(Canvas canvas, View child) {
        LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        TreeNode treeNode = layoutParams.treeNode;
        if(null!=treeNode){
            int paddingLeft = getPaddingLeft();
            int paddingTop = getPaddingTop();
            int decoratedMeasuredWidth = getDecoratedMeasuredWidth(child);
            int decoratedMeasuredHeight = getDecoratedMeasuredHeight(child);

            int left=paddingLeft+treeNode.centerBreadth*decoratedMeasuredWidth;
            int top=paddingTop+treeNode.depth * decoratedMeasuredHeight;
            canvas.save();
            canvas.translate(left,top);
            child.draw(canvas);
            canvas.restore();
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
     * {@link MarginLayoutParams LayoutParams} subclass for children of
     * {@link RecyclerZoomLayout}. All the sub-class of this View are encouraged
     * to create their own subclass of this <code>LayoutParams</code> class
     * to store any additional required per-child view metadata about the layout.
     */
    public static class LayoutParams extends RecyclerZoomLayout.LayoutParams {
        public TreeNode treeNode;

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


    /**
     * Created by cz on 2017/10/13.
     * The hierarchy data adapter.
     */
    public abstract static class Adapter<E>{
        @NonNull
        final TreeNode<E> root;

        public Adapter(@NonNull TreeNode<E> root) {
            this.root = root;
        }

        public int getViewType(TreeNode<E> node){
            return 0;
        }

        /**
         * Return a new view from adapter.
         * @param parent
         * @return
         */
        public abstract View onCreateView(@NonNull ViewGroup parent,int viewType);


        public abstract void onBindView(Context context, @NonNull View view, @NonNull TreeNode<E> node, @NonNull E item);
    }


    private class LayoutState {
        /**
         * If the data structure has changed. We will fillTableAndRecycle the content again.
         */
        boolean structureChanged = false;
        /**
         * The rectangle represents the whole screen scroll value.
         * But not the dimension size. We use the rect as a table size.
         * Like both left and right is represent the depth of tree.
         */
        final Rect rect=new Rect();
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
