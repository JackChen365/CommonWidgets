package com.cz.widgets.sample.textview.span.table

import android.os.Bundle
import android.text.SpannableString
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.data.DataManager
import com.cz.widgets.sample.R
import com.cz.widgets.textview.span.table.TableLayout
import com.cz.widgets.textview.span.view.TouchableViewSpan
import kotlinx.android.synthetic.main.activity_text_table_sample.*
import java.util.*

@SampleSourceCode
@RefRegister(title=R.string.text_table_span_title,desc=R.string.text_table_span_desc,category = R.string.text_span)
class TextTableSampleActivity : AppCompatActivity() {
    companion object{
        private const val WRAP_CONTENT = 0
        private const val MATCH_PARENT = 1
    }
    private var tableSizeMode= MATCH_PARENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_table_sample)
        initTextTables(tableSizeMode)
        randomButton.setOnClickListener { initTextTables(tableSizeMode) }
        radioLayout.setOnCheckedChangeListener { _, position, selected ->
            if(selected){
                if(0==position){
                    initTextTables(WRAP_CONTENT)
                } else if(1==position){
                    initTextTables(MATCH_PARENT)
                }
                tableSizeMode=position
            }
        }
    }

    /**
     * Initialize the table cells.
     */
    private fun initTextTables(tableSizeMode:Int) {
        val textItems = mutableMapOf(
            textView to R.string.text_test2
        )
        val random= Random()
        val dataProvider = DataManager.getDataProvider(this)
        var startIndex = random.nextInt(dataProvider.wordList.size/2)
        textItems.forEach { (textView, textRes) ->
            val spannableString = SpannableString(getString(textRes))
            var start = 0
            do {
                val index = spannableString.indexOf("，", start)
                if (0 > index) break
                val rowCount=1+random.nextInt(3)
                val columnCount=1+random.nextInt(2)
                val list = (0..rowCount).map { dataProvider.getWordList(startIndex++, columnCount).toList() }.toList()
                val tableAdapter = SimpleTableAdapter(this, list)
                val tableLayout= TableLayout(this)
                tableLayout.layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                tableLayout.setAdapter(tableAdapter)
                if (MATCH_PARENT == tableSizeMode) {
                    tableLayout.layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT
                } else {
                    tableLayout.layoutParams.width=ViewGroup.LayoutParams.WRAP_CONTENT
                }
                val tableSpan = TouchableViewSpan(textView, tableLayout)
                spannableString.setSpan(tableSpan, index, index + 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                start = index + 1
            } while (-1 != index)
            textView.text = spannableString
        }
    }

}
