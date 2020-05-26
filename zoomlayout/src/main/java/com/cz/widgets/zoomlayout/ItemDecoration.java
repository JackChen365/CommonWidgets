package com.cz.widgets.zoomlayout;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;

public class ItemDecoration {

        protected RecyclerZoomLayout layout;

        void attachToView(RecyclerZoomLayout layout){
                this.layout=layout;
                onAttachToView(layout);
        }

        public void onAttachToView(RecyclerZoomLayout parent){
        }

        void detachFromView(RecyclerZoomLayout parent){
                this.layout=null;
                onDetachFromView(parent);
        }

        public void onDetachFromView(RecyclerZoomLayout parent){
        }

        public RecyclerZoomLayout getLayout() {
                return layout;
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
                final Rect insets = ((RecyclerZoomLayout.LayoutParams) child.getLayoutParams()).decorInsets;
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
                final Rect insets = ((RecyclerZoomLayout.LayoutParams) child.getLayoutParams()).decorInsets;
                return child.getMeasuredHeight() + insets.top + insets.bottom;
        }

        public Rect getDecoratedRect(@NonNull View child) {
                return ((RecyclerZoomLayout.LayoutParams) child.getLayoutParams()).decorInsets;
        }

        /**
         * Returns the left edge of the given child view within its parent, offset by any applied
         * {@link ItemDecoration ItemDecorations}.
         *
         * @param child Child to query
         * @return Child left edge with offsets applied
         */
        public int getDecoratedLeft(@NonNull View child) {
                final Rect insets = ((RecyclerZoomLayout.LayoutParams) child.getLayoutParams()).decorInsets;
                return child.getLeft() - insets.left;
        }

        /**
         * Returns the top edge of the given child view within its parent, offset by any applied
         * {@link ItemDecoration ItemDecorations}.
         *
         * @param child Child to query
         * @return Child top edge with offsets applied
         */
        public int getDecoratedTop(@NonNull View child) {
                final Rect insets = ((RecyclerZoomLayout.LayoutParams) child.getLayoutParams()).decorInsets;
                return child.getTop() - insets.top;
        }

        /**
         * Returns the right edge of the given child view within its parent, offset by any applied
         * {@link ItemDecoration ItemDecorations}.
         *
         * @param child Child to query
         * @return Child right edge with offsets applied
         */
        public int getDecoratedRight(@NonNull View child) {
                final Rect insets = ((RecyclerZoomLayout.LayoutParams) child.getLayoutParams()).decorInsets;
                return child.getRight() + insets.right;
        }

        /**
         * Returns the bottom edge of the given child view within its parent, offset by any applied
         * {@link ItemDecoration ItemDecorations}.
         *
         * @param child Child to query
         * @return Child bottom edge with offsets applied
         */
        public int getDecoratedBottom(@NonNull View child) {
                final Rect insets = ((RecyclerZoomLayout.LayoutParams) child.getLayoutParams()).decorInsets;
                return child.getBottom() + insets.bottom;
        }

        /**
         * Draw any appropriate decorations into the Canvas supplied to the 
         * Any content drawn by this method will be drawn before the value views are drawn,
         * and will thus appear underneath the views.
         *
         * @param c Canvas to draw into
         */
        public void onDraw(@NonNull Canvas c, View child,Rect insetRect,float scaleX,float scaleY) {
        }

        /**
         * Draw any appropriate decorations into the Canvas supplied to the 
         * Any content drawn by this method will be drawn after the value views are drawn
         * and will thus appear over the views.
         *
         * @param c Canvas to draw into
         */
        public void onDrawOver(@NonNull Canvas c, View child,Rect outRect,float scaleX,float scaleY) {
        }

        public void onDraw(@NonNull Canvas c,float scaleX,float scaleY) {
        }

        public void onDrawOver(@NonNull Canvas c, float scaleX,float scaleY) {
        }

        /**
         * Retrieve any offsets for the given value. Each field of <code>outRect</code> specifies
         * the number of pixels that the value view should be inset by, similar to padding or margin.
         * The default implementation sets the bounds of outRect to 0 and returns.
         *
         * <p>
         * If this ItemDecoration does not affect the positioning of value views, it should set
         * all four fields of <code>outRect</code> (left, top, right, bottom) to zero
         * before returning.
         * <p>
         *
         * @param outRect Rect to receive the output.
         */
        public void getItemOffsets(@NonNull Rect outRect, View item) {
            outRect.set(0, 0, 0, 0);
        }
}