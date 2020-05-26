package com.cz.widgets.zoomlayout;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

/**
 * Helper class for ViewGroup to abstract measurements depending on the View's orientation.
 * <p>
 * It is developed to easily support vertical and horizontal orientations in a ViewGroup but
 * can also be used to abstract calls around view bounds and child measurements with margins and
 * decorations.
 *
 * @see #createHorizontalHelper(RecyclerZoomLayout)
 * @see #createVerticalHelper(RecyclerZoomLayout)
 */
public abstract class ZoomOrientationHelper {
    public static final int HORIZONTAL = RecyclerZoomLayout.HORIZONTAL;

    public static final int VERTICAL = RecyclerZoomLayout.VERTICAL;

    private RecyclerZoomLayout layout;

    private ZoomOrientationHelper(RecyclerZoomLayout layout) {
        this.layout=layout;
    }

    /**
     * Returns the start of the view including its decoration and margin.
     * <p>
     * For example, for the horizontal helper, if a View's left is at pixel 20, has 2px left
     * decoration and 3px left margin, returned value will be 15px.
     *
     * @param view The view element to check
     * @return The first pixel of the element
     * @see #getDecoratedEnd(android.view.View)
     */
    public abstract int getDecoratedStart(View view);

    public abstract int getDecoratedStartInOther(View view);

    /**
     * Returns the end of the view including its decoration and margin.
     * <p>
     * For example, for the horizontal helper, if a View's right is at pixel 200, has 2px right
     * decoration and 3px right margin, returned value will be 205.
     *
     * @param view The view element to check
     * @return The last pixel of the element
     * @see #getDecoratedStart(android.view.View)
     */
    public abstract int getDecoratedEnd(View view);

    /**
     * Returns the space occupied by this View in the current orientation including decorations and
     * margins.
     *
     * @param view The view element to check
     * @return Total space occupied by this view
     * @see #getDecoratedMeasurementInOther(View)
     */
    public abstract int getDecoratedMeasurement(View view);

    /**
     * Returns the space occupied by this View in the perpendicular orientation including
     * decorations and margins.
     *
     * @param view The view element to check
     * @return Total space occupied by this view in the perpendicular orientation to current one
     * @see #getDecoratedMeasurement(View)
     */
    public abstract int getDecoratedMeasurementInOther(View view);

    /**
     * Returns the start position of the layout after the start padding is added.
     *
     * @return The very first pixel we can draw.
     */
    public abstract int getStartAfterPadding();

    /**
     * Returns the end position of the layout after the end padding is removed.
     *
     * @return The end boundary for this layout.
     */
    public abstract int getEndAfterPadding();

    /**
     * Returns the end position of the layout without taking padding into account.
     *
     * @return The end boundary for this layout without considering padding.
     */
    public abstract int getEnd();

    /**
     * Returns the total space to layout. This number is the difference between
     * {@link #getEndAfterPadding()} and {@link #getStartAfterPadding()}.
     *
     * @return Total space to layout children
     */
    public abstract int getTotalSpace();

    public abstract int getTotalSpaceInOther();

    /**
     * Returns the padding at the end of the layout. For horizontal helper, this is the right
     * padding and for vertical helper, this is the bottom padding. This method does not check
     * whether the layout is RTL or not.
     *
     * @return The padding at the end of the layout.
     */
    public abstract int getEndPadding();

    public int getLeftMargin(){
        int leftMargin=0;
        ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
        if(null!=layoutParams&&layoutParams instanceof ViewGroup.MarginLayoutParams){
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            leftMargin = marginLayoutParams.leftMargin;
        }
        return leftMargin;
    }

    public int getTopMargin(){
        int topMargin=0;
        ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
        if(null!=layoutParams&&layoutParams instanceof ViewGroup.MarginLayoutParams){
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            topMargin = marginLayoutParams.topMargin;
        }

        return topMargin;
    }

    public int getRightMargin(){
        int rightMargin=0;
        ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
        if(null!=layoutParams&&layoutParams instanceof ViewGroup.MarginLayoutParams){
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            rightMargin = marginLayoutParams.rightMargin;
        }
        return rightMargin;
    }

    public int getBottomMargin(){
        int bottomMargin=0;
        ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
        if(null!=layoutParams&&layoutParams instanceof ViewGroup.MarginLayoutParams){
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            bottomMargin = marginLayoutParams.bottomMargin;
        }

        return bottomMargin;
    }

