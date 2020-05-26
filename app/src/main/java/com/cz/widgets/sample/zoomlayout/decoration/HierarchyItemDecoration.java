package com.cz.widgets.sample.zoomlayout.decoration;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.NonNull;

import com.cz.widgets.sample.R;
import com.cz.widgets.zoomlayout.ItemDecoration;
import com.cz.widgets.zoomlayout.RecyclerZoomLayout;
import com.cz.widgets.zoomlayout.tree.TreeNode;
import com.cz.widgets.zoomlayout.tree.hierarchy.HierarchyLayout;

/**
 * @author Created by cz
 * @date 2020-05-11 14:00
 * @email bingo110@126.com
 */
public class HierarchyItemDecoration extends ItemDecoration {
    private final RectF tempRect=new RectF();
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private int horizontalSpacing;
    private int verticalSpacing;
    private float strokeWidth;
    public HierarchyItemDecoration(Context context) {
        Resources resources = context.getResources();
        horizontalSpacing=resources.getDimensionPixelOffset(R.dimen.treeHorizontalSpacing);
        verticalSpacing=resources.getDimensionPixelOffset(R.dimen.treeVerticalSpacing);
        strokeWidth=resources.getDimension(R.dimen.hierarchyStrokeWidth);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(strokeWidth);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, View item) {
        super.getItemOffsets(outRect, item);
        outRect.set(horizontalSpacing,verticalSpacing,horizontalSpacing,verticalSpacing);
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, float scaleX, float scaleY) {
        super.onDrawOver(canvas, scaleX, scaleY);
        paint.setStrokeWidth(strokeWidth * scaleX);
        RecyclerZoomLayout layout = getLayout();
        int paddingLeft = layout.getPaddingLeft();
        int paddingTop = layout.getPaddingTop();
        int childCount = layout.getChildCount();
        int layoutScrollX = layout.getLayoutScrollX();
        int layoutScrollY = layout.getLayoutScrollY();
        canvas.save();
        canvas.translate((paddingLeft-layoutScrollX)*scaleX,(paddingTop-layoutScrollY)*scaleY);
        for(int i=0;i<childCount;i++){
            View childView = layout.getChildAt(i);
            HierarchyLayout.LayoutParams layoutParams = (HierarchyLayout.LayoutParams) childView.getLayoutParams();
            TreeNode treeNode = layoutParams.treeNode;
            TreeNode parentNode = treeNode.parent;
            if (null != parentNode) {
                //The the connect line between current node and its parent node.
                drawConnectLine(canvas,paint,childView,parentNode,treeNode,scaleX,scaleY);
            }
            //The the connect line between current node and its child nodes.
            for(int index=0;index<treeNode.children.size();index++){
                TreeNode childNode = (TreeNode) treeNode.children.get(index);
                drawConnectLine(canvas,paint,childView,treeNode,childNode,scaleX,scaleY);
            }
        }
        canvas.restore();
    }

    private<E> void drawConnectLine(Canvas canvas,Paint paint,View childView,TreeNode<E> fromNode, TreeNode<E> toNode,float matrixScaleY,float matrixScaleX) {
        RectF layoutRect = getLayoutRect(childView, fromNode, matrixScaleX, matrixScaleY);
        float right=layoutRect.right;
        float centerY=layoutRect.centerY();
        layoutRect = getLayoutRect(childView, toNode, matrixScaleX, matrixScaleY);
        canvas.drawLine(right, centerY, layoutRect.left, layoutRect.centerY(),paint);
    }

    private<E> RectF getLayoutRect(View childView,TreeNode<E> treeNode,float scaleX,float scaleY){
        int paddingLeft = layout.getPaddingLeft();
        int paddingTop = layout.getPaddingTop();
        Rect decoratedRect = getDecoratedRect(childView);
        int decoratedMeasuredWidth = getDecoratedMeasuredWidth(childView);
        int decoratedMeasuredHeight = getDecoratedMeasuredHeight(childView);
        int left=paddingLeft+treeNode.depth*decoratedMeasuredWidth+decoratedRect.left;
        int top=paddingTop+treeNode.centerBreadth * decoratedMeasuredHeight+decoratedRect.top;
        tempRect.set(left*scaleX, top*scaleY, (
                        left+childView.getMeasuredWidth())*scaleX,
                (top+childView.getMeasuredHeight())*scaleY);
        return tempRect;

    }
}
