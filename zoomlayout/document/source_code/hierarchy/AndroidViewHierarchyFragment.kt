package com.cz.laboratory.app.android.view.hierarchy

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.data.DataManager
import com.cz.android.sample.library.data.DataProvider
import com.cz.laboratory.app.R
import kotlinx.android.synthetic.main.android_view_hierarchy_fragment.*

/**
 * @author Created by cz
 * @date 2020-02-01 19:08
 * @email bingo110@126.com
 */
@SampleSourceCode
@RefRegister(title= R.string.android_view_hierarchy_title,desc = R.string.android_view_hierarchy_desc,category = R.string.android_view)
class AndroidViewHierarchyFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.android_view_hierarchy_fragment, container, false);
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val layoutInflater = LayoutInflater.from(context);
        val dataService = DataManager.getDataProvider(context)

        val colorArrayLength = DataProvider.COLOR_BLUE_GREY
        for(i in 0 until colorArrayLength){
            val colorArray = dataService.getColorArray(i)
            val levelFrameLayout = layoutInflater.inflate(
                R.layout.android_view_hierarchy_test_item,
                hierarchyLayoutContainer,
                false
            )
            setDifferentLevelColor(colorArray,levelFrameLayout,0)
            hierarchyLayoutContainer.addView(levelFrameLayout)
        }
        showButton.setOnClickListener { v->
            val decorView = activity?.window?.decorView
            if(null!=decorView){
                v.visibility=View.GONE
                val root=HierarchyNode(0,"#")
                viewTraversal(decorView,root,1)
                childFragmentManager.beginTransaction().add(R.id.fragmentHierarchyContainer,AndroidViewHierarchyTreeFragment.newInstance(root)).commit()
            }
        }
    }

    /**
     * Set different color into different view
     */
    private fun setDifferentLevelColor(colorArray: IntArray, view: View,index:Int) {
        if(index < colorArray.size){
            view.setBackgroundColor(colorArray[index])
            if(view is ViewGroup&&0 < view.childCount){
                val childView = view.getChildAt(0) as ViewGroup
                setDifferentLevelColor(colorArray,childView,index+1)
            }
        }
    }

    /**
     * 遍历所有控件层级节点
     */
    private fun viewTraversal(view: View, parent:HierarchyNode, level:Int){
        val node=HierarchyNode(level,view::class.java.simpleName)
        //记录id
        node.id=view.id
        //记录控件描述
        node.description=view.contentDescription
        if(view.id!=View.NO_ID){
            //记录id
            node.entryName=resources.getResourceEntryName(view.id)
        }
        //记录控件所占矩阵
        val rect= Rect()
        view.getGlobalVisibleRect(rect)
        node.rect.set(rect)

        //记录父节点
        node.parent=parent
        //记录子节点
        parent.children.add(node)
        if(view is ViewGroup){
            for(i in 0 until view.childCount){
                val childView = view.getChildAt(i)
                viewTraversal(childView,node,level+1)
            }
        }
    }

}
