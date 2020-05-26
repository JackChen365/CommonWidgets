package com.cz.laboratory.app.android.view.hierarchy

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cz.laboratory.app.R
import kotlinx.android.synthetic.main.android_view_hierarchy_fragment_detail.*

/**
 * Created by cz on 2017/10/13.
 */
class AndroidViewHierarchyTreeFragment : Fragment(){
    private lateinit var node: HierarchyNode
    companion object {
        fun newInstance(node: HierarchyNode):Fragment{
            val fragment= AndroidViewHierarchyTreeFragment()
            fragment.node=node
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.android_view_hierarchy_fragment_detail,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val context=context?:return
        hierarchyView.setAdapter(SimpleHierarchyAdapter(context,node))

        buttonScale.setOnClickListener {
            hierarchyView.setHierarchyScale(2f)
        }
    }

    class SimpleHierarchyAdapter(private val context: Context, node:HierarchyNode):AbsHierarchyLayout.HierarchyAdapter(node){
        private val layoutInflate=LayoutInflater.from(context)
        override fun getView(parent:ViewGroup): View {
            return layoutInflate.inflate(R.layout.android_view_hierarchy_item,parent,false)
        }

        override fun bindView(view: View, node: HierarchyNode) {
            view.findViewById<TextView>(R.id.viewClassNameText).text=node.name
            view.findViewById<TextView>(R.id.viewResourceName).text=node.entryName
            view.findViewById<TextView>(R.id.viewRectText).text=node.rect.toString()
            view.findViewById<TextView>(R.id.viewDescriptionText).text=node.description
            view.setOnClickListener {
                Toast.makeText(context,"点击:${node.name} 节点!",Toast.LENGTH_SHORT).show()
            }
        }

    }
}