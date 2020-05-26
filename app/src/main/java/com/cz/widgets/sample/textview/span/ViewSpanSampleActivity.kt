package com.cz.widgets.sample.textview.span

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widgets.sample.R
import com.cz.widgets.sample.data.Data
import com.cz.widgets.textview.span.view.ClickableViewSpan
import kotlinx.android.synthetic.main.activity_text_view_span_sample.*
import java.util.*
import kotlin.concurrent.thread

@SampleSourceCode(".*ViewSpanSampleActivity.kt")
@RefRegister(title=R.string.text_view_span_title,desc=R.string.text_view_span_desc,category = R.string.text_span)
class ViewSpanSampleActivity : AppCompatActivity() {

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_view_span_sample)
        val layoutInflater = LayoutInflater.from(this)
        val viewLayout = layoutInflater.inflate(R.layout.text_view_span_layout, null) as ViewGroup
        initTextView(viewLayout)
        button.setOnClickListener {
            val textView2=viewLayout.getChildAt(2) as TextView
            textView2.text=textView2.text.toString()+"2"
            val measuredWidth = textView2.measuredWidth
            println(measuredWidth)
        }
    }

    private fun requestImage(imageView: ImageView) {
        Glide.get(this).clearMemory()
        thread { Glide.get(this).clearDiskCache() }
        val image = Data.getImage()
        Glide.with(this).load(image).into(imageView)
    }

    private fun initTextView(viewLayout: ViewGroup) {
        val viewSpans = LinkedList<ClickableViewSpan>()
        var i=0
        while(0<viewLayout.childCount){
            val view=viewLayout.getChildAt(0)
            viewLayout.removeView(view)
            val index=i
            view.setOnClickListener {
                Toast.makeText(applicationContext, "click:$index", Toast.LENGTH_SHORT).show()
            }
            val viewSpan = ClickableViewSpan(textView, view)
            view.setOnClickListener {
                Toast.makeText(applicationContext, "span click:$index", Toast.LENGTH_SHORT).show()
            }
            if(view is ImageView){
                requestImage(view)
            }
            viewSpans.add(viewSpan)
            i++
        }
        var start = 0
        val spannableString = SpannableStringBuilder(getString(R.string.text_sentence1))
        while (!viewSpans.isEmpty()) {
            val index = spannableString.indexOf(",", start)
            if (0 > index) break
            val imageSpan = viewSpans.pollFirst()
            if (null != imageSpan) {
                spannableString.setSpan(imageSpan, index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            start = index + 1
        }
        textView.post {
            textView.text = spannableString
        }
    }

}
