package com.cz.widgets.sample.textview.other

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widgets.sample.R
import kotlinx.android.synthetic.main.activity_text_count_down.*

@SampleSourceCode(".*CountDown.*")
@RefRegister(title = R.string.text_countdown_text_title, desc = R.string.text_countdown_text_desc,category=R.string.text_other)
class CountDownTextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_count_down)

        countDownTextView.start()
    }
}
