package com.cz.widgets.sample.view.state

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.widgets.sample.R
import kotlinx.android.synthetic.main.activity_state_mark_layout.*
import kotlinx.android.synthetic.main.activity_view_state_text_view.*
import kotlin.math.pow

@RefRegister(title=R.string.view_state_mark_view,desc=R.string.view_state_mark_view_desc,category = R.string.view)
class StateMarkLayoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_state_mark_layout)
        checkedLayout.setOnCheckedChangeListener { _, index, isChecked ->
            if(isChecked){
                val flag = Math.pow(2.0,index.toDouble()).toInt();
                stateMarkLayout.setStateEnabled(flag, true)
            }
        }
    }
}