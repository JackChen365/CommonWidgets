package com.cz.laboratory.app.android.view.hierarchy

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.data.DataManager
import com.cz.android.sample.library.data.DataProvider
import com.cz.laboratory.app.R
import kotlinx.android.synthetic.main.android_view_genealogy_fragment.*

/**
 * @author Created by cz
 * @date 2020-02-01 19:08
 * @email bingo110@126.com
 */
@SampleSourceCode
@RefRegister(title= R.string.android_view_genealogy_title,desc = R.string.android_view_genealogy_desc,category = R.string.android_view)
class AndroidViewGenealogyFragment : Fragment() {
    private val nameArray = arrayOf("Asadero", "Asiago", "Aubisque Pyrenees", "Autun", "Avaxtskyr", "Baby Swiss",
        "Babybel", "Baguette Laonnaise", "Bakers", "Baladi", "Balaton", "Bandal", "Banon",
        "Barry's Bay Cheddar", "Basing", "Basket Cheese", "Bath Cheese", "Bavarian Bergkase",
        "Baylough", "Beaufort", "Beauvoorde", "Beenleigh Blue", "Beer Cheese", "Bel Paese",
        "Bergader", "Bergere Bleue", "Berkswell", "Beyaz Peynir", "Bierkase", "Bishop Kennedy",
        "Blarney", "Bleu d'Auvergne", "Bleu de Gex", "Bleu de Laqueuille",
        "Bleu de Septmoncel", "Bleu Des Causses", "Blue", "Blue Castello", "Blue Rathgore",
        "Blue Vein (Australian)", "Blue Vein Cheeses", "Bocconcini", "Bocconcini (Australian)",
        "Boeren Leidenkaas", "Bonchester", "Bosworth", "Bougon", "Boule Du Roves",
        "Boulette d'Avesnes", "Boursault", "Boursin", "Bouyssou", "Bra", "Braudostur",
        "Breakfast Cheese", "Brebis du Lavort", "Brebis du Lochois", "Brebis du Puyfaucon",
        "Bresse Bleu", "Brick", "Brie", "Brie de Meaux", "Brie de Melun", "Brillat-Savarin",
        "Brin", "Brin d' Amour", "Brin d'Amour", "Brinza (Burduf Brinza)")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.android_view_genealogy_fragment, container, false);
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val context= context?:return
        val dataProvider = DataManager.getDataProvider(context)

        val decorView = activity?.window?.decorView
        if(null!=decorView){
            val root=GenealogyNode(0,"#",0)
            viewTraversal(dataProvider,decorView,root,1)
            genealogyView.setAdapter(SimpleGenealogyAdapter(context,root))
        }
        buttonScale.setOnClickListener {
            genealogyView.setHierarchyScale(2f)
        }
    }

    /**
     * 遍历所有控件层级节点
     */
    private fun viewTraversal(dataProvider: DataProvider,view: View, parent:HierarchyNode, level:Int){
        //随机生成家庭成员信息
        val gender=if(DataProvider.RANDOM.nextBoolean()) 0 else 1
        val name = nameArray[DataProvider.RANDOM.nextInt(nameArray.size)]
        val node=GenealogyNode(level,name,gender)
        //记录父节点
        node.parent=parent
        //记录子节点
        parent.children.add(node)
        if(view is ViewGroup){
            for(i in 0 until view.childCount){
                val childView = view.getChildAt(i)
                viewTraversal(dataProvider,childView,node,level+1)
            }
        }
    }

    class GenealogyNode(level: Int, name: String,val gender:Int) : HierarchyNode(level, name)


    class SimpleGenealogyAdapter(private val context: Context, node:GenealogyNode):AbsHierarchyLayout.HierarchyAdapter(node){
        private val layoutInflate=LayoutInflater.from(context)
        override fun getView(parent:ViewGroup): View {
            return layoutInflate.inflate(R.layout.android_view_genealogy_item,parent,false)
        }

        override fun bindView(view: View, node: HierarchyNode) {
            val genealogyNode=node as GenealogyNode
            val hierarchyDepth = getHierarchyDepth()
            val familyMemberIcon=view.findViewById<ImageView>(R.id.familyMemberIcon)
            val familyMemberRelation=view.findViewById<TextView>(R.id.familyMemberRelation)
            val familyMember=view.findViewById<TextView>(R.id.familyMember)
            if(node.level+1<hierarchyDepth){
                //父级超过2级以上
                if(0==genealogyNode.gender){
                    //男性
                    familyMemberIcon.setImageResource(R.mipmap.android_view_genealogy_grand_father)
                    familyMemberRelation.text =
                        context.getString(R.string.android_view_genealogy_ancestor_level_man,node.level+1)
                    familyMember.text=node.name
                } else {
                    //女性
                    familyMemberIcon.setImageResource(R.mipmap.android_view_genealogy_grand_mother)
                    familyMemberRelation.text =
                        context.getString(R.string.android_view_genealogy_ancestor_level_woman,node.level+1)
                    familyMember.text=node.name
                }
            } else {
                familyMember.text=node.name
                if(0==genealogyNode.gender){
                    if(node.level==hierarchyDepth){
                        familyMemberIcon.setImageResource(R.mipmap.android_view_genealogy_boy)
                    } else {
                        familyMemberIcon.setImageResource(R.mipmap.android_view_genealogy_uncle)
                    }
                    //男性
                    familyMemberRelation.text =
                        context.getString(R.string.android_view_genealogy_junior_level_man,node.level+1)
                } else {
                    if(node.level==hierarchyDepth){
                        familyMemberIcon.setImageResource(R.mipmap.android_view_genealogy_girl)
                    } else {
                        familyMemberIcon.setImageResource(R.mipmap.android_view_genealogy_aunt)
                    }
                    //女性
                    familyMemberRelation.text =
                        context.getString(R.string.android_view_genealogy_junior_level_woman,node.level+1)
                }
            }
            view.setOnClickListener {
                Toast.makeText(context,"Clicked:${node.name} The:"+(node.level+1)+" Generation", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
