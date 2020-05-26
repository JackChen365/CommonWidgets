## HierarchyLayout

The first time I want to have a hierarchy layout is because of the DDMS HierarchyLayout. Once I developer a debug tool I want to show the window hierarchy. But I can not do that.

Three year ago. I develop the first version of the View.

[HierarchyView first version](./source_code/hierarchy/AbsHierarchyLayout.kt)

It all drawing on a view. By handle the touch event manually we support click event. and all the touch gesture.
It is better than any views you could search on the internet. At least better than any views I could search on the internet.

Because all the things I've found it just uses the image matrix to scale and translation. Not even support the keep scrolling after finger left the screen.
For me it is very bad experience.

### Picture
![zoom_hierarchy](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/zoom_hierarchy.gif?raw=true)

### Style

```
<attr name="zoom_minimum" format="float"/>
<attr name="zoom_maximum" format="float"/>
<attr name="zoom_enabled" format="boolean"/>
```

### Usage

1. Build the tree and initial the tree adapter.

```
//Build the tree.
val decorView = window?.decorView
if(null!=decorView){
    val root= TreeNode<View>()
    viewTraversal(frameLayout,null,root)
}
/**
 * Traverse all the view in this Activity window.
 */
private fun viewTraversal(view: View, parent: TreeNode<View>?,node: TreeNode<View>){
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


//The tree adapter.
class SimpleHierarchyAdapter(context: Context, node: TreeNode<View>):HierarchyLayout.Adapter<View>(node){
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
```

That's all we have to do.

There are some things I should mention. It's base on RecyclerZoomLayout So It supports ItemDecoration and zoom gesture.

We use ZoomItemDecoration to support anything you want decorate for the tree.
Here I use the ZoomItemDecoration to connect each node in the tree.

see:[HierarchyItemDecoration](HierarchyItemDecoration)

### References.

* [The zoom strategy](./zoom.md).
* [The recycle strategy](./zoom_recycler.md).
* [The preview layout](./preview.md).