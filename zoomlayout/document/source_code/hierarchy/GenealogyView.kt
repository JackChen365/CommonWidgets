package com.cz.laboratory.app.android.view.hierarchy

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

/**
 * @author Created by cz
 * @date 2020-02-02 18:32
 * @email bingo110@126.com
 * Genealogy View that shows all the family member.
 */
class GenealogyView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AbsHierarchyLayout(context, attrs, defStyleAttr){
    /**
     * 绘制连接线
     */
    override fun drawConnectLine(canvas: Canvas, paint: Paint, strokeWidth:Float, node: HierarchyNode, layoutRect: Rect, matrixScaleY: Float, matrixScaleX: Float) {
        //线宽按比例缩放
        val childRect = node.layoutRect
        paint.strokeWidth = strokeWidth * matrixScaleX
        canvas.drawLine((layoutRect.left+layoutRect.width()/2)*matrixScaleX,
            layoutRect.bottom*matrixScaleY,
            (childRect.left+childRect.width()/2)*matrixScaleX,
            childRect.top*matrixScaleY,paint)
    }

    override fun onHierarchyMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int, hierarchyNoeItems: List<HierarchyNode>, view: View) {
        val adapter = getAdapter()?:return
        val horizontalSpacing = getHorizontalSpacing()
        val verticalSpacing = getVerticalSpacing()
        hierarchyNoeItems.forEach {node ->
            //绑定数据
            adapter.bindView(view,node)
            measureChildWithMargins(view,MeasureSpec.getMode(widthMeasureSpec),MeasureSpec.getMode(heightMeasureSpec))
            val centerDepth=node.startDepth+(node.endDepth-node.startDepth)/2
            val left=paddingLeft+centerDepth*horizontalSpacing+centerDepth *view.measuredWidth
            val top=paddingTop+node.level*verticalSpacing+node.level*view.measuredHeight
            //记录view排版矩阵
            node.layoutRect.set(left.toInt(), top.toInt(), left.toInt()+view.measuredWidth, top.toInt()+view.measuredHeight)
        }
    }
}
