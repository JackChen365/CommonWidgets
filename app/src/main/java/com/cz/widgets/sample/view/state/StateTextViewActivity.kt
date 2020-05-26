package com.cz.widgets.sample.view.state

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widgets.sample.R
import kotlinx.android.synthetic.main.activity_view_state_text_view.*

/**
 * @author :Created by cz
 * @date 2019-06-13 17:01
 * @email bingo110@126.com
 * 演示不同State状态变化
 */
@SampleSourceCode(".*StateTextViewActivity.kt")
@RefRegister(title=R.string.view_state_text_view,desc=R.string.view_state_text_view_desc,category = R.string.view)
class StateTextViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_state_text_view)

        radioLayout.setOnCheckedChangeListener { _, index, selected ->
            if(selected){
                stateTextView1.setStateEnabled(index + 1, true)
                stateTextView2.setStateEnabled(index + 1, true)
                stateTextView3.setStateEnabled(index + 1, true)
                stateTextView4.setStateEnabled(index + 1, true)
            }
        }
        stateTextView1.setOnClickListener {
            Toast.makeText(applicationContext,"Click",Toast.LENGTH_SHORT).show()
        }

        stateTextView2.isSelected = true
        stateTextView3.isSelected = true
        stateTextView4.isSelected = false
    }
}
