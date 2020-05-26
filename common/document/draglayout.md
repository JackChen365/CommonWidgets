## DragLayout

### Picture

* ![DragLayout](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/view_drag.gif?raw=true)

### Style Prototype

```
<attr name="dragFrameLayout" format="reference"/>
<declare-styleable name="DragLayout" >
    <attr name="layout_dragHandle" format="reference"/>
    <attr name="layout_dragMode" >
        <flag name="horizontal" value="0x01"/>
        <flag name="vertical" value="0x02"/>
        <flag name="unlimited" value="0x04"/>
    </attr>
</declare-styleable>
```

### Usage

If the view is the child of the view group. You could use the layout params


```
<com.cz.widgets.common.drag.DragLayout
        android:id="@+id/dragLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

    <androidx.appcompat.widget.LinearLayoutCompat
        android:id="@+id/innerLayout"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:layout_margin="12dp"
        android:layout_gravity="right|bottom"

        //The drag direction.
        app:layout_dragMode="horizontal|vertical"
        //The drag handle.
        app:layout_dragHandle="@id/viewHandler1">
        ...
    </androidx.appcompat.widget.LinearLayoutCompat>
</com.cz.widgets.common.drag.DragLayout>
```

If you want to drag the child inside the ViewGroup but inside the child view.
You have to register the drag item manually.

```
//1. Drag the view vertically without handle.
dragLayout.addDragView(R.id.dragView2,DragLayout.VERTICAL, View.NO_ID)

//2. Drag the view anywhere no limitation without handle.
dragLayout.addDragView(R.id.dragView3,DragLayout.VERTICAL or DragLayout.HORIZONTAL or DragLayout.UNLIMITED,View.NO_ID)

//3. Drag the view inside the parent's boundary use the handle.
dragLayout.addDragView(R.id.noteLayout,DragLayout.VERTICAL or DragLayout.HORIZONTAL,R.id.viewHandler)
```
