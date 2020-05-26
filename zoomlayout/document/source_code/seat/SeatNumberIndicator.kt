package com.cz.laboratory.app.android.view.seat

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/**
 * Created by cz on 2017/10/18.
 */
class SeatNumberIndicator(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null,0)
    private val views= mutableListOf<View>()
    val childCount:Int
        get() = views.size

    fun addView(view:View){
        if(null==view.layoutParams){
            view.layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        views.add(view)
    }

    fun remove(view:View)=views.remove(view)

    fun getChildAt(index:Int)=views[index]

    fun removeAllViews(){
        views.clear()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //以子控件最大宽为自身宽度
        var measureWidth=0
        views.forEach {
            if(measureWidth<it.measuredWidth){
                measureWidth=it.measuredWidth
            }
        }
        setMeasuredDimension(measureWidth,measuredHeight)
    }

    /**
     * 绘预览图
     */
    fun drawPreView(canvas: Canvas,numberView:View,rowRect: Rect, matrixScaleX:Float, matrixScaleY:Float,leftOffset:Int){
        numberView.layout(leftOffset+(measuredWidth-numberView.measuredWidth)/2,
                rowRect.centerY()-numberView.measuredHeight/2,
                leftOffset+(measuredWidth+numberView.measuredWidth)/2,
                rowRect.centerY()+numberView.measuredHeight/2)
        drawNumberView(canvas,numberView,matrixScaleX,matrixScaleY)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        forEach{ drawNumberView(canvas,it,1f,1f)}
    }

    fun drawNumberView(canvas: Canvas, childView: View, matrixScaleX:Float, matrixScaleY:Float) {
        canvas.save()
        canvas.scale(matrixScaleX,matrixScaleY)
        canvas.translate(childView.left.toFloat(), childView.top.toFloat())
        childView.draw(canvas)
        canvas.restore()
    }

    fun forEach(action:(View)->Unit)=views.forEach(action)
}