package com.cz.laboratory.app.android.view.seat

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.util.SparseBooleanArray
import android.view.*
import androidx.core.view.ViewCompat
import androidx.core.widget.ScrollerCompat
import com.cz.laboratory.app.BuildConfig.DEBUG
import com.cz.laboratory.app.R
import java.util.*

/**
 * Created by cz on 2017/10/18.
 * 1:改良节点信息,以一个横/纵,两列,替代以往整个二维矩阵数据
 * 2:添加一个HandleThread作预览图绘制
 * 3:改良了view复用,第一步先清理出界view,第二步添加新的
 */
class SeatTable(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        View(context, attrs, defStyleAttr) , ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnGestureListener {
    companion object{
        private const val TAG="SeatTable"
    }
    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null,0)
    private val viewFlinger=ViewFlinger()
    private val scaleGestureDetector = ScaleGestureDetector(context, this)
    private val gestureDetector = GestureDetector(context, this)
    private val tmpRect = Rect()
    //当前屏幕显示区域
    private val screenRect= Rect()
    private var m = FloatArray(9)
    private val scaleMatrix=Matrix()
    private val views=ArrayList<View>()

    //可滚动区域大小,涉及动态计算,所以保存值
    private var horizontalRange=0
    private var verticalRange=0
    //所有座位信息
    private lateinit var seatArray: SeatArray
    private val recyclerBin=RecyclerBin()
    private var adapter:SeatTableAdapter?=null
    //绘制元素对象
    private lateinit var viewItem:ViewItem
    //缩放限制区域
    private var hierarchySpringBackMinScale=0.6f
    private var hierarchySpringBackMaxScale=2.4f
    private var hierarchyMaxScale=2.0f
    private var hierarchyMinScale=1.0f
    //缩放动画对象
    private var zoomAnimator: ValueAnimator?=null
    //缩放聚焦点,因为缩放过程中,会改变其值,为了体验平滑,记录最初值
    private var scaleFocusX =0f
    private var scaleFocusY =0f
    //缩略图背景
    private var thumbBackgroundDrawable:Drawable?=null
    //缩略图
    private val previewPaint=Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color=Color.RED
        strokeWidth=2f
        style=Paint.Style.STROKE
    }
    private val previewPainter=PreViewPainter()
    private var previewBitmap:Bitmap?=null
    private var previewWidth=0f

    init {
        context.obtainStyledAttributes(attrs, R.styleable.SeatTable1).apply {
            setPreViewWidth(getDimension(R.styleable.SeatTable1_st_previewWidth,0f))
            setHierarchyMaxScale(getFloat(R.styleable.SeatTable1_st_hierarchyMaxScale,2f))
            setHierarchyMinScale(getFloat(R.styleable.SeatTable1_st_hierarchyMinScale,0.8f))
            setThumbBackgroundDrawable(getDrawable(R.styleable.SeatTable1_st_thumbBackgroundDrawable))
            recycle()
        }
    }

    private fun setPreViewWidth(width: Float) {
        this.previewWidth=width
    }

    private fun setHierarchyMaxScale(hierarchyMaxScale: Float) {
        this.hierarchyMaxScale=hierarchyMaxScale
    }

    private fun setHierarchyMinScale(hierarchyMinScale: Float) {
        this.hierarchyMinScale=hierarchyMinScale
    }

    private fun setThumbBackgroundDrawable(drawable: Drawable?) {
        this.thumbBackgroundDrawable=drawable
        invalidate()
    }

    /**
     * 设置数据适配器
     */
    fun setAdapter(newAdapter: SeatTableAdapter){
        //重置table
        resetSeatTable()
        adapter= newAdapter
        //初始化座位数据信息
        val rowCount = newAdapter.getSeatRowCount()
        val columnCount = newAdapter.getSeatColumnCount()
        seatArray = SeatArray(rowCount,columnCount)
        //绘制元素对象
        viewItem=ViewItem(newAdapter)
        //重新排版
        requestLayout()
    }

    /**
     * 重置所有信息
     */
    private fun resetSeatTable() {
        if (null != adapter) {
            scrollTo(0,0)
            adapter = null
            //清空指示器
            viewItem.numberLayout.removeAllViews()
            //清除所有缓存
            recyclerBin.recyclerAll()
            //清空预览图
            previewBitmap = null
            //重置matrix
            scaleMatrix.reset()
            //清空缓存控件
            views.clear()
        }
    }

    /**
     * 滚动位置到屏幕中间
     */
    fun scrollToCenter(){
        post {
            scrollTo(computeHorizontalScrollRange() / 2, 0)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val adapter=adapter?:return
        if(!viewItem.isLayoutComplete()){
            val widthMode = MeasureSpec.getMode(widthMeasureSpec)
            val heightMode = MeasureSpec.getMode(heightMeasureSpec)
            //测量排版屏幕
            forEachChild { measureChildWithMargins(it,widthMode,heightMode) }
            //测量所有控件
            viewItem.onMeasured(adapter,widthMode,heightMode)
            //计算整个电影院大小
            measureSeatRange(adapter,viewItem.seatView)
            //排版
            viewItem.onLayout()
            //重置预览图
            previewPainter.resetReviewBitmap()
            //铺满屏幕
            fill()
            println("onMeasure:${computeHorizontalScrollRange()} ${computeVerticalScrollRange()}")
        }
    }

    /**
     * 仅计算出第1横/纵列的排版数据,以及整体区间
     */
    private fun measureSeatRange(adapter: SeatTableAdapter, itemView:View) {
        var left = viewItem.leftOffset
        var top = viewItem.topOffset
        //计算第一横列节点信息
        seatArray.initColumnArray { column->
            val item=Rect(left, top, left + itemView.measuredWidth, top + itemView.measuredHeight)
            if(column==seatArray.columnCount-1){
                left += itemView.measuredWidth
            } else {
                left += itemView.measuredWidth + adapter.getHorizontalSpacing(column)
            }
            item
        }
        //计算第1纵列控件位置
        seatArray.initRowArray { row->
            val item=Rect(left, top, left + itemView.measuredWidth, top + itemView.measuredHeight)
            //换行,计算上偏移信息
            if(row==seatArray.rowCount-1){
                top += itemView.measuredHeight
            } else {
                top += itemView.measuredHeight + adapter.getVerticalSpacing(row)
            }
            item
        }
        //计算出横纵向区间范围
        horizontalRange = left + paddingRight
        verticalRange = top + paddingBottom
        println("measureSeatRange:${itemView.measuredWidth} ${itemView.measuredHeight}")
    }

    fun measureChildWithMargins(child: View, widthMode: Int, heightMode: Int,ignorePadding:Boolean=false) {
        val lp = child.layoutParams
        val widthSpec = getChildMeasureSpec(measuredWidth, widthMode, if(ignorePadding) 0 else paddingLeft + paddingRight, lp.width)
        val heightSpec = getChildMeasureSpec(measuredHeight, heightMode,if(ignorePadding) 0 else paddingTop + paddingBottom, lp.height)
        child.measure(widthSpec, heightSpec)
    }

    fun getChildMeasureSpec(parentSize: Int, parentMode: Int, padding: Int, childDimension: Int): Int {
        val size = Math.max(0, parentSize - padding)
        var resultSize = 0
        var resultMode = 0
        if (childDimension >= 0) {
            resultSize = childDimension
            resultMode = View.MeasureSpec.EXACTLY
        } else {
            if (childDimension == ViewGroup.LayoutParams.MATCH_PARENT) {
                resultSize = size
                resultMode = parentMode
            } else if (childDimension == ViewGroup.LayoutParams.WRAP_CONTENT) {
                resultSize = size
                if (parentMode == View.MeasureSpec.AT_MOST || parentMode == View.MeasureSpec.EXACTLY) {
                    resultMode = View.MeasureSpec.AT_MOST
                } else {
                    resultMode = View.MeasureSpec.UNSPECIFIED
                }
            }
        }
        return View.MeasureSpec.makeMeasureSpec(resultSize, resultMode)
    }

    override fun onDetachedFromWindow() {
        previewPainter.quit()
        super.onDetachedFromWindow()
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        //屏幕滚动变化
        fill()
    }

    /**
     * 铺满屏幕
     */
    private fun fill() {
        //滚动时检测当前屏需要控件
        val adapter = adapter ?: return
        var st = System.currentTimeMillis()
        val matrixScaleX = getMatrixScaleX()
        val matrixScaleY = getMatrixScaleY()
        //此处优化细节为,直接通过缩放比,以及控件尺寸,确定当前屏幕在,整个二维节点内的行与列,减少大量数量时的大量运算,仅适用于电影票选座
        screenRect.set(scrollX, scrollY, scrollX + measuredWidth, scrollY + measuredHeight)
        //快速查找到需要绘制的矩阵条目起始,结束位置
        //起始纵向矩阵
        val rowRange = findScreenRange(seatArray.rowArray, { (it.top * matrixScaleY).toInt() .. (it.bottom * matrixScaleY).toInt() }, scrollY) {
            tmpRect.set((it.left * matrixScaleX).toInt(), (it.top * matrixScaleY).toInt(), (it.right * matrixScaleX).toInt(), (it.bottom * matrixScaleY).toInt())
            intersetsVerticalRect(screenRect, tmpRect)
        }
        //横向查
        val columnRange = findScreenRange(seatArray.columnArray, { (it.left * matrixScaleX).toInt() .. (it.right*matrixScaleX).toInt() }, scrollX) {
            tmpRect.set((it.left * matrixScaleX).toInt(), (it.top * matrixScaleY).toInt(), (it.right * matrixScaleX).toInt(), (it.bottom * matrixScaleY).toInt())
            intersetsHorizontalRect(screenRect, tmpRect)
        }
        //清空当前屏幕所有控件
        recyclerBin.detachAndScrapAttachedViews()
        rowRange.forEach { row ->
            //列矩阵
            val rowLayout = seatArray.rowArray[row]
            columnRange.forEach { column ->
                //限制指定条目是否展示
                if (adapter.isSeatVisible(row, column)) {
                    val columnLayout = seatArray.columnArray[column]
                    //取一个新的控件,并运算
                    val view = recyclerBin.getView(row, column)
                    //添加控件
                    addView(view)
                    adapter.bindSeatView(parent as ViewGroup, view, row, column)
                    view.tag = itemId(row, column, seatArray.columnCount)
                    //直接排,不排在指定矩阵内
                    view.layout(columnLayout.left, rowLayout.top, columnLayout.right, rowLayout.bottom)
                }
            }
            //添加指示器控件
            val numberView = recyclerBin.getNumberView()
            //绑定序列
            adapter.bindSeatNumberView(numberView, row)
            viewItem.numberLayout.addView(numberView)
            //排版位置
            numberView.layout((viewItem.numberLayout.measuredWidth - numberView.measuredWidth) / 2,
                    rowLayout.centerY() - numberView.measuredHeight / 2 - viewItem.numberLayout.top,
                    (viewItem.numberLayout.measuredWidth + numberView.measuredWidth) / 2,
                    rowLayout.centerY() + numberView.measuredHeight / 2 - viewItem.numberLayout.top)
        }
    }


    /**
     * 查找屏幕内起始计算矩阵,当数据量非常大时,快速找到起始遍历位置
     * 算法为,先找到当前scroll偏移量,再根据偏移量,找到横/纵向,起始的位置,然后遍历数据,找到排版的矩阵位置
     */
    private fun findScreenRange(array:Array<Rect>,callback:(Rect)->IntRange,offset:Int,predicate:(Rect)->Boolean):IntRange{
        var (start,end)=-1 to -1
        val nearIndex=getNearSeatIndex(array,callback,offset)
        run {
            (nearIndex until array.size).forEach{ index->
                val rect=array[index]
                val intersects=predicate(rect)
                if(-1==start&&intersects){
                    start=index//记录头
                } else if(-1!=start&&!intersects){
                    end=index
                    return@run
                }
            }
        }
        //检测最后结果
        if(-1==end){
            end=array.size-1
        }
        return IntRange(start,end)
    }
    /**
     * 二分查找法,找到横/纵向最接近的节点位置
     * @return
     */
    private fun getNearSeatIndex(array:Array<Rect>,callback:(Rect)->IntRange,offset:Int): Int {
        var start = 0
        var end = array.size - 1
        var result = -1
        while (start <= end) {
            val middle = (start + end) / 2
            val itemRange=callback.invoke(array[middle])
            if (offset in itemRange) {
                result = middle
                break
            } else if (offset < itemRange.first) {
                end = middle - 1
            } else {
                start = middle + 1
            }
        }
        if (-1 == result) {
            result = start
        }
        return result
    }

    /**
     * 绘制一个预览节点信息
     */
    private fun drawPreviewNode(canvas: Canvas,firstView: View,matrixScaleX: Float, matrixScaleY: Float,left:Int,top:Int,right:Int,bottom:Int) {
        firstView.layout(left,top,right,bottom)
        //这里不用排版,不用顾忌不排版后点击错乱问题
        drawSeatView(canvas, firstView, matrixScaleX, matrixScaleY)
    }

    fun getChildCount()=views.size

    fun getChildAt(index:Int)=views[index]

    fun indexOfChild(view:View)=views.indexOf(view)

    /**
     * 选中一个指定位置数据,选中后,涉及预览图更新
     */
    fun setItemSelected(row:Int,column:Int,selected:Boolean){
        viewItem.seatView.isSelected=selected
        val matrixScaleX=previewWidth*1f/horizontalRange
        //记录选中状态
        val itemId=itemId(row,column,seatArray.columnCount)
        seatArray.setItemSelected(itemId,selected)
        //记录id
        viewItem.seatView.tag=itemId
        //重绘座位信息
        val rowLayout=seatArray.rowArray[row]
        val columnLayout=seatArray.columnArray[column]
        val bitmap=previewBitmap;
        if(null!=bitmap){
            drawPreviewNode(Canvas(bitmap),viewItem.seatView,matrixScaleX,matrixScaleX,
                    columnLayout.left,rowLayout.top,columnLayout.right,rowLayout.bottom)
        }
        //重绘界面
        postInvalidate()
        println("setItemSelected:${viewItem.seatView.isSelected} ${viewItem.seatView.isPressed} $selected")
    }

    inline fun setSeatItemViewSelect(v:View,selected:Boolean){
        val row=getSeatNodeRow(v)
        val column=getSeatNodeColumn(v)
        setItemSelected(row,column,selected)
    }

    fun isSeatItemViewSelected(v:View)=seatArray.isItemSelected(v.tag as Int)

    fun getSeatNodeRow(v:View):Int{
        val id=v.tag as Int
        return id/seatArray.columnCount
    }

    fun getSeatNodeColumn(v:View):Int{
        val id=v.tag as Int
        return id%seatArray.columnCount
    }


    private fun addView(view: View) {
        if(null!=view.layoutParams){
            view.layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        views.add(view)
    }

    fun remove(view:View)=views.remove(view)

    /**
     * 设置当前屏幕缩放值
     */
    fun setHierarchyScale(scale:Float)=setHierarchyScaleInner(scale,width/2f,height/2f)

    fun setHierarchyScaleInner(scale: Float,focusX: Float,focusY: Float,postAction:(()->Unit)?=null){
        cancelZoomAnimator()
        zoomAnimator = ValueAnimator.ofFloat(getMatrixScaleX(), scale).apply {
            duration=200
            addUpdateListener { animation ->
                val matrixScale=getMatrixScaleX()
                val animatedValue=animation.animatedValue as Float
                scaleMatrix.setScale(animatedValue,animatedValue)
                scaleHierarchyScroll(matrixScale,focusX,focusY)
            }
            addListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    postAction?.invoke()
                }
            })
            start()
        }
    }

    /**
     * 终止缩放动画
     */
    private fun cancelZoomAnimator() {
        val animator=zoomAnimator?:return
        animator.removeAllUpdateListeners()
        animator.removeAllListeners()
        animator.cancel()
    }

    /**
     * 缩放动画是否动行,用于与手势onFling区分状态
     */
    private fun zoomAnimatorRunning():Boolean{
        var isRunning=false
        if(null!=zoomAnimator){
            isRunning=zoomAnimator?.isRunning?:false
        }
        return isRunning
    }

    override fun computeHorizontalScrollRange(): Int {
        return Math.max(0,(horizontalRange*getMatrixScaleX()-width).toInt())
    }

    override fun computeVerticalScrollRange(): Int {
        return Math.max(0,(verticalRange*getMatrixScaleY()-height).toInt())
    }

    private fun getMatrixScaleX(): Float {
        scaleMatrix.getValues(m)
        return m[Matrix.MSCALE_X]
    }

    private fun getMatrixScaleY(): Float {
        scaleMatrix.getValues(m)
        return m[Matrix.MSCALE_Y]
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        cancelZoomAnimator()
        viewFlinger.abortAnimation()
        scaleFocusX =detector.focusX
        scaleFocusY =detector.focusY
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector){
        //回弹缩放
        val matrixScaleX = getMatrixScaleX()
        if(hierarchyMinScale>matrixScaleX){
            setHierarchyScaleInner(hierarchyMinScale, scaleFocusX,scaleFocusY,this::checkBorderAndScroll)
        } else if(hierarchyMaxScale<matrixScaleX){
            setHierarchyScaleInner(hierarchyMaxScale, scaleFocusX,scaleFocusY,this::checkBorderAndScroll)
        } else {
            checkBorderAndScroll()
        }
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        //原始缩放比例
        val matrixScaleX = getMatrixScaleX()
        var scaleFactor = detector.scaleFactor
        if(hierarchySpringBackMinScale>scaleFactor*matrixScaleX){
            scaleFactor=hierarchySpringBackMinScale/matrixScaleX
        } else if(hierarchySpringBackMaxScale<scaleFactor*matrixScaleX){
            scaleFactor=hierarchySpringBackMaxScale/matrixScaleX
        }
        //缩放矩阵
        scaleMatrix.postScale(scaleFactor, scaleFactor, scaleFocusX,scaleFocusY)
        //传入原始比例,进行计算,一定需要上面原始比例
        scaleHierarchyScroll(matrixScaleX, scaleFocusX,scaleFocusY)
        return true
    }

    /**
     * 检测边界,并自动滚动到正常边界
     */
    private fun checkBorderAndScroll(){
        //缩放完毕后,检测当前屏幕位置,如果有一部分在范围外,自动滚动到范围内
        var destX=0
        var destY=0
        val horizontalScrollRange = computeHorizontalScrollRange()
        val verticalScrollRange = computeVerticalScrollRange()
        if(0>scrollX){
            destX=-scrollX
        } else if(horizontalScrollRange<scrollX){
            destX=horizontalScrollRange-scrollX
        }
        if(0>scrollY){
            destY=-scrollY
        } else if(verticalScrollRange<scrollY){
            destY=verticalScrollRange-scrollY
        }
        //开始滚动
        viewFlinger.startScroll(scrollX,scrollY,destX,destY)
    }

    /**
     * 缩放视图时,自动滚动位置
     */
    private fun scaleHierarchyScroll(matrixScale:Float, focusX:Float, focusY:Float) {
        //计算出放大中心点
        val scrollX = ((scrollX + focusX) / matrixScale * getMatrixScaleX())
        val scrollY = ((scrollY + focusY) / matrixScale * getMatrixScaleY())
        //动态滚动至缩放中心点
        scrollTo(((scrollX - focusX)).toInt(), ((scrollY - focusY)).toInt())
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        adapter?:return
        val st=System.currentTimeMillis()
        //当前屏幕所占矩阵
        val matrixScaleX = getMatrixScaleX()
        val matrixScaleY = getMatrixScaleY()
        //绘制座位整体信息
        screenRect.set(scrollX, scrollY, scrollX + width, scrollY + height)
        //绘电影院座位
        forEachChild { drawSeatView(canvas, it, matrixScaleX, matrixScaleY) }
        //绘屏幕
        drawScreen(canvas, screenRect, matrixScaleX, matrixScaleY)
        //绘左侧指示器
        drawNumberIndicator(canvas, matrixScaleX, matrixScaleY)
        //绘当前座位描述
        drawSeatLayout(canvas)
        //绘缩略图
        drawPreView(canvas)
        println("onDraw:${System.currentTimeMillis()-st}")

        if(DEBUG){
            val paint= Paint()
            paint.color=Color.RED
            paint.strokeWidth=4f
            //画焦点横线
            canvas.drawLine(scrollX+scaleGestureDetector.focusX-20,
                    scrollY+scaleGestureDetector.focusY,
                    scrollX+scaleGestureDetector.focusX+20,
                    scrollY+scaleGestureDetector.focusY,paint)
            //画焦点竖线
            canvas.drawLine(scrollX+scaleGestureDetector.focusX,
                    scrollY+scaleGestureDetector.focusY-20,
                    scrollX+scaleGestureDetector.focusX,
                    scrollY+scaleGestureDetector.focusY+20,paint)

        }
    }

    /**
     * 绘屏幕
     */
    private fun drawScreen(canvas: Canvas,screenRect:Rect,matrixScaleX: Float, matrixScaleY: Float,drawPreview:Boolean=false) {
        tmpRect.set((viewItem.screenView.left * matrixScaleX).toInt(),
                (viewItem.screenView.top * matrixScaleY).toInt(),
                (viewItem.screenView.right * matrixScaleX).toInt(),
                (viewItem.screenView.bottom * matrixScaleY).toInt())
        if (drawPreview||intersectsRect(screenRect, tmpRect)) {
            //如果屏幕在当前显示范围内,进行绘制
            canvas.save()
            canvas.scale(matrixScaleX, matrixScaleY)
            canvas.translate(viewItem.screenView.left*1f,viewItem.screenView.top*1f)
            viewItem.screenView.draw(canvas)
            canvas.restore()
        }
    }

    /**
     * 绘左侧指示器
     */
    private fun drawNumberIndicator(canvas: Canvas, matrixScaleX: Float, matrixScaleY: Float,drawPreview: Boolean=false) {
        canvas.save()
        canvas.scale(matrixScaleX, matrixScaleY)
        if(drawPreview){
            canvas.translate(viewItem.numberLayout.left*1f, viewItem.numberLayout.top.toFloat())
        } else {
            canvas.translate(scrollX/matrixScaleX+viewItem.numberLayout.left, viewItem.numberLayout.top.toFloat())
        }
        viewItem.numberLayout.draw(canvas)
        canvas.restore()
    }

    /**
     * 绘座位信息布局
     */
    private fun drawSeatLayout(canvas: Canvas) {
        canvas.save()
        canvas.translate(scrollX*1f,scrollY*1f)
        viewItem.seatLayout.draw(canvas)
        canvas.restore()
    }
    /**
     * 绘制当前屏幕内座位
     */
    private fun drawSeatView(canvas: Canvas,childView:View, matrixScaleX: Float, matrixScaleY: Float) {
        canvas.save()
        //按比例放大
        canvas.scale(matrixScaleX, matrixScaleY)
        canvas.translate(childView.left.toFloat(), childView.top.toFloat())
        //是否选中
        childView.isSelected=seatArray.isItemSelected(childView.tag as Int)
        childView.draw(canvas)
        canvas.restore()
    }

    /**
     * 绘制预览图
     */
    private fun drawPreView(canvas: Canvas) {
        //绘制预览bitmap
        canvas.save()
        val offsetHeight=viewItem.seatLayout.height*1f
        canvas.translate(0f, offsetHeight)
        val bitmap=previewBitmap
        if(null!=bitmap){
            //异步初始化预览图
            canvas.drawBitmap(bitmap,scrollX*1f,scrollY*1f,null)
        }
        //当前绘制区域大小
        val measuredWidth=computeHorizontalScrollRange()+width
        //预览尺寸比例
        val matrixScaleX = previewWidth/measuredWidth
        //绘制起始位置
        val left=scrollX+scrollX*matrixScaleX
        val top=scrollY+(scrollY+offsetHeight)*matrixScaleX
        //绘当前屏幕范围
        canvas.drawRect(left,top,left+width*matrixScaleX,top+(height-offsetHeight)*matrixScaleX,previewPaint)
        canvas.restore()
    }


    private inline
    fun intersectsRect(rect1:Rect,rect2:Rect):Boolean{
        return rect1.left < rect2.right && rect2.left < rect1.right && rect1.top < rect2.bottom && rect2.top < rect1.bottom;
    }

    private inline fun intersetsVerticalRect(rect1:Rect, rect2:Rect):Boolean{
        return rect1.top < rect2.bottom && rect2.top < rect1.bottom
    }

    private inline fun intersetsHorizontalRect(rect1:Rect, rect2:Rect):Boolean{
        return rect1.left < rect2.right && rect1.right > rect2.left
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return true
    }

    /**
     * 取消按下控件状态
     */
    private fun releasePressView(){
        views.find { it.isPressed }?.let { it.isPressed=false }
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        views.find { it.isPressed }?.let { it.performClick() }
        releasePressView()
        return true
    }


    override fun onDown(e: MotionEvent): Boolean {
        viewFlinger.abortAnimation()
        val x=scrollX+e.x.toInt()
        val y=scrollY+e.y.toInt()
        val matrixScaleX = getMatrixScaleX()
        val matrixScaleY = getMatrixScaleY()
        run {
            forEachChild { view->
                tmpRect.set((view.left*matrixScaleX).toInt(),
                        (view.top*matrixScaleY).toInt(),
                        (view.right*matrixScaleX).toInt(),
                        (view.bottom*matrixScaleY).toInt())
                //选中状态下,不设置按下状态
                if(tmpRect.contains(x,y)){
                    println("onDown:${view.isSelected}")
                    view.isPressed=true
                }
            }
        }
        return false
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        if(zoomAnimatorRunning()){
            return false
        } else {
            viewFlinger.onFling(velocityX,velocityY)
            return true
        }
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        //当正在进行缩放时,不触发滚动
        if(scaleGestureDetector.isInProgress){
            return false
        } else {
            scrollRange(distanceX, distanceY)
            return true
        }
    }

    /**
     * 滚动视图
     */
    private fun scrollRange(distanceX: Float, distanceY: Float) {
        val horizontalScrollRange = computeHorizontalScrollRange()
        val verticalScrollRange = computeVerticalScrollRange()
        //横轨滚动越界
        var distanceX = distanceX.toInt()
        if (0 > scrollX || scrollX > horizontalScrollRange) {
            //横向直接越界
            distanceX = 0
        } else if (scrollX < -distanceX) {
            //横向向左滚动阀值越界
            distanceX = -scrollX
        } else if (scrollX + distanceX > horizontalScrollRange) {
            //横向向右越界
            distanceX = horizontalScrollRange - scrollX
        }
        //纵向滚动越界
        var distanceY = distanceY.toInt()
        if (0 > scrollY || scrollY > verticalScrollRange) {
            distanceY = 0
        } else if (scrollY < -distanceY) {
            distanceY = -scrollY
        } else if (scrollY + distanceY > verticalScrollRange) {
            distanceY = verticalScrollRange - scrollY
        }
        scrollBy(distanceX, distanceY)
        invalidate()
        //释放按下控件状态
        releasePressView()
    }

    override fun onLongPress(e: MotionEvent) {
        //触发长按事件
        views.find { it.isPressed }?.let {
            if(it.isLongClickable){
                it.performLongClick()
            }
            it.isPressed=false
            invalidate()
        }
    }

    fun forEachChild(action:(View)->Unit)=views.forEach(action)

    fun forEachIndexed(action:(Int,View)->Unit)=views.forEachIndexed(action)

    /**
     * 将二维坐标位置映射为1维id
     */
    inline fun itemId(row:Int,column:Int,columnCount: Int):Int=row*columnCount+column

    /**
     * 一个动态模拟的二维数据数据
     * 主要为了解决大量数据信息快速计算查询
     */
    inner class SeatArray(val rowCount:Int, val columnCount:Int){
        //选中记录信息
        private val selectItems=SparseBooleanArray()
        //横向数据列
        lateinit var rowArray:Array<Rect>
        //纵向数据列
        lateinit var columnArray:Array<Rect>

        fun initRowArray(init:(Int)->Rect){
            rowArray =Array(rowCount,init)
        }

        fun initColumnArray(init:(Int)->Rect){
            columnArray =Array(columnCount,init)
        }

        /**
         * 此矩阵是否选中
         */
        inline fun isItemSelected(row:Int,column:Int)=isItemSelected(itemId(row,column,columnCount))

        fun isItemSelected(id:Int)=selectItems.get(id,false)

        fun setItemSelected(id:Int,selected: Boolean){
            selectItems.put(id,selected)
            println("selectItems:$selectItems")
        }
    }

    /**
     * 预览图绘画对象
     * 负责管理异步绘制预览图.并在绘制时,以16毫秒,通知重绘
     * 单独此类,也为拆离做一些准备
     */
    inner class PreViewPainter:Handler.Callback{
        //重置预览图
        private val RESET_BITMAP=1
        //预览图显示的最大个数,超过100,再进行绘制则没有意义
        private val MAX_COLUMN_COUNT=100
        private val MAX_ROW_COUNT=100
        private val previewThread:HandlerThread = HandlerThread("preview")
        private val previewHandler:Handler

        init {
            //初始化绘制线程以及通信handler对象
            previewThread.start()
            previewHandler=Handler(previewThread.looper,this)
        }

        fun quit()=previewThread.quit()

        /**
         * 重绘预览
         */
        fun resetReviewBitmap()=previewHandler.sendEmptyMessage(RESET_BITMAP)

        override fun handleMessage(msg: Message): Boolean {
            //发送消息直接重置
            if(RESET_BITMAP==msg.what){
                drawReviewBitmap()
            }
            return true
        }
        /**
         * 重置预览图
         */
        private fun drawReviewBitmap() {
            //绘制宽高
            val measuredWidth = computeHorizontalScrollRange() + width
            val measuredHeight = computeVerticalScrollRange() + height
            //当measuredWidth/measuredHeight为0时,不进行预览图初始化
            if (0 < measuredWidth && 0 < measuredHeight) {
                val st = System.currentTimeMillis()
                //此处按宽高比例重新设置previewHeight,因为配置比例与实际比例会有冲突,所以保留宽,高度自适应
                val previewHeight = previewWidth / measuredWidth * measuredHeight
                //计算出预览图缩放比例
                previewBitmap = Bitmap.createBitmap(previewWidth.toInt(), previewHeight.toInt(), Bitmap.Config.RGB_565)
                //绘制预览图
                val matrixScaleX = previewWidth / measuredWidth
                val matrixScaleY = previewHeight / measuredHeight
                val bitmap=previewBitmap
                if(null!=bitmap){
                    drawPreviewSeatRange(Canvas(bitmap), matrixScaleX, matrixScaleY)
                }
                //绘完毕后,通知刷新
                postInvalidate()
                println("drawReviewBitmap:${System.currentTimeMillis() - st} $previewWidth $previewHeight $matrixScaleX")
            }
        }

        /**
         * 绘制缩略图
         * @param canvas 绘制canvas对象,传入对象为预览canvas,则会绘制到bitmap上
         */
        private fun drawPreviewSeatRange(canvas: Canvas,matrixScaleX: Float, matrixScaleY: Float) {
            val adapter=adapter?:return
            //画背景
            thumbBackgroundDrawable?.setBounds(0,0,canvas.width,canvas.height)
            thumbBackgroundDrawable?.draw(canvas)
            //绘序列背景
            drawNumberIndicator(canvas,matrixScaleX,matrixScaleY,true)
            //绘屏幕
            drawScreen(canvas,screenRect,matrixScaleX,matrixScaleY,true)
            //绘所有座位描述
            val st=SystemClock.uptimeMillis()
            run{
                (0 until seatArray.rowCount).forEach { row->
                    (0 until seatArray.columnCount).forEach { column ->
                        if(null!=previewBitmap&&adapter.isSeatVisible(row,column)){
                            val rowLayout=seatArray.rowArray[row]
                            val columnLayout=seatArray.columnArray[column]
                            drawPreviewNode(canvas,viewItem.seatView,matrixScaleX, matrixScaleY,
                                    columnLayout.left,rowLayout.top,columnLayout.right,rowLayout.bottom)
                        }
                    }
                    //当预览图为空,跳出循环
                    if(null==previewBitmap) return@run
                    //绘序列
                    val numberView=viewItem.numberView
                    val rowLayout = seatArray.rowArray[row]
                    adapter.bindSeatNumberView(numberView,row)
                    viewItem.numberLayout.drawPreView(canvas,numberView,rowLayout,matrixScaleX,matrixScaleY,paddingLeft)
                    //如果线程中止,跳出循环
                    //以每一列统计一次刷新时间,减少判断次数
                    if(SystemClock.uptimeMillis()-st>10){
                        //通知重绘
                        postInvalidate()
                    }
                }
            }
        }
    }

    /**
     * 绘制View管理对象
     */
    inner class ViewItem(adapter:SeatTable.SeatTableAdapter){
        //座位信息布局
        val seatLayout:View = adapter.getHeaderSeatLayout(parent as ViewGroup)
        //屏幕信息
        val screenView:View = adapter.getHeaderScreenView(parent as ViewGroup)
        //行序列信息
        val numberView:View = recyclerBin.getNumberView()
        //列指示器
        val numberLayout= SeatNumberIndicator(context)
        //座位信息
        var seatView:View
        //座位左侧偏移
        val leftOffset:Int
            get() = paddingLeft+numberLayout.measuredWidth
        //座位上边偏移
        val topOffset:Int
            get() = paddingTop + seatLayout.measuredHeight + screenView.measuredHeight

        init {
            numberLayout.layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            seatView = recyclerBin.newViewWithMeasured(0,0)
        }

        fun onMeasured(adapter:SeatTable.SeatTableAdapter, widthMode: Int, heightMode: Int){
            //一个绘制临时view
            //这个控件计算尺寸,忽略边距,因为他要铺满
            measureChildWithMargins(seatLayout, widthMode, heightMode, ignorePadding = true)
            measureChildWithMargins(screenView, widthMode, heightMode)
            measureChildWithMargins(seatView, widthMode, heightMode)

            numberLayout.addView(recyclerBin.getNumberView())
            adapter.bindNumberLayout(numberLayout)
            measureChildWithMargins(numberLayout, widthMode, heightMode)
        }

        /**
         * 排版控件
         */
        fun onLayout() {
            //排版顶部描述
            seatLayout.layout(0, 0, seatLayout.measuredWidth, seatLayout.measuredHeight)
            //排版屏幕位置
            val screenTop = seatLayout.measuredHeight + paddingTop
            screenView.layout((horizontalRange - screenView.measuredWidth) / 2, screenTop,
                    (horizontalRange + screenView.measuredWidth) / 2, screenTop + screenView.measuredHeight)
            //排版指示器布局
            numberLayout.layout(paddingLeft,
                    screenTop + screenView.measuredHeight,
                    paddingLeft + numberLayout.measuredWidth,
                    verticalRange - paddingBottom)
            //移除seatView,与numberView,使其独立于整个绘制进程外,作一些辅助绘图
            remove(seatView)
        }

        /**
         * 判断是否初始化,运算阶段为,seatView被移除主控制
         */
        fun isLayoutComplete()=0!=numberLayout.width&&0!=numberLayout.height


    }

    inner class RecyclerBin{
        val scrapViews= LinkedList<View>()
        val scrapNumberViews= LinkedList<View>()

        fun addScarpView(view:View){
            view.isSelected=false
            view.isPressed=false
            scrapViews.add(view)
        }

        fun addScarpNumberView(view:View){
            scrapNumberViews.add(view)
        }

        fun detachAndScrapAttachedViews(){
            views.forEach(this::addScarpView)
            views.clear()
            viewItem.numberLayout.forEach(this::addScarpNumberView)
            viewItem.numberLayout.removeAllViews()
        }

        fun recyclerAll(){
            scrapViews.clear()
            scrapNumberViews.clear()
        }

        fun newViewWithMeasured(row:Int,column: Int):View{
            val adapter= adapter ?:throw NullPointerException("获取View时Adapter不能为空!")
            val parent=parent as ViewGroup
            val view=adapter.getSeatView(parent,row,column)
            adapter.bindSeatView(parent,view,row,column)
            measureChildWithMargins(view, MeasureSpec.AT_MOST, MeasureSpec.AT_MOST)
            view.tag=itemId(row,column,seatArray.columnCount)
            return view
        }

        fun getView(row:Int,column: Int):View{
            val adapter= adapter ?:throw NullPointerException("获取View时Adapter不能为空!")
            val view:View
            if(!scrapViews.isEmpty()){
                view=scrapViews.pollFirst()
            } else {
                view=adapter.getSeatView(parent as ViewGroup,row,column)
                measureChildWithMargins(view,MeasureSpec.AT_MOST,MeasureSpec.AT_MOST)
            }
            view.tag=itemId(row,column,seatArray.columnCount)
            return view
        }

        /**
         * 获得一个指示器控件
         */
        fun getNumberView():View{
            val adapter= adapter ?:throw NullPointerException("获取View时Adapter不能为空!")
            val view:View
            if(!scrapNumberViews.isEmpty()){
                view=scrapNumberViews.pollFirst()
            } else {
                view=adapter.getSeatNumberView(parent as ViewGroup)
                adapter.bindSeatNumberView(view,adapter.getSeatRowCount())
                measureChildWithMargins(view,MeasureSpec.AT_MOST,MeasureSpec.AT_MOST)
            }
            return view
        }

    }

    inner class ViewFlinger:Runnable{
        private val scroller= ScrollerCompat.create(context)

        override fun run() {
            if(!scroller.isFinished&&scroller.computeScrollOffset()){
                scrollTo(scroller.currX,scroller.currY)
                postInvalidate()
                postOnAnimation()
            }
        }

        fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            scroller.startScroll(startX, startY, dx, dy)
            postOnAnimation()
        }

        /**
         * 终止滚动
         */
        fun abortAnimation(){
            scroller.abortAnimation()
            postInvalidate()
        }

        fun onFling(velocityX: Float, velocityY: Float) {
            scroller.fling(scrollX,scrollY,-velocityX.toInt(),-velocityY.toInt(),0,computeHorizontalScrollRange(),0,computeVerticalScrollRange())
            postOnAnimation()
        }

        fun postOnAnimation() {
            removeCallbacks(this)
            ViewCompat.postOnAnimation(this@SeatTable, this)
        }
    }
    /**
     *
     */
    abstract class SeatTableAdapter(val table: SeatTable){
        /**
         * 获得顶部座位
         */
        abstract fun getHeaderSeatLayout(parent:ViewGroup):View
        /**
         * 获得屏幕控件
         */
        abstract fun getHeaderScreenView(parent:ViewGroup):View

        /**
         * 获得座位排左侧指示控件
         */
        abstract fun getSeatNumberView(parent:ViewGroup):View

        /**
         * 绑定座位序列
         */
        open fun bindSeatNumberView(view:View,row:Int)=Unit
        /**
         * 绑定序号列数据
         */
        open fun bindNumberLayout(numberLayout:View)=Unit
        /**
         * 获得座位号
         */
        abstract fun getSeatView(parent:ViewGroup,row:Int,column:Int):View

        /**
         * 绑定座位数据
         */
        abstract fun bindSeatView(parent:ViewGroup,view:View,row:Int,column:Int)

        /**
         * 获得座位列数
         */
        abstract fun getSeatColumnCount():Int

        /**
         * 获得座位排数
         */
        abstract fun getSeatRowCount():Int

        /**
         * 获得横向多余空间
         */
        abstract fun getHorizontalSpacing(column:Int):Int

        /**
         * 获得纵向多余空间
         */
        abstract fun getVerticalSpacing(row:Int):Int

        /**
         * 某个座位是否可见
         */
        open fun isSeatVisible(row:Int,column:Int)=true

        /**
         * 选中一个条目
         */
        fun setItemSelected(row:Int, column:Int, selected:Boolean){
            table.setItemSelected(row,column, selected)
        }

        fun setSeatItemViewSelect(v:View, selected:Boolean){
            table.setSeatItemViewSelect(v, selected)
        }

        /**
         * 指定控件是否选中
         */
        fun isSeatItemViewSelected(v:View)=table.isSeatItemViewSelected(v)

        fun getSeatNodeRow(v:View):Int=table.getSeatNodeRow(v)

        fun getSeatNodeColumn(v:View):Int=table.getSeatNodeColumn(v)


    }


}
