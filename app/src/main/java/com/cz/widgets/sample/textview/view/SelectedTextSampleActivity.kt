package com.cz.widgets.sample.textview.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widgets.sample.R
import kotlinx.android.synthetic.main.activity_text_selected_sample.*

@SampleSourceCode(".*SelectedTextSampleActivity.kt")
@RefRegister(title=R.string.text_marked_title,desc=R.string.text_marked_desc,category = R.string.text_widget)
class SelectedTextSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_selected_sample)

        textView.text="The narrator, a pilot, discusses his childhood attempts at drawing a boa constrictor eating an elephant. First, he draws the image from the outside, and all the grownups believe it's a hat—so the narrator attempts to drawText the boa constrictor from the inside, and this time the grownups advise him to quit drawing boa constrictors and devote his time to other subjects like geography, arithmetic, grammar, or history instead.\n" +
                "The narrator chooses another profession instead, becoming a pilot. As a pilot, he claims that he has spent a great deal of time among grownups. When he meets one who seems removeAllViews-sighted, he says, he shows them his childhood drawing of the boa constrictor from the outside, but the grownups always say that the drawing is of a hat. As a result, the pilot brings himself down to their level, talking of sensible matters instead of stars, boa constrictors, or primeval forests."

        textView.post { textView.setMarkedText(0,3) }
    }
}
