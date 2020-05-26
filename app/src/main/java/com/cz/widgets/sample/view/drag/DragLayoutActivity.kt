package com.cz.widgets.sample.view.drag

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widgets.common.drag.DragLayout
import com.cz.widgets.sample.R
import kotlinx.android.synthetic.main.activity_view_drag_layout.*

/**
 * @author Created by cz
 * @date 2020-05-24 14:48
 * @email bingo110@126.com
 */
@SampleSourceCode
@RefRegister(title= R.string.view_drag_view,desc= R.string.view_drag_view_desc,category = R.string.view)
class DragLayoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_drag_layout)

        dragLayout.addDragView(R.id.dragView1, DragLayout.HORIZONTAL, View.NO_ID)
        dragLayout.addDragView(R.id.dragView2,DragLayout.VERTICAL, View.NO_ID)
        dragLayout.addDragView(R.id.dragView3,DragLayout.VERTICAL or DragLayout.HORIZONTAL or DragLayout.UNLIMITED,View.NO_ID)
        dragLayout.addDragView(R.id.noteLayout,DragLayout.VERTICAL or DragLayout.HORIZONTAL,R.id.viewHandler)
        dragLayout.addDragView(R.id.innerLayout,DragLayout.VERTICAL or DragLayout.HORIZONTAL,R.id.viewHandler1)

        for(i in 0 until contentLayout.childCount){
            val childView=contentLayout.getChildAt(i)
            childView.setOnClickListener {
                Toast.makeText(applicationContext,"点击:$i",Toast.LENGTH_SHORT).show()
            }
        }

    }
}
