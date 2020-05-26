package com.cz.widgets.sample.textview.aniamtion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.textview.sample.ui.aniamtion.animator.TextLoad2Controller
import com.cz.textview.sample.ui.aniamtion.animator.WindowsTextLoadController
import com.cz.widgets.sample.R
import com.cz.widgets.sample.textview.aniamtion.animator.BallLoadTextController
import com.cz.widgets.sample.textview.aniamtion.animator.TextLoad1Controller
import kotlinx.android.synthetic.main.activity_text_animation_text_controller_sample.*

@SampleSourceCode(".*Controller.*")
@RefRegister(title= R.string.text_controller_animation_title,desc= R.string.text_controller_animation_desc,category = R.string.text_animator)
class AnimationTextControllerSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_animation_text_controller_sample)

        textView1.textController = BallLoadTextController()
        textView2.textController = WindowsTextLoadController()
        textView3.textController = TextLoad1Controller()
        textView4.textController = TextLoad2Controller()

        textView1.setOnClickListener { textView1.startAnimator() }
        textView2.setOnClickListener { textView2.startAnimator() }
        textView3.setOnClickListener { textView3.startAnimator() }
        textView4.setOnClickListener { textView4.startAnimator() }

        textView1.startAnimator()
        textView2.startAnimator()
        textView3.startAnimator()
        textView4.startAnimator()

        startButton.setOnClickListener {
            textView1.startAnimator()
            textView2.startAnimator()
            textView3.startAnimator()
            textView4.startAnimator()
        }
        stopButton.setOnClickListener {
            textView1.cancelAnimator()
            textView2.cancelAnimator()
            textView3.cancelAnimator()
            textView4.cancelAnimator()
        }
    }
}
