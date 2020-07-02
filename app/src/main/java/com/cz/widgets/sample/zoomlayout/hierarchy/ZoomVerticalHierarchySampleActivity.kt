package com.cz.widgets.sample.zoomlayout.hierarchy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.data.DataManager
import com.cz.android.sample.library.data.DataProvider
import com.cz.widgets.sample.R
import com.cz.widgets.zoomlayout.tree.TreeNode
import kotlinx.android.synthetic.main.activity_zoom_vertical_hierarchy_sample.*

@RefRegister(
    title = R.string.zoom_hierarchy_title2,
    desc = R.string.zoom_hierarchy_desc2,
    category = R.string.zoom_hierarchy
)
class ZoomVerticalHierarchySampleActivity :
    AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_vertical_hierarchy_sample)
        val layoutInflater = LayoutInflater.from(this)
        val dataProvider = DataManager.getDataProvider(this)

        val colorArrayLength = DataProvider.COLOR_BLUE_GREY
        for(i in 0 until 1){
            val colorArray = dataProvider.getColorArray(i)
            val levelFrameLayout = layoutInflater.inflate(
                R.layout.zoom_view_hierarchy_test_item,
                hierarchyLayoutContainer,
                false
            )
            setDifferentLevelColor(colorArray,levelFrameLayout,0)
            hierarchyLayoutContainer.addView(levelFrameLayout)
        }
        runOnUiThread{
            showButton.visibility= View.GONE
            val decorView = window?.decorView
            if(null!=decorView){
                val root= TreeNode<View>()
                viewTraversal(frameLayout,null,root)
                supportFragmentManager.beginTransaction().add(R.id.fragmentHierarchyContainer,AndroidViewVerticalHierarchyTreeFragment.newInstance(root)).commit()
            }
        }
        showButton.setOnClickListener { v->
            val decorView = window?.decorView
            if(null!=decorView){
                v.visibility= View.GONE
                val root= TreeNode<View>()
                viewTraversal(decorView,null,root)
                supportFragmentManager.beginTransaction().add(R.id.fragmentHierarchyContainer,AndroidViewVerticalHierarchyTreeFragment.newInstance(root)).commit()
            }
        }
    }

    /**
     * Set different color into different view
     */
    private fun setDifferentLevelColor(colorArray: IntArray, view: View, index:Int) {
        if(index < colorArray.size){
            view.setBackgroundColor(colorArray[index])
            if(view is ViewGroup &&0 < view.childCount){
                val childView = view.getChildAt(0) as ViewGroup
                setDifferentLevelColor(colorArray,childView,index+1)
            }
        }
    }

    /**
     * Traverse all the view in this Activity window.
     */
    private fun viewTraversal(view: View, parent: TreeNode<View>?, node: TreeNode<View>){
        node.value=view
        node.parent=parent
        parent?.children?.add(node)
        if(view is ViewGroup){
            for(i in 0 until view.childCount){
                val childView = view.getChildAt(i)
                val child= TreeNode<View>()
                viewTraversal(childView,node,child)
            }
        }
    }

}