package com.cz.widgets.sample.zoomlayout.hierarchy.test

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widgets.sample.R
import com.cz.widgets.zoomlayout.tree.TreeNode
import kotlinx.android.synthetic.main.activity_hierarchy_recycler_test_sample.*

@SampleSourceCode
@RefRegister(title= R.string.zoom_hierarchy_recycler_layout,desc = R.string.zoom_hierarchy_recycler_layout_desc,category = R.string.zoom_hierarchy)
class HierarchyRecyclerTestSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hierarchy_recycler_test_sample)

        val maxDepth = 2
        val breadthCount = 2
        val root = TreeNode<String>()
        root.value="#"
        buildTree(root, breadthCount, 0, maxDepth)
        val tableAdapter =
            SimpleTreeAdapter(
                this,
                root
            )
        zoomLayout.setAdapter(tableAdapter)
    }

    private fun buildTree(parent: TreeNode<String>, levelCount: Int, depth: Int, maxDepth: Int) {
        var levelCount = levelCount
        if (depth < maxDepth) {
            for (i in 0 until levelCount) {
                val value = "Level:" + depth + ('A'.toInt() + i).toChar()
                val child = TreeNode<String>()
                child.value=value
                child.depth = depth
                child.parent = parent
                parent.children.add(child)

                levelCount = 3
                buildTree(child, levelCount, depth + 1, maxDepth)
            }
        }
    }

    class SimpleTreeAdapter(context: Context, node: TreeNode<String>): HierarchyTestLayout.Adapter<String>(node){
        private val layoutInflate= LayoutInflater.from(context)

        override fun onCreateView(parent: ViewGroup, viewType: Int): View {
            return layoutInflate.inflate(R.layout.zoom_tree_test_item,parent,false)
        }

        override fun onBindView(context: Context, view: View, node: TreeNode<String>, item: String) {
            val viewTitleText = view.findViewById<TextView>(R.id.viewTitleText)
            val viewDescName = view.findViewById<TextView>(R.id.viewDescName)
            viewTitleText.text=item
            viewDescName.text="Depth:"+node.depth+" breadth:"+node.breadth
            view.setOnClickListener {
                Toast.makeText(context, "Click:"+"Depth:"+node.depth+" breadth:"+node.breadth, Toast.LENGTH_SHORT).show()
            }
        }


    }
}
