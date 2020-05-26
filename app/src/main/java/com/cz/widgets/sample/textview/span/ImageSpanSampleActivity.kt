package com.cz.widgets.sample.textview.span

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widgets.sample.R
import com.cz.widgets.textview.span.click.TouchableMovementMethod
import com.cz.widgets.textview.span.image.TextImageSpan
import kotlinx.android.synthetic.main.activity_text_image_span_sample.*
import java.util.*

@SampleSourceCode(".*ImageSpanSampleActivity.kt")
@RefRegister(title=R.string.text_image_span_title,desc=R.string.text_image_span_desc,category = R.string.text_span)
class ImageSpanSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_image_span_sample)
        initTextViewSpan(text1, TextImageSpan.ALIGN_CENTER)
        val alignArray= intArrayOf(
            TextImageSpan.ALIGN_TOP,
            TextImageSpan.ALIGN_CENTER,
            TextImageSpan.ALIGN_BASELINE,
            TextImageSpan.ALIGN_BOTTOM)
        radioLayout.setOnCheckedChangeListener { _, position, selected ->
            if(selected){
                initTextViewSpan(text1,alignArray[position])
            }
        }
    }

    private fun initTextViewSpan(textView: TextView, align:Int) {
        val imageSpanItems= LinkedList<TextImageSpan>()
        val imageArray = intArrayOf(
            R.drawable.text_round_primary_rect_selector,
            R.mipmap.text_image1, R.mipmap.text_image2,
            R.mipmap.text_image3, R.mipmap.text_image4,
            R.mipmap.text_image5, R.mipmap.text_image6,
            R.mipmap.text_image7, R.mipmap.text_image8,R.mipmap.text_image9
        )
        imageArray.forEachIndexed { index, res ->
            val textImageSpan =
                TextImageSpan.Builder(this).
                    drawable(res).
                    alignment(align).
                    textSize(20f).
                    drawableSize(80, 80).
                    click {
                        Toast.makeText(applicationContext, "click:$index!", Toast.LENGTH_SHORT).show()
                    }.build()
            if(0==index){
                textImageSpan.setText("Text$index")
                textImageSpan.setTextColor(Color.WHITE)
            }
            imageSpanItems.add(textImageSpan)
        }
        var start = 0
        val spannableString = SpannableString(getString(R.string.text_sentence1))
        while (!imageSpanItems.isEmpty()) {
            val index = spannableString.indexOf(" ", start)
            if(0 > index) break
            val imageSpan = imageSpanItems.pollFirst()
            if (null != imageSpan) {
                spannableString.setSpan(imageSpan, index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            start = index + 1
        }
        textView.movementMethod= TouchableMovementMethod.getInstance()
        textView.text = spannableString
    }
}

