package com.cz.widgets.sample.textview.aniamtion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widgets.sample.R
import com.cz.widgets.sample.textview.aniamtion.controller.*
import kotlinx.android.synthetic.main.activity_text_animation_sample.*

@SampleSourceCode(".*TextAnimationSampleActivity.kt")
@RefRegister(title=R.string.text_animation_title,desc=R.string.text_animation_desc,category=R.string.text_animator)
class TextAnimationSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_animation_sample)
        textView.setOnClickListener { textView.startAnimator() }
        textView.textController= AlphaTextController()
        animLayout.setOnCheckedChangeListener { _, index, selected ->
            if(selected){
                when(index){
                    0->textView.textController=
                        AlphaTextController()
                    1->textView.textController=
                        TransitionXTextController()
                    2->textView.textController=
                        TransitionYTextController()
                    3->textView.textController= ScaleXTextController()
                    4->textView.textController= ScaleYTextController()
                    5->textView.textController=RotationTextController()
                    6->textView.textController=XTextController()
                    7->textView.textController=YTextController()
                }
                textView.prepareAnimator()
                textView.startAnimator()
            }
        }
        startButton.setOnClickListener {
            textView.prepareAnimator()
            textView.startAnimator()
        }
        cleanButton.setOnClickListener {
            textView.cancelAnimator()
        }

        textView.post {
            textView.startAnimator()
        }
    }
}
