package com.cz.widgets.zoomlayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.OverScroller;

import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.view.ViewCompat;

/**
 * @author Created by cz
 * @date 2020-03-12 20:21
 * @email bingo110@126.com
 * A view group that we could zoom the sub-view.
 * This view has a few feathers above:
 * 1. You could be able to zoom the View
 * 2. Add sub-view like a ViewGroup
 * 3. Support {@link View#setOnClickListener(OnClickListener)} and {@link View#setOnLongClickListener(OnLongClickListener)}
 * 4. Support background drawable.
 * It's a totally abstract zoom layout.
 *
 */
public abstract class ZoomLayout extends ViewGroup implements ScaleGestureDetector.OnScaleGestureListener{
    private static final String TAG="ZoomLayout";
    /**
     * When set, this ViewGroup should not intercept touch events.
     * {@hide}
     */
    protected static final int FLAG_DISALLOW_INTERCEPT = 0x01;
    /**
     * Indicates whether the view is temporarily detached.
     *
     * @hide
     */
    static final int FLAG_CANCEL_NEXT_UP_EVENT = 0x02;
    /**
     * Check the view state changed. When a view change its whatever drawable state or something related to the state.
     * We post this runnable to update the preview.
     */
    private final CheckForState checkForState=new CheckForState();
    private final ScaleGestureDetector scaleGestureDetector;
    private final ViewFlinger viewFlinger;
    private final Matrix matrix=new Matrix();
    private float[] m = new float[9];
    private ValueAnimator zoomAnimator=null;
    /**
     * The minimum value we could narrow
     */
    private float zoomMinimum=0;
    /**
     * The maximum value we could expand
     */
    private float zoomMaximum=0;
    /**
     * Since we start to zoom. When the finger moved. The focus will moved too.
     * We keep the touchdown focus point x,y to avoid the change.
     */
    private float scaleFocusX =0f;
    private float scaleFocusY =0f;
    private float scaleScrollX;
    private float scaleScrollY;
    private boolean zoomEnabled;

    protected int groupFlags;
    // Target of Motion events
    private View motionTarget;
    private final Rect tempRect = new Rect();
    private boolean isBeingDragged = false;
    private boolean isScaleDragged = false;
    private int touchSlop;
    private float lastMotionX = 0f;
    private float lastMotionY = 0f;

    private VelocityTracker velocityTracker = null;
    private int minimumVelocity;
    private int maximumVelocity;

    private OnLayoutScrollChangeListener scrollChangeListener;
    private OnLayoutScaleChangeListener scaleChangeListener;
    private OnLayoutChildDrawableStateChanged layoutChildDrawableStateChangeListener;

    public ZoomLayout(Context context) {
        this(context,null, R.attr.zoomLayout);
    }

