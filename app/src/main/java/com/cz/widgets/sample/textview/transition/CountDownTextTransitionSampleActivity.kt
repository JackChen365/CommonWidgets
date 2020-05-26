package com.cz.widgets.sample.textview.transition

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widgets.sample.R
import kotlinx.android.synthetic.main.activity_text_transition_sample.*
import java.text.DecimalFormat

@SampleSourceCode(".*CountDownText.*")
@RefRegister(title=R.string.text_transition_title,desc=R.string.text_transition_desc,category = R.string.text_transition)
class CountDownTextTransitionSampleActivity : AppCompatActivity() {
    companion object{
        private const val MINUTES = 60 * 1000
        private const val HOUR = 60 * MINUTES
        private const val DAY = 24 * HOUR
    }

    private val decimalFormatter: DecimalFormat= DecimalFormat("00")
    private var countDownTimer:CountDownTimer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_transition_sample)
        var index=0
        val timeMillis=24*60*60*1000L
        textView.textTransition = CountDownTextTransform()
        textView.setOnClickListener {
            val millisUntilFinished=timeMillis-index++
            setTextMillis(millisUntilFinished)
        }
        setTextMillis(timeMillis)
        startCountDown()
    }

    private fun startCountDown() {
        val timeMillis=24*60*60*1000L
        countDownTimer = object : CountDownTimer(timeMillis, (1 * 1000).toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                setTextMillis(millisUntilFinished)
            }

            override fun onFinish() {
            }
        }
        countDownTimer?.start()
    }

    private fun setTextMillis(millisUntilFinished: Long) {
        val hour = (millisUntilFinished / HOUR).toInt()
        val minute = ((millisUntilFinished - hour * HOUR) / MINUTES).toInt()
        val second = (millisUntilFinished/1000 % 60).toInt()
        val hourText = decimalFormatter.format(hour.toLong())
        val minuteText = decimalFormatter.format(minute.toLong())
        val secondText = decimalFormatter.format(second.toLong())
        textView.text = "$hourText:$minuteText:$secondText"
    }
}
