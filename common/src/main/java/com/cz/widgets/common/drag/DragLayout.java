package com.cz.widgets.common.drag;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cz.widgets.common.ContextHelper;
import com.cz.widgets.common.R;

/**
 * @author Created by cz
 * @date 2020-05-17 21:27
 * @email bingo110@126.com
 */
public class DragLayout extends FrameLayout {
    public static final int NONE=0x00;
    /**
     * Drag horizontally
     */
    public static final int HORIZONTAL=0x01;
    /**
     * Drag vertically
     */
    public static final int VERTICAL=0x02;
    /**
     * Drag from any directions.
     */
    public static final int UNLIMITED=0x04;

    private Rect tempRect = new Rect();
    /**
     * Is begging drag.
     */
    private boolean isBeingDragged = false;
    /**
     * The touch slop value.
     */
    private int touchSlop = 0;
    /**
     * The last motion point:x/y
     */
    private float lastMotionX = 0f;
    private float lastMotionY = 0f;
    /**
     * Current drag view.
     */
    private View dragView=null;

    private SparseArray<DragItem> dragViewArray = new SparseArray<>();

    public DragLayout(@NonNull Context context) {
        super(context);
    }

    public DragLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DragLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        for(int i=0;i<childCount;i++){
            View childView = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
            if(NONE!=layoutParams.dragMode){
                int id = childView.getId();
                if(View.NO_ID==id){
                    childView.setId(View.generateViewId());
                }
                childView.bringToFront();
                dragViewArray.put(id,new DragItem(id,layoutParams.dragMode,layoutParams.dragHandleId,0,0));
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //Make sure we re-layout all the child view we won't ruin our drag locations.
        for(int i = 0; i< dragViewArray.size(); i++){
            DragItem dragItem = dragViewArray.valueAt(i);
            View childView = findViewById(dragItem.id);
            if(0!=dragItem.offsetX||0!=dragItem.offsetY){
                childView.offsetLeftAndRight((int) (dragItem.offsetX-childView.getLeft()));
                childView.offsetTopAndBottom((int) (dragItem.offsetY-childView.getTop()));
            }
        }
    }

    @Nullable
    private View findDragView(View v,int left,int top,float x,float y){
        View findView=null;
        if (v instanceof ViewGroup) {
            int scrollX = v.getScrollX();
            int scrollY = v.getScrollY();
            ViewGroup viewGroup = (ViewGroup) v;
            for (int i=0;i<viewGroup.getChildCount();i++) {
                View child = viewGroup.getChildAt(i);
                int childLeft=left+child.getLeft();
                int childTop=top+child.getTop();
                if (x >= childLeft && x < childLeft +child.getWidth() && y >= childTop && y < childTop +child.getHeight()) {
                    if(0 <= dragViewArray.indexOfKey(child.getId())){
                        return child;
                    } else {
                        findView = findDragView(child,childLeft+scrollX, childTop+scrollY,x,y);
                        if(null==findView&&0 <= dragViewArray.indexOfKey(child.getId())){
                            findView=child;
                        }
                    }
                }
                if(null!=findView){
                    break;
                }
            }
        }
        return findView;
    }

    public void addDragView(@IdRes int id,int dragMode,@IdRes int handleId){
        DragItem dragItem = dragViewArray.get(id);
        if(null==dragItem){
            dragViewArray.put(id,new DragItem(id,dragMode,handleId,0,0));
        }
    }

    public void removeDragView(@IdRes int id){
        dragViewArray.remove(id);
    }

    /**
     * Return the total distance from the child top to this view.
     * @param viewParent
     * @return
     */
    private int getChildViewTop(ViewParent viewParent){
        if(this==viewParent){
            return 0;
        } else {
            int parentTop=0;
            if(viewParent instanceof View){
                View parent = (View) viewParent;
                parentTop=parent.getTop();
            }
            return parentTop+getChildViewTop(viewParent.getParent());
        }
    }

    /**
     * Return the total distance from the child left to this view.
     */
    private int getChildViewLeft(ViewParent viewParent){
        if(this==viewParent){
            return 0;
        } else {
            int parentTop=0;
            if(viewParent instanceof View){
                View parent = (View) viewParent;
                parentTop=parent.getLeft();
            }
            return parentTop+getChildViewLeft(viewParent.getParent());
        }
    }

    private boolean checkDragHandlerView(@Nullable View findDragView,float x,float y) {
        boolean result=true;
        if (null != findDragView) {
            DragItem dragItem = dragViewArray.get(findDragView.getId());
            if(null!=dragItem){
                if (NONE == dragItem.mode) {
                    result=false;
                } else {
                    //Check the drag handle.
                    int dragHandleId = dragItem.handleId;
                    if (View.NO_ID != dragHandleId) {
                        View handleView = findDragView.findViewById(dragHandleId);
                        int childLeft = getChildViewLeft(handleView.getParent());
                        int childTop = getChildViewTop(handleView.getParent());
                        tempRect.set(childLeft, childTop, childLeft + handleView.getWidth(), childTop + handleView.getHeight());
                        if (!tempRect.contains((int)x, (int)y)) {
                            //The point is not in this view.
                            result=false;
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        if(MotionEvent.ACTION_DOWN==action){
            //Find the drag view.
            float x = ev.getX();
            float y = ev.getY();
            View findDragView = findDragView(this,0,0,x,y);
            if(!checkDragHandlerView(findDragView, x, y)){
                findDragView=null;
            }
            if(null!=findDragView){
                int id = findDragView.getId();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //The elevation could make the view display at the top of the layer.
                    findDragView.setElevation(1f);
                    DragItem dragItem = dragViewArray.get(id);
                    if(null!=dragItem){
                        dragItem.elevation=findDragView.getElevation();
                    }
                }
                isBeingDragged=true;
            }
        }
        return isBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if(MotionEvent.ACTION_DOWN==action){
            lastMotionX = event.getX();
            lastMotionY = event.getY();
            ViewParent parent = getParent();
            if(null!=parent){
                parent.requestDisallowInterceptTouchEvent(true);
            }
        } else if(MotionEvent.ACTION_MOVE==action){
            float x = event.getX();
            float y = event.getY();
            float dx = x - lastMotionX;
            float dy = y - lastMotionY;
            if (!isBeingDragged&&((Math.abs(dx) > touchSlop)||Math.abs(dy) > touchSlop)) {
                isBeingDragged = true;
            }
            //If we start drag.
            if (isBeingDragged) {
                lastMotionX =x;
                lastMotionY =y;
                if(null==dragView){
                    View findDragView = findDragView(this,0,0,x,y);
                    if(!checkDragHandlerView(findDragView, x, y)){
                        findDragView=null;
                    }
                    dragView=findDragView;
                }
                if(null==dragView){
                    //If we didn't found a drag view. we are not supposed to do anything.
                    isBeingDragged=false;
                }
                offsetDrawView(dragView,dx,dy);
            }
        } else if(MotionEvent.ACTION_UP==action||MotionEvent.ACTION_CANCEL==action){
            releaseDrag();
        }
        return isBeingDragged;
    }

    private void offsetDrawView(@Nullable View dragView,float dx,float dy) {
        if(null==dragView) return;
        int offsetX=Math.round(dx);
        int offsetY=Math.round(dy);
        DragItem dragItem = dragViewArray.get(dragView.getId());
        if(null!=dragItem){
            int dragMode = dragItem.mode;
            if(0==(HORIZONTAL & dragMode)){
                offsetX=0;
            }
            if(0==(VERTICAL & dragMode)){
                offsetY=0;
            }
            if(0==(UNLIMITED & dragMode)){
                //Check the boundary.
                ViewParent parent = dragView.getParent();
                if(null!=parent&&parent instanceof ViewGroup) {
                    ViewGroup viewParent = (ViewGroup) parent;
                    int width = viewParent.getWidth();
                    int height = viewParent.getHeight();
                    int left = dragView.getLeft();
                    int right = dragView.getRight();
                    if(offsetX<0&&left+offsetX<0){
                        offsetX=-left;
                    } else if(offsetX>0&&right+offsetX>width){
                        offsetX=right > width ? 0 : width-right;
                    }
                    int top = dragView.getTop();
                    int bottom = dragView.getBottom();
                    if(offsetY<0&&top+offsetY<0){
                        offsetY=-top;
                    } else if(offsetY>0&&bottom+offsetY>height){
                        offsetY=bottom > height ? 0 : height-bottom;
                    }
                }
            }
            dragView.offsetLeftAndRight(offsetX);
            dragView.offsetTopAndBottom(offsetY);
        }
    }

    private void releaseDrag() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(null!=dragView){
                int id = dragView.getId();
                DragItem dragItem = dragViewArray.get(id);
                if(null!=dragItem){
                    dragView.setElevation(dragItem.elevation);
                }
            }
        }
        dragView=null;
        lastMotionX=0f;
        lastMotionY=0f;
        isBeingDragged = false;
    }

    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /**
     * Generate a default layout params.
     * When you call {@link ViewGroup#addView(View)}.
     * It will ask for a default LayoutParams
     * @return
     */
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Create a layout params from a giving one.
     * @param p
     * @return
     */
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        Context context = getContext();
        return new LayoutParams(context,attrs);
    }
    /**
     * This class is to create our own subclass of this <code>FrameLayout.LayoutParams</code> class
     * to store any additional required per-child view metadata about the layout.
     */
    public static class LayoutParams extends FrameLayout.LayoutParams {
        /**
         * The drag mode.
         */
        public int dragMode= NONE;
        /**
         * The handle view id.
         */
        public int dragHandleId= View.NO_ID;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.DragLayout);
            dragMode=a.getInt(R.styleable.DragLayout_layout_dragMode, NONE);
            dragHandleId=a.getResourceId(R.styleable.DragLayout_layout_dragHandle,View.NO_ID);
            a.recycle();
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

    class DragItem {
        @IdRes
        final int id;
        final int mode;
        @IdRes
        final int handleId;
        final float offsetX;
        final float offsetY;

        float elevation=0f;

        public DragItem(int id, int mode, int handleId, float offsetX, float offsetY) {
            this.id = id;
            this.mode = mode;
            this.handleId = handleId;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }
    }
}
