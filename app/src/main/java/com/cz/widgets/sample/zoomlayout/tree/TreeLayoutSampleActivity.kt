package com.cz.widgets.sample.zoomlayout.tree

import android.content.Context
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cz.widgets.sample.R
import com.cz.widgets.sample.data.Data
import com.cz.widgets.zoomlayout.tree.TreeLayout
import com.cz.widgets.zoomlayout.tree.TreeNode
import kotlinx.android.synthetic.main.activity_tree_layout_sample.*

//@RefRegister(title= R.string.zoom_tree_layout,desc = R.string.zoom_tree_layout_desc,category = R.string.zoom)
class TreeLayoutSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tree_layout_sample)
        val maxDepth = 4
        val breadthCount = 1
        val root = TreeNode<String>()
        root.value="The egg"
        buildTree(root, breadthCount, 0, maxDepth)
        val tableAdapter = SimpleTreeAdapter(this, root)
        treeLayout.setAdapter(tableAdapter)
        previewLayout.attachToHostView(treeLayout)
    }

    private fun buildTree(parent: TreeNode<String>, levelCount: Int, depth: Int, maxDepth: Int) {
        var levelCount = levelCount
        if (depth < maxDepth) {
            for (i in 0 until levelCount) {
                val value = "The " + depth + "generation"
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

    class SimpleTreeAdapter(context: Context, node: TreeNode<String>): TreeLayout.Adapter<String>(node){
        private val layoutInflate= LayoutInflater.from(context)
        private val imageArray=SparseArray<String>()

        override fun onCreateView(parent: ViewGroup, viewType: Int): View {
            return layoutInflate.inflate(R.layout.zoom_tree_genealogy_item,parent,false)
        }

        override fun onBindView(context: Context, view: View, node: TreeNode<String>, item: String) {
//            val userCover = view.findViewById<ImageView>(R.id.userCover)
            val userName = view.findViewById<TextView>(R.id.userName)
//            val userDesc = view.findViewById<TextView>(R.id.userDesc)
            val index = node.depth * Short.MAX_VALUE + node.centerBreadth
            var image: String? = imageArray.get(index)
            if (null == image) {
                image = Data.getImage()
                imageArray.put(index, image)
            }
//            Glide.with(view.context).load(image).transition(withCrossFade()).into(userCover)
            userName.text=item
            view.setOnClickListener {
                Toast.makeText(context, "Click:"+"Depth:"+node.depth+" breadth:"+node.breadth, Toast.LENGTH_SHORT).show()
            }
        }


    }
}
