package com.cz.laboratory.app.android.view.hierarchy

import android.graphics.Rect
import android.view.View

/**
 * Created by cz on 2017/10/12.
 * 视图节点
 */
open class HierarchyNode(val level:Int,val name:String){
    //资源id
    var id:Int= View.NO_ID
    //id文本
    var entryName:String?=null
    //view屏幕矩阵
    var rect= Rect()
    //节点排版矩阵
    var layoutRect=Rect()
    //描述
    var description:CharSequence?=null

    //子节点纵深层级
    var childDepth =0
    //当前列起始深度
    var startDepth =0
    //排列结束
    var endDepth =0
    //父节点
    var parent:HierarchyNode?=null
    //子集
    var children = mutableListOf<HierarchyNode>()
}