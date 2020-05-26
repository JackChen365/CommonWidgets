package com.cz.widgets.sample.view.form

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widgets.common.form.validator.ValidatorObservable
import com.cz.widgets.sample.R
import kotlinx.android.synthetic.main.activity_view_form.*

/**
 * @author Created by cz
 * @date 2020-05-24 21:26
 * @email bingo110@126.com
 *
 */
@SampleSourceCode
@RefRegister(title=R.string.view_form,desc=R.string.view_form_desc,category = R.string.view)
class FormSampleActivity : AppCompatActivity() {

    private var countDownTimer:CountDownTimer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_form)
        val subscribe = ValidatorObservable.create(editLayout1,editLayout2,editLayout3)
                .subscribe { _, changed -> messageText.append("ValidatorObserver:$changed\n") }
        clearButton.setOnClickListener {
            messageText.text = null
        }
        countDownTimer = object : CountDownTimer(60 * 1000, 1 * 1000) {
            override fun onTick(millisUntilFinished: Long) {
                smsCodeText.text = getString(R.string.view_sms_count_down,millisUntilFinished/1000)
            }
            override fun onFinish() {
                smsCodeText.isEnabled=true;
            }
        }
        //If you want to switch the password visibility.
        passwordTransition.setOnClickListener {
            editLayout3.toggleEditPasswordTransformation()
        }
        //Send the identified code.
        smsCodeText.setOnClickListener {
            smsCodeText.isEnabled=false
            countDownTimer?.start()
        }
        //Verify the form manually
        checkButton.setOnClickListener {
            if (!subscribe.isValid()) {
                messageText.append(subscribe.getErrorMessage() + "\n")
            } else {
                messageText.append("The form is available!\n")
            }
        }
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }
}