    public ZoomLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.zoomLayout);
    }

    public ZoomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
        minimumVelocity = configuration.getScaledMinimumFlingVelocity();
        maximumVelocity = configuration.getScaledMaximumFlingVelocity();

        scaleGestureDetector = new ScaleGestureDetector(context, this);
        viewFlinger=new ViewFlinger(context);


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZoomLayout, defStyleAttr, R.style.ZoomLayout);
        setZoomEnabled(a.getBoolean(R.styleable.ZoomLayout_zoom_enabled,true));
        setZoomMinimum(a.getFloat(R.styleable.ZoomLayout_zoom_minimum,0f));
        setZoomMaximum(a.getFloat(R.styleable.ZoomLayout_zoom_maximum,0f));
        a.recycle();
    }

    public void setZoomEnabled(boolean enabled) {
        this.zoomEnabled=enabled;
    }

    private void setZoomMinimum(float minimum) {
        this.zoomMinimum=minimum;
    }

    private void setZoomMaximum(float maximum) {
        this.zoomMaximum=maximum;
    }

    public float getLayoutScaleX() {
        matrix.getValues(m);
        return m[Matrix.MSCALE_X];
    }

    public float getLayoutScaleY(){
        matrix.getValues(m);
        return m[Matrix.MSCALE_Y];
    }

    @Override
    protected abstract void onLayout(boolean changed, int left, int top, int right, int bottom);

    /**
     * We override this method to support the children's disallow intercept event.
     * @param disallowIntercept
     */
    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
        if (disallowIntercept) {
            groupFlags |= FLAG_DISALLOW_INTERCEPT;
        } else {
            groupFlags &= ~FLAG_DISALLOW_INTERCEPT;
        }
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        groupFlags |= FLAG_CANCEL_NEXT_UP_EVENT;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        groupFlags &= ~FLAG_CANCEL_NEXT_UP_EVENT;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!onFilterTouchEventForSecurity(ev)) {
            return false;
        }
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        final int action = ev.getActionMasked();
        final float xf = ev.getX();
        final float yf = ev.getY();
        final float scrolledXFloat = xf + scrollX;
        final float scrolledYFloat = yf + scrollY;
        final Rect frame = tempRect;

        boolean disallowIntercept = (groupFlags & FLAG_DISALLOW_INTERCEPT) != 0;

        if (action == MotionEvent.ACTION_DOWN) {
            if (motionTarget != null) {
                // this is weird, we got a pen down, but we thought it was
                // already down!
                // XXX: We should probably send an ACTION_UP to the current
                // target.
                motionTarget = null;
            }
            // If we're disallowing intercept or if we're allowing and we didn't
            // intercept
            if (disallowIntercept || !onInterceptTouchEvent(ev)) {
                // reset this event's action (just to protect ourselves)
                ev.setAction(MotionEvent.ACTION_DOWN);
                // We know we want to dispatch the event down, find a child
                // who can handle it, start with the front-most child.
                final int scrolledXInt = (int) scrolledXFloat;
                final int scrolledYInt = (int) scrolledYFloat;
                final int count = getChildCount();

                float matrixScaleX = getLayoutScaleX();
                float matrixScaleY = getLayoutScaleY();
                for (int i = count - 1; i >= 0; i--) {
                    final View child = getChildAt(i);
                    if (child.getVisibility() == VISIBLE || child.getAnimation() != null) {
                        child.getHitRect(frame);
                        frame.set((int)(frame.left*matrixScaleX),
                                (int)(frame.top*matrixScaleY),
                                (int)(frame.right*matrixScaleX),
                                (int)(frame.bottom*matrixScaleY));
                        if (frame.contains(scrolledXInt, scrolledYInt)) {
                            // offset the event to the view's coordinate system
                            final float xc = scrolledXFloat - frame.left;
                            final float yc = scrolledYFloat - frame.top;
                            ev.setLocation(xc/matrixScaleX, yc/matrixScaleY);
                            if (child.dispatchTouchEvent(ev)){
                                // Event handled, we have a target now.
                                motionTarget = child;
                                return true;
                            }
                            // The event didn't get handled, try the next view.
                            // Don't reset the event's location, it's not
                            // necessary here.
                        }
                    }
                }
            }
        }

        boolean isUpOrCancel = (action == MotionEvent.ACTION_UP) ||
                (action == MotionEvent.ACTION_CANCEL);

        if (isUpOrCancel) {
            // Note, we've already copied the previous state to our local
            // variable, so this takes effect on the next event
            groupFlags &= ~FLAG_DISALLOW_INTERCEPT;
        }

        // The event wasn't an ACTION_DOWN, dispatch it to our target if
        // we have one.
        View target = motionTarget;
        if (target == null) {
            if ((groupFlags & FLAG_CANCEL_NEXT_UP_EVENT) != 0) {
                ev.setAction(MotionEvent.ACTION_CANCEL);
                groupFlags &= ~FLAG_CANCEL_NEXT_UP_EVENT;
            }
            ev.setLocation(xf, yf);
            //We handle this event by dispatching this event to this method.
            return onTouchEvent(ev);
        }

        // if have a target, see if we're allowed to and want to intercept its
        // events
        if (!disallowIntercept && onInterceptTouchEvent(ev)) {
            int left = target.getLeft();
            int top = target.getTop();
            float matrixScaleX = getLayoutScaleX();
            float matrixScaleY = getLayoutScaleY();
            final float xc = scrolledXFloat - left*matrixScaleX;
            final float yc = scrolledYFloat - top*matrixScaleY;
            groupFlags &= ~FLAG_CANCEL_NEXT_UP_EVENT;
            ev.setAction(MotionEvent.ACTION_CANCEL);
            ev.setLocation(xc/matrixScaleX, yc/matrixScaleY);
            if (!target.dispatchTouchEvent(ev)) {
                // target didn't handle ACTION_CANCEL. not much we can do
                // but they should have.
            }
            // clear the target
            motionTarget = null;
            // Don't dispatch this event to our own view, because we already
            // saw it when intercepting; we just want to give the following
            // event to the normal onTouchEvent().
            return true;
        }

        if (isUpOrCancel) {
            motionTarget = null;
        }

        // finally offset the event to the target's coordinate system and
        // dispatch the event.
        int left = target.getLeft();
        int top = target.getTop();
        float matrixScaleX = getLayoutScaleX();
        float matrixScaleY = getLayoutScaleY();
        final float xc = scrolledXFloat - left*matrixScaleX;
        final float yc = scrolledYFloat - top*matrixScaleY;
        ev.setLocation(xc/matrixScaleX, yc/matrixScaleY);
        //The target child view does not exist.
        if (-1==indexOfChild(target)) {
            ev.setAction(MotionEvent.ACTION_CANCEL);
            motionTarget = null;
        }
        return target.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        if (super.onInterceptTouchEvent(ev)) {
            return true;
        }
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            releaseDrag();
            return false;
        }
        if (action != MotionEvent.ACTION_DOWN&&isBeingDragged) {
            return true;
        }
        if(MotionEvent.ACTION_DOWN==action) {
            viewFlinger.abortAnimation();
            lastMotionX = ev.getX();
            lastMotionY = ev.getY();
        } else if(MotionEvent.ACTION_MOVE==action){
            float x = ev.getX();
            float y = ev.getY();
            float dx = x - lastMotionX;
            float dy = y - lastMotionY;
            if (Math.abs(dx) > touchSlop||Math.abs(dy) > touchSlop) {
                isBeingDragged = true;
                ViewParent parent = getParent();
                if(null!=parent){
                    parent.requestDisallowInterceptTouchEvent(true);
                }
            }
        } else if(MotionEvent.ACTION_UP==action||MotionEvent.ACTION_CANCEL==action) {
            releaseDrag();
        }
        return isBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //Process the scale gesture.
        if(zoomEnabled){
            scaleGestureDetector.onTouchEvent(ev);
        }
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(ev);
        int action = ev.getActionMasked();
        if(MotionEvent.ACTION_DOWN==action){
            lastMotionX = ev.getX();
            lastMotionY = ev.getY();
            viewFlinger.abortAnimation();
            invalidate();
            ViewParent parent = getParent();
            if(null!=parent){
                parent.requestDisallowInterceptTouchEvent(true);
            }
        } else if(MotionEvent.ACTION_MOVE==action){
            float x = ev.getX();
            float y = ev.getY();
            float dx = x - lastMotionX;
            float dy = y - lastMotionY;
            if (!isScaleDragged&&!isBeingDragged&&(Math.abs(dx) > touchSlop||Math.abs(dy) > touchSlop)) {
                isBeingDragged = true;
                lastMotionX = x;
                lastMotionY = y;
                ViewParent parent = getParent();
                if(null!=parent){
                    parent.requestDisallowInterceptTouchEvent(true);
                }
            }
            //To avoid the scale gesture. We check the pointer count.
            int pointerCount = ev.getPointerCount();
            if (1==pointerCount&&!isScaleDragged&&isBeingDragged) {
                lastMotionX = x;
                lastMotionY = y;
                float matrixScaleX = getLayoutScaleX();
                float matrixScaleY = getLayoutScaleY();
                int scaleDx = Math.round(dx / matrixScaleX);
                int scaleDy = Math.round(dy / matrixScaleY);
                scrollBy(-scaleDx,-scaleDy);
                invalidate();
            }
        } else if(MotionEvent.ACTION_UP==action){
            if(!isScaleDragged&&null!=velocityTracker){
                float matrixScaleX = getLayoutScaleX();
                float matrixScaleY = getLayoutScaleY();
                velocityTracker.computeCurrentVelocity(1000,maximumVelocity);
                float xVelocity = velocityTracker.getXVelocity();
                float yVelocity = velocityTracker.getYVelocity();
                if(Math.abs(xVelocity)>minimumVelocity||Math.abs(yVelocity)>minimumVelocity){
                    viewFlinger.fling(-xVelocity/matrixScaleX,-yVelocity/matrixScaleY);
                }
            }
            releaseDrag();
        } else if(MotionEvent.ACTION_CANCEL==action){
            releaseDrag();
        }
        return true;
    }

    /**
     * Tests scrollability within child views of v given a delta of dx.
     *
     * @param v View to test for horizontal scrollability
     * @param dx Delta scrolled in pixels
     * @param x X coordinate of the active touch point
     * @param y Y coordinate of the active touch point
     * @return true if child views of v can be scrolled by delta of dx.
     */
    protected boolean canChildViewScrollHorizontally(View v, int dx, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();
            final int count = group.getChildCount();
            // Count backwards - let topmost views consume scroll distance first.
            for (int i = count - 1; i >= 0; i--) {
                // TODO: Add versioned support here for transformed views.
                // This will not work for transformed views in Honeycomb+
                final View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight()
                        && y + scrollY >= child.getTop() && y + scrollY < child.getBottom()
                        && canChildViewScrollHorizontally(child, dx, x + scrollX - child.getLeft(),
                        y + scrollY - child.getTop())) {
                    return true;
                }
            }
        }

        return v.canScrollHorizontally(-dx);
    }
    /**
     * Tests scrollability within child views of v given a delta of dx.
     *
     * @param v View to test for horizontal scrollability
     * @param dx Delta scrolled in pixels
     * @param x X coordinate of the active touch point
     * @param y Y coordinate of the active touch point
     * @return true if child views of v can be scrolled by delta of dx.
     */
    protected boolean canChildViewScrollVertically(View v, int dx, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();
            final int count = group.getChildCount();
            // Count backwards - let topmost views consume scroll distance first.
            for (int i = count - 1; i >= 0; i--) {
                // TODO: Add versioned support here for transformed views.
                // This will not work for transformed views in Honeycomb+
                final View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight()
                        && y + scrollY >= child.getTop() && y + scrollY < child.getBottom()
                        && canChildViewScrollVertically(child, dx, x + scrollX - child.getLeft(),
                        y + scrollY - child.getTop())) {
                    return true;
                }
            }
        }

        return v.canScrollHorizontally(-dx);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor=detector.getScaleFactor();
        float oldMatrixScaleX = getLayoutScaleX();
        float oldMatrixScaleY = getLayoutScaleY();
        if(zoomMinimum>scaleFactor*oldMatrixScaleX){
            scaleFactor=zoomMinimum/oldMatrixScaleX;
        } else if(zoomMaximum<scaleFactor*oldMatrixScaleX){
            scaleFactor=zoomMaximum/oldMatrixScaleX;
        }
        matrix.postScale(scaleFactor, scaleFactor);
        setViewScaleInternal(oldMatrixScaleX,oldMatrixScaleY,scaleFocusX,scaleFocusY);
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        cancelZoomAnimator();
        scaleScrollX=0;
        scaleScrollY=0;
        isScaleDragged=true;
        float matrixScaleX = getLayoutScaleX();
        float matrixScaleY = getLayoutScaleY();
        scaleFocusX=detector.getFocusX()/matrixScaleX;
        scaleFocusY=detector.getFocusY()/matrixScaleY;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        float matrixScaleX = getLayoutScaleX();
        if(zoomMinimum>matrixScaleX){
            scaleAnimationTo(zoomMinimum, scaleFocusX,scaleFocusY);
        } else if(zoomMaximum<matrixScaleX){
            scaleAnimationTo(zoomMaximum, scaleFocusX,scaleFocusY);
        }
    }

    /**
     * Set the view scale value manually.
     */
    public void setViewScale(float scale){
        int width = getWidth();
        int height = getHeight();
        setViewScale(scale,width/2,height/2);
    }

    public void setViewScale(float scale,float focusX,float focusY){
        scaleScrollX=0;
        scaleScrollY=0;
        float matrixScaleX = getLayoutScaleX();
        float matrixScaleY = getLayoutScaleY();
        scaleFocusX=focusX/matrixScaleX;
        scaleFocusY=focusY/matrixScaleY;
        scaleAnimationTo(scale,scaleFocusX,scaleFocusY);
    }

    private void scaleAnimationTo(float scale, final float focusX, final float focusY){
        cancelZoomAnimator();
        float matrixScaleX = getLayoutScaleX();
        zoomAnimator = ValueAnimator.ofFloat(matrixScaleX, scale);
        zoomAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float matrixScaleX = getLayoutScaleX();
                float matrixScaleY = getLayoutScaleY();
                float animatedValue= (float) animation.getAnimatedValue();
                matrix.setScale(animatedValue,animatedValue);
                setViewScaleInternal(matrixScaleX,matrixScaleY,focusX,focusY);
            }
        });
        zoomAnimator.start();
    }

    public void setScaleAnimation(float scale){
        float matrixScaleX = getLayoutScaleX();
        float matrixScaleY = getLayoutScaleY();
        matrix.setScale(scale,scale);
        setViewScaleInternal(matrixScaleX,matrixScaleY,scaleFocusX,scaleFocusY);
    }

    public void setViewScaleInternal(float oldMatrixScaleX, float oldMatrixScaleY,float focusX,float focusY) {
        //Calculate the focus center location.
        float matrixScaleX = getLayoutScaleX();
        float matrixScaleY = getLayoutScaleY();

        float scrolledX = focusX+scaleScrollX;
        float scrolledY = focusY+scaleScrollY;
        int dx = Math.round(((matrixScaleX-oldMatrixScaleX) * scrolledX)/matrixScaleX);
        int dy = Math.round(((matrixScaleY-oldMatrixScaleY) * scrolledY)/matrixScaleY);
        scrollBy(dx,dy);
        ViewCompat.postInvalidateOnAnimation(this);

        if(null!=scaleChangeListener){
            scaleChangeListener.onScaleChange(scaleFocusX,scaleFocusY);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float matrixScaleX = getLayoutScaleX();
        float matrixScaleY = getLayoutScaleY();
        scaleFocusX=(w/2)/matrixScaleX;
        scaleFocusY=(h/2)/matrixScaleY;
    }

    /**
     * Cancel the zoom animator
     */
    private void cancelZoomAnimator() {
        if(null!=zoomAnimator){
            zoomAnimator.removeAllUpdateListeners();
            zoomAnimator.removeAllListeners();
            zoomAnimator.cancel();
            zoomAnimator=null;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        float matrixScaleX = getLayoutScaleX();
        float matrixScaleY = getLayoutScaleY();
        canvas.scale(matrixScaleX,matrixScaleY);
        super.dispatchDraw(canvas);
        canvas.restore();

        if(BuildConfig.DEBUG){
            drawDebugCenter(canvas);
        }
    }

    private void drawDebugCenter(Canvas canvas) {
        Paint paint=new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);

        int centerX=getWidth()/2;
        int centerY=getHeight()/2;
        canvas.drawLine(centerX-20, centerY, centerX+20, centerY,paint);
        canvas.drawLine(centerX, centerY-20, centerX, centerY+20,paint);
    }

    @Override
    public void scrollBy(int x, int y) {
        final boolean canScrollHorizontal = canScrollHorizontally();
        final boolean canScrollVertical = canScrollVertically();
        if (canScrollHorizontal || canScrollVertical) {
            scrollByInternal(canScrollHorizontal ? x : 0, canScrollVertical ? y : 0,isScaleDragged);
        }
    }

    protected void scrollByInternal(int dx, int dy,boolean isScaleDragged) {
        int consumedX=0;
        if (dx != 0) {
            consumedX = scrollHorizontallyBy(dx,isScaleDragged);
        }
        int consumedY=0;
        if (dy != 0) {
            consumedY = scrollVerticallyBy(dy,isScaleDragged);
        }
        dispatchScrollChange(-consumedX,-consumedY);
        offsetScaleScroll(consumedX,consumedY);
    }

    protected void dispatchScrollChange(int dx,int dy){
        if(null!=scrollChangeListener){
            float matrixScaleX = getLayoutScaleX();
            float matrixScaleY = getLayoutScaleY();
            scrollChangeListener.onScrollChange(this,dx,dy,matrixScaleX,matrixScaleY);
        }
    }

    /**
     * Override this function to handle the scroll event.
     * @param l
     */
    public void setOnLayoutScrollChangeListener(OnLayoutScrollChangeListener l) {
        this.scrollChangeListener=l;
    }

    protected void offsetScaleScroll(float x, float y){
        scaleScrollX+=x;
        scaleScrollY+=y;
    }

    protected int scrollHorizontallyBy(int dx, boolean isScaleDragged) {
        return 0;
    }

    protected int scrollVerticallyBy(int dy,boolean isScaleDragged) {
        return 0;
    }

    /**
     * Query if horizontal scrolling is currently supported. The default implementation
     * returns true.
     *
     * @return True if this View can scroll the current contents horizontally
     */
    public boolean canScrollHorizontally() {
        return true;
    }

    /**
     * Query if vertical scrolling is currently supported. The default implementation
     * returns true.
     *
     * @return True if this View can scroll the current contents vertically
     */
    public boolean canScrollVertically() {
        return true;
    }


    public int offsetChildrenHorizontal(@Px int dx) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            childView.offsetLeftAndRight(dx);
        }
        return dx;
    }

    public int offsetChildrenVertical(@Px int dy) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            childView.offsetTopAndBottom(dy);
        }
        return dy;
    }

    public void offsetChild(View childView,@Px int dx,@Px int dy) {
        if(null!=childView){
            childView.offsetLeftAndRight(dx);
            childView.offsetTopAndBottom(dy);
        }
    }

    public void offsetChildren(@Px int dx,@Px int dy) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            childView.offsetLeftAndRight(dx);
            childView.offsetTopAndBottom(dy);
        }
    }

    /**
     * Release the drag.
     */
    private void releaseDrag() {
        lastMotionX=0f;
        lastMotionY=0f;
        isBeingDragged = false;
        isScaleDragged = false;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    @Override
    public void childDrawableStateChanged(final View child) {
        super.childDrawableStateChanged(child);
        if(null!=layoutChildDrawableStateChangeListener&&hasWindowFocus()){
            int currentViewStateFlag = 0;
            if(child.isSelected()) currentViewStateFlag|=LayoutParams.VIEW_STATE_SELECTED;
            if(child.isEnabled()) currentViewStateFlag|=LayoutParams.VIEW_STATE_ENABLED;
            if(child.isActivated()) currentViewStateFlag|=LayoutParams.VIEW_STATE_ACTIVATED;
            if(child.isFocused()) currentViewStateFlag|=LayoutParams.VIEW_STATE_FOCUSED;
            if(child.isHovered()) currentViewStateFlag|=LayoutParams.VIEW_STATE_HOVERED;
            if(child.isPressed()) currentViewStateFlag|=LayoutParams.VIEW_STATE_PRESSED;
            final LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            //To avoid dispatch to many times, We use our custom view state flag to check if the view state is changed.
            if(currentViewStateFlag!=layoutParams.viewStateFlag){
                layoutParams.viewStateFlag=currentViewStateFlag;
                checkForState.setStateChangedView(child);
                post(checkForState);
            }
        }
    }

    /**
     * Check the view state and dispatch view drawable state change event.
     */
    private class CheckForState implements Runnable{
        private View child;

        public void setStateChangedView(@Nullable View child) {
            this.child = child;
        }

        @Override
        public void run() {
            if(null!=child&&null!=layoutChildDrawableStateChangeListener){
                float layoutScaleX = getLayoutScaleX();
                float layoutScaleY = getLayoutScaleY();
                layoutChildDrawableStateChangeListener.childDrawableStateChanged(child,layoutScaleX,layoutScaleY);
                child=null;
            }
        }
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
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Create a layout params from a giving one.
     * @param p
     * @return
     */
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

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
    public static class LayoutParams extends android.view.ViewGroup.MarginLayoutParams {
        public static final int VIEW_STATE_SELECTED = 1 << 1;
        public static final int VIEW_STATE_FOCUSED = 1 << 2;
        public static final int VIEW_STATE_ENABLED = 1 << 3;
        public static final int VIEW_STATE_PRESSED = 1 << 4;
        public static final int VIEW_STATE_ACTIVATED = 1 << 5;
        public static final int VIEW_STATE_HOVERED = 1 << 6;
        public int viewStateFlag =VIEW_STATE_ENABLED;

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


    /**
     * Interface definition for a callback to be invoked when the scroll
     * X or Y positions of a view change.
     *
     * @see #setOnLayoutScrollChangeListener(OnLayoutScrollChangeListener)
     */
    public interface OnLayoutScrollChangeListener {
        /**
         * Called when the scroll position of a view changes.
         *
         * @param v The view whose scroll position has changed.
         * @param dx Current horizontal scroll origin.
         * @param dy Current vertical scroll origin.
         */
        void onScrollChange(View v, int dx, int dy,float scaleX,float scaleY);
    }


    public void setOnScaleChangeListener(OnLayoutScaleChangeListener listener){
        this.scaleChangeListener=listener;
    }

    public interface OnLayoutScaleChangeListener {
        void onScaleChange(float scaleX,float scaleY);
    }

    public void setOnLayoutChildDrawableStateChanged(OnLayoutChildDrawableStateChanged listener){
        this.layoutChildDrawableStateChangeListener=listener;
    }

    public interface OnLayoutChildDrawableStateChanged{
        void childDrawableStateChanged(View child,float scaleX,float scaleY);
    }

    public class ViewFlinger implements Runnable{
        private final OverScroller overScroller;
        private int lastFlingX = 0;
        private int lastFlingY = 0;

        public ViewFlinger(Context context) {
            overScroller=new OverScroller(context);
        }

        @Override
        public void run() {
            if(!overScroller.isFinished()&&overScroller.computeScrollOffset()){
                int currX = overScroller.getCurrX();
                int currY = overScroller.getCurrY();
                int dx = currX - lastFlingX;
                int dy = currY - lastFlingY;
                lastFlingX = currX;
                lastFlingY = currY;
//                // We are done scrolling if scroller is finished, or for both the x and y dimension,
//                // we are done scrolling or we can't scroll further (we know we can't scroll further
//                // when we have unconsumed scroll distance).  It's possible that we don't need
//                // to also check for scroller.isFinished() at all, but no harm in doing so in case
//                // of old bugs in OverScroller.
//                boolean scrollerFinishedX = overScroller.getCurrX() == overScroller.getFinalX();
//                boolean scrollerFinishedY = overScroller.getCurrY() == overScroller.getFinalY();
//                final boolean doneScrolling = overScroller.isFinished()
//                        || ((scrollerFinishedX || dx != 0) && (scrollerFinishedY || dy != 0));
                scrollBy(dx,dy);
                invalidate();
                postOnAnimation();
            }
        }

        void startScroll(int startX,int startY,int dx,int dy) {
            lastFlingX = startX;
            lastFlingY = startY;
            overScroller.startScroll(startX, startY, dx, dy);
            if (Build.VERSION.SDK_INT < 23) {
                // b/64931938 before API 23, startScroll() does not reset getCurX()/getCurY()
                // to start values, which causes fillRemainingScrollValues() put in obsolete values
                // for LayoutManager.onLayoutChildren().
                overScroller.computeScrollOffset();
            }
            postOnAnimation();
        }

        /**
         * abort the animation
         */
        void abortAnimation(){
            if(!overScroller.isFinished()){
                overScroller.abortAnimation();
                postInvalidate();
            }
        }

        void fling(float velocityX,float velocityY) {
            lastFlingX = lastFlingY = 0;
            overScroller.fling(0, 0, (int)velocityX, (int)velocityY,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            postOnAnimation();
        }

        void postOnAnimation() {
            removeCallbacks(this);
            ViewCompat.postOnAnimation(ZoomLayout.this, this);
        }
    }
}
