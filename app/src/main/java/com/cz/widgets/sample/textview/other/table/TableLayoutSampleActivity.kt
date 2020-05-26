package com.cz.widgets.sample.textview.other.table

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.data.DataManager
import com.cz.widgets.sample.R
import kotlinx.android.synthetic.main.activity_text_table_layout_sample.*

@SampleSourceCode
@RefRegister(title=R.string.text_table_layout_title,desc=R.string.text_table_layout_desc,category = R.string.text_other)
class TableLayoutSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_table_layout_sample)

        val rowCount=6
        val columnCount=2
        val dataProvider = DataManager.getDataProvider(this)
//        var startIndex = Random.nextInt(dataProvider.wordList.size/2)
        var startIndex=9
        val list = (0 until rowCount).map { dataProvider.getWordList(startIndex++, columnCount).toList() }.toList()
        val tableAdapter = SimpleTableLayoutAdapter(this, list)
        tableLayout.setAdapter(tableAdapter)
    }
}
