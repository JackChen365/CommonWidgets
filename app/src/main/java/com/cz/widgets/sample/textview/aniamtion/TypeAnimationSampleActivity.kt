package com.cz.widgets.sample.textview.aniamtion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widgets.sample.R
import com.cz.widgets.sample.textview.aniamtion.animator.TypingTextController
import kotlinx.android.synthetic.main.activity_text_type_animation_sample.*

@SampleSourceCode(".*Typing.*")
@RefRegister(title=R.string.text_type_text_animation_title,desc=R.string.text_type_text_animation_desc,category=R.string.text_animator)
class TypeAnimationSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_type_animation_sample)

        animationTextView.textController = TypingTextController(this)
        animationTextView.startAnimator()

        startButton.setOnClickListener {
            animationTextView.startAnimator()
        }

        stopButton.setOnClickListener {
            animationTextView.cancelAnimator()
        }
    }
}
