package com.cz.widgets.sample.zoomlayout.table

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.data.DataManager
import com.cz.widgets.sample.R
import com.cz.widgets.sample.zoomlayout.decoration.TableDecoration
import kotlinx.android.synthetic.main.activity_zoom_table_layout_sample.*

@SampleSourceCode
@RefRegister(title= R.string.zoom_layout,desc = R.string.zoom_layout_desc,category = R.string.zoom_table)
class TableZoomLayoutSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom_table_layout_sample)

        var startIndex=0
        val rowCount=40
        val columnCount=6
        val dataProvider = DataManager.getDataProvider(this)
        val list = (0 until rowCount).map { dataProvider.getWordList(startIndex++, columnCount).toList() }.toList()
        val tableAdapter = SimpleTableLayoutAdapter(this, list)

        zoomLayout.addItemDecoration(TableDecoration(this))
        zoomLayout.setAdapter(tableAdapter)
        scaleButton.setOnClickListener {
            zoomLayout.setViewScale(1.5f)
        }
    }
}
