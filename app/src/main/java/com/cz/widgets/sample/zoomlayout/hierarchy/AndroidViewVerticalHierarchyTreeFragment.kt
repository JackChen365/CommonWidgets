package com.cz.widgets.sample.zoomlayout.hierarchy

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cz.android.sample.api.Exclude
import com.cz.widgets.sample.R
import com.cz.widgets.sample.zoomlayout.decoration.VerticalHierarchyItemDecoration
import com.cz.widgets.zoomlayout.tree.TreeNode
import com.cz.widgets.zoomlayout.tree.hierarchy.VerticalHierarchyLayout
import kotlinx.android.synthetic.main.fragment_zoom_vertical_hierarchy_sample.*

/**
 * Created by cz on 2017/10/13.
 */
@Exclude
class AndroidViewVerticalHierarchyTreeFragment : Fragment(){
    private lateinit var node: TreeNode<View>
    companion object {
        fun newInstance(node: TreeNode<View>):Fragment{
            val fragment= AndroidViewVerticalHierarchyTreeFragment()
            fragment.node=node
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_zoom_vertical_hierarchy_sample,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val context=context?:return
        hierarchyView.addItemDecoration(VerticalHierarchyItemDecoration(context))
        hierarchyView.setAdapter(SimpleHierarchyAdapter(context,node))

        //Initialize the preview
        previewLayout.attachToHostView(hierarchyView)

        scaleButton.setOnClickListener {
            hierarchyView.setViewScale(2f)
        }
    }

    class SimpleHierarchyAdapter(context: Context, node: TreeNode<View>):VerticalHierarchyLayout.Adapter<View>(node){
        private val tmpRect= Rect()
        private val layoutInflate=LayoutInflater.from(context)

        override fun onCreateView(parent: ViewGroup, viewType: Int): View {
            return layoutInflate.inflate(R.layout.zoom_hierarchy_item,parent,false)
        }

        override fun onBindView(context:Context, view: View, node: TreeNode<View>, itemView: View) {
            val viewClassNameText = view.findViewById<TextView>(R.id.viewClassNameText)
            val viewResourceName = view.findViewById<TextView>(R.id.viewResourceName)
            viewClassNameText.text=itemView::class.java.simpleName
            val resourceEntryName = if(itemView.id==View.NO_ID) "#" else context.resources.getResourceEntryName(itemView.id)
            viewResourceName.text=resourceEntryName

            view.findViewById<TextView>(R.id.viewDescriptionText).text=itemView.contentDescription

            itemView.getGlobalVisibleRect(tmpRect)
            view.findViewById<TextView>(R.id.viewRectText).text=tmpRect.toString()

            view.setOnClickListener {
                Toast.makeText(context, "Click:$resourceEntryName", Toast.LENGTH_SHORT).show()
            }
        }


    }
}