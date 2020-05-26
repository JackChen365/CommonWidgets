package com.cz.widgets.sample.view.frame

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widgets.sample.R
import com.cz.widgets.sample.view.frame.transition.ContentFrameTransition
import com.cz.widgets.sample.view.frame.transition.FrameTranslationX
import kotlinx.android.synthetic.main.activity_view_frame_view.*

/**
 * @author :Created by cz
 * @date 2020-05-24 10:34
 * @email bingo110@126.com
 */
@SampleSourceCode("(.*FrameViewActivity.kt)|(.*FrameWrapper.*)")
@RefRegister(title=R.string.view_frame_view,desc=R.string.view_frame_view_desc,category = R.string.view_frame)
class FrameViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_frame_view)

        val viewFrameWrapper1 = FrameWrapper(frameView1,R.style.FrameSmallStyle)
        val viewFrameWrapper2 = FrameWrapper(frameView2,R.style.FrameFullScreenStyle)

        viewFrameWrapper1.setFrameTransition(ContentFrameTransition())
        viewFrameWrapper2.setFrameTransition(FrameTranslationX())

        val emptyImage1=viewFrameWrapper1.findFrameView(FrameWrapper.FRAME_EMPTY,R.id.emptyImage)
        emptyImage1?.setOnClickListener {
            Toast.makeText(applicationContext,"点击图片1",Toast.LENGTH_SHORT).show()
        }

        testButton.setOnClickListener {
            Toast.makeText(applicationContext,"点击按钮1",Toast.LENGTH_SHORT).show()
        }

        val emptyImage2=viewFrameWrapper2.findFrameView(FrameWrapper.FRAME_EMPTY,R.id.emptyLayout)
        emptyImage2?.setOnClickListener {
            Toast.makeText(applicationContext,"点击~",Toast.LENGTH_SHORT).show()
        }
        for(i in 0 until frameView2.childCount){
            val childView=frameView2.getChildAt(i)
            childView.setOnClickListener {
                Toast.makeText(applicationContext,"点击内容:$i~",Toast.LENGTH_SHORT).show()
            }
        }

        changeButton.setOnClickListener {
            frameTextView.setText(R.string.text)
            while(3<frameView2.childCount){
                frameView2.removeViewAt(frameView2.childCount-1)
            }
        }

        radioLayout.setOnCheckedChangeListener { _, index, selected ->
            if(selected){
                when(index){
                    0->{
                        viewFrameWrapper1.setFrame(FrameWrapper.FRAME_CONTAINER)
                        viewFrameWrapper2.setFrame(FrameWrapper.FRAME_CONTAINER)
                    }
                    1->{
                        viewFrameWrapper1.setFrame(FrameWrapper.FRAME_PROGRESS)
                        viewFrameWrapper2.setFrame(FrameWrapper.FRAME_PROGRESS)
                    }
                    2->{
                        viewFrameWrapper1.setFrame(FrameWrapper.FRAME_EMPTY)
                        viewFrameWrapper2.setFrame(FrameWrapper.FRAME_EMPTY)
                    }
                    3->{
                        viewFrameWrapper1.setFrame(FrameWrapper.FRAME_ERROR)
                        viewFrameWrapper2.setFrame(FrameWrapper.FRAME_ERROR)
                    }
                }
            }
        }
    }
}
