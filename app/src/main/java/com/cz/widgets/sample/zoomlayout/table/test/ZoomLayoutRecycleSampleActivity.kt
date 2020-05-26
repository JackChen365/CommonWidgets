package com.cz.widgets.sample.zoomlayout.table.test

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.data.DataManager
import com.cz.widgets.sample.R
import kotlinx.android.synthetic.main.activity_zoom_layout_recycle_sample.*

@SampleSourceCode
@RefRegister(title= R.string.zoom_recycler_layout,desc = R.string.zoom_recycler_layout_desc,category = R.string.zoom_table)
class ZoomLayoutRecycleSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_layout_recycle_sample)

        val rowCount=40
        val columnCount=1
        var startIndex=0
        val dataProvider = DataManager.getDataProvider(this)
        val list = (0 until rowCount).map { dataProvider.getWordList(startIndex++, columnCount).toList() }.toList()
        val tableAdapter = SimpleRecyclerTableLayoutAdapter(this, list)
        zoomLayout.setAdapter(tableAdapter)
        scaleButton.setOnClickListener {
            zoomLayout.setViewScale(3f)
        }
        seekLayout.setOnProgressChangeListener { seekBar, i, _ ->
            val fraction = 1f+i * 1f / seekBar.max
            zoomLayout.setScaleAnimation(fraction)
        }

        zoomLayout.setOnHierarchyChangeListener(object:ViewGroup.OnHierarchyChangeListener{
            override fun onChildViewAdded(parent: View, child: View) {
                val output = StringBuilder()
                val childCount = zoomLayout.childCount
                for (i in 0 until childCount) {
                    val childView = zoomLayout.getChildAt(i)
                    val layoutParams = childView.getLayoutParams() as TableZoomTestLayout.LayoutParams
                    output.append("${layoutParams.row} ")
                }
                positionText.text="Position:$output"
            }

            override fun onChildViewRemoved(parent: View, child: View) {
                val output = StringBuilder()
                val childCount = zoomLayout.childCount
                for (i in 0 until childCount) {
                    val childView = zoomLayout.getChildAt(i)
                    val layoutParams = childView.getLayoutParams() as TableZoomTestLayout.LayoutParams
                    output.append("${layoutParams.row} ")
                }
                positionText.text="Position:$output"
            }
        })
    }
}