    /**
     * Creates an OrientationHelper for the given LayoutManager and orientation.
     *
     * @param layout LayoutManager to attach to
     * @param orientation   Desired orientation. Should be {@link #HORIZONTAL} or {@link #VERTICAL}
     * @return A new OrientationHelper
     */
    public static ZoomOrientationHelper createOrientationHelper(final RecyclerZoomLayout layout, final int orientation) {
//        switch (orientation) {
//            case HORIZONTAL:
//                return createHorizontalHelper(layout);
//            case VERTICAL:
//                return createVerticalHelper(layout);
//        }
//        throw new IllegalArgumentException("invalid orientation");
        ZoomOrientationHelper orientationHelper=null;
        switch (orientation) {
            case HORIZONTAL:
                orientationHelper = createHorizontalHelper(layout);
                break;
            case VERTICAL:
                orientationHelper = createVerticalHelper(layout);
                break;
        }
        if(null==orientationHelper){
            throw new IllegalArgumentException("invalid orientation");
        }
        final ZoomOrientationHelper wrappedHelper=orientationHelper;
        return new ZoomOrientationHelper(layout) {
            @Override
            public int getEndAfterPadding() {
                float matrixScaleY = layout.getLayoutScaleY();
                return (int) (wrappedHelper.getEndAfterPadding()/matrixScaleY);
            }

            @Override
            public int getEnd() {
                //#Question-1 document/question.md
                float matrixScaleY = layout.getLayoutScaleY();
                return (int) (wrappedHelper.getEnd()/matrixScaleY);
            }

            @Override
            public int getStartAfterPadding() {
                float matrixScaleY = layout.getLayoutScaleY();
                return (int) (wrappedHelper.getStartAfterPadding()/matrixScaleY);
            }

            @Override
            public int getDecoratedMeasurement(@NonNull View view) {
                return wrappedHelper.getDecoratedMeasurement(view);
            }

            @Override
            public int getDecoratedMeasurementInOther(@NonNull View view) {
                return wrappedHelper.getDecoratedMeasurementInOther(view);
            }

            @Override
            public int getDecoratedEnd(View view) {
                return wrappedHelper.getDecoratedEnd(view);
            }

            @Override
            public int getDecoratedStart(View view) {
                return wrappedHelper.getDecoratedStart(view);
            }

            @Override
            public int getDecoratedStartInOther(View view) {
                return wrappedHelper.getDecoratedStartInOther(view);
            }

            @Override
            public int getTotalSpace() {
                float matrixScaleY = layout.getLayoutScaleY();
                return (int) (wrappedHelper.getTotalSpace()/matrixScaleY);
            }

            @Override
            public int getTotalSpaceInOther() {
                float matrixScaleY = layout.getLayoutScaleY();
                return (int) (wrappedHelper.getTotalSpaceInOther()/matrixScaleY);
            }

            @Override
            public int getEndPadding() {
                float matrixScaleY = layout.getLayoutScaleY();
                return (int) (wrappedHelper.getEndPadding()/matrixScaleY);
            }
        };

    }

    /**
     * Creates a horizontal OrientationHelper for the given LayoutManager.
     *
     * @param layout The LayoutManager to attach to.
     * @return A new OrientationHelper
     */
    static ZoomOrientationHelper createHorizontalHelper(final RecyclerZoomLayout layout) {
        return new ZoomOrientationHelper(layout) {
            @Override
            public int getEndAfterPadding() {
                return layout.getWidth() - layout.getPaddingRight();
            }

            @Override
            public int getEnd() {
                return layout.getWidth();
            }

            @Override
            public int getStartAfterPadding() {
                return layout.getPaddingLeft();
            }

            @Override
            public int getDecoratedMeasurement(@NonNull View view) {
                int leftMargin = getLeftMargin();
                int rightMargin = getRightMargin();
                return leftMargin + layout.getDecoratedMeasuredWidth(view) + rightMargin;
            }

            @Override
            public int getDecoratedMeasurementInOther(@NonNull View view) {
                int topMargin = getTopMargin();
                int bottomMargin = getBottomMargin();
                return topMargin+layout.getDecoratedMeasuredHeight(view)+bottomMargin;
            }

            @Override
            public int getDecoratedEnd(View view) {
                int rightMargin = getRightMargin();
                return layout.getDecoratedRight(view) + rightMargin;
            }

            @Override
            public int getDecoratedStart(View view) {
                int leftMargin = getLeftMargin();
                return layout.getDecoratedLeft(view) - leftMargin;
            }

            @Override
            public int getDecoratedStartInOther(View view) {
                int topMargin = getTopMargin();
                return layout.getDecoratedTop(view) - topMargin;
            }

            @Override
            public int getTotalSpace() {
                return layout.getWidth() - layout.getPaddingLeft() - layout.getPaddingRight();
            }

            @Override
            public int getTotalSpaceInOther() {
                return layout.getHeight() - layout.getPaddingTop() - layout.getPaddingBottom();
            }

            @Override
            public int getEndPadding() {
                return layout.getPaddingRight();
            }
        };
    }

    /**
     * Creates a vertical OrientationHelper for the given LayoutManager.
     *
     * @param layout The LayoutManager to attach to.
     * @return A new OrientationHelper
     */
    static ZoomOrientationHelper createVerticalHelper(final RecyclerZoomLayout layout) {
        return new ZoomOrientationHelper(layout) {
            @Override
            public int getEndAfterPadding() {
                return layout.getHeight() - layout.getPaddingBottom();
            }

            @Override
            public int getEnd() {
                return layout.getHeight();
            }

            @Override
            public int getStartAfterPadding() {
                return layout.getPaddingTop();
            }

            @Override
            public int getDecoratedMeasurement(View view) {
                int topMargin = getTopMargin();
                int bottomMargin = getBottomMargin();
                return topMargin+layout.getDecoratedMeasuredHeight(view)+bottomMargin;
            }

            @Override
            public int getDecoratedMeasurementInOther(View view) {
                int leftMargin = getLeftMargin();
                int rightMargin = getRightMargin();
                return leftMargin+layout.getDecoratedMeasuredWidth(view)+rightMargin;
            }

            @Override
            public int getDecoratedEnd(@NonNull View view) {
                int bottomMargin = getBottomMargin();
                return layout.getDecoratedBottom(view)+bottomMargin;
            }

            @Override
            public int getDecoratedStart(@NonNull View view) {
                int topMargin = getTopMargin();
                return layout.getDecoratedTop(view)-topMargin;
            }

            @Override
            public int getDecoratedStartInOther(View view) {
                int leftMargin = getLeftMargin();
                return layout.getDecoratedLeft(view)-leftMargin;
            }


            @Override
            public int getTotalSpace() {
                return layout.getHeight() - layout.getPaddingTop() - layout.getPaddingBottom();
            }

            @Override
            public int getTotalSpaceInOther() {
                return layout.getWidth() - layout.getPaddingLeft() - layout.getPaddingRight();
            }

            @Override
            public int getEndPadding() {
                return layout.getPaddingBottom();
            }
        };
    }
}
