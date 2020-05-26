package com.cz.laboratory.app.android.view.hierarchy

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * @author Created by cz
 * @date 2020-02-02 18:11
 * @email bingo110@126.com
 *
 */
open class HierarchyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AbsHierarchyLayout(context, attrs, defStyleAttr) {


    override fun drawConnectLine(canvas: Canvas,paint:Paint,strokeWidth:Float,node: HierarchyNode,layoutRect: Rect, matrixScaleY: Float, matrixScaleX: Float) {
        //线宽按比例缩放
        val childRect = node.layoutRect
        paint.strokeWidth = strokeWidth * matrixScaleX
        canvas.drawLine(layoutRect.right*matrixScaleX,
            layoutRect.centerY()*matrixScaleY,
            childRect.left*matrixScaleX,
            childRect.centerY()*matrixScaleY,paint)
    }

    override fun onHierarchyMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int,hierarchyNoeItems: List<HierarchyNode>, view: View) {
        val adapter = getAdapter()?:return
        val horizontalSpacing = getHorizontalSpacing()
        val verticalSpacing = getVerticalSpacing()
        hierarchyNoeItems.forEach {node ->
            //绑定数据
            adapter.bindView(view,node)
            //计算大小,此处测量耗时如果不测量,耗时3-4毫秒,测量耗时30-40毫秒,控件44个,所以为了性能考虑,应该让每一个控件大小一致,然后设置hasFixSize=true!
            measureChildWithMargins(view,MeasureSpec.getMode(widthMeasureSpec),MeasureSpec.getMode(heightMeasureSpec))
            val left=paddingLeft+node.level*horizontalSpacing+node.level*view.measuredWidth
            val centerDepth=node.startDepth+(node.endDepth-node.startDepth)/2
            val top=paddingTop+centerDepth*verticalSpacing+centerDepth *view.measuredHeight
            //记录view排版矩阵
            node.layoutRect.set(left.toInt(), top.toInt(), left.toInt()+view.measuredWidth, top.toInt()+view.measuredHeight)
        }
    }

    override fun draw(canvas: Canvas?) {
        val st=SystemClock.elapsedRealtime()
        super.draw(canvas)
        Log.i("HierarchyView","draw:"+(SystemClock.elapsedRealtime()-st))
    }

}