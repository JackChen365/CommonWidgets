package com.cz.widgets.sample.view.frame

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widgets.sample.R
import kotlinx.android.synthetic.main.activity_view_frame_view_sample.*

@SampleSourceCode("(.*FrameViewSampleActivity.kt)|FrameWrapper")
@RefRegister(title=R.string.view_frame_view1,desc=R.string.view_frame_view_desc1,category = R.string.view_frame)
class FrameViewSampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_frame_view_sample)
        val viewFrameWrapper = FrameWrapper(frameView,R.style.FrameSmallStyle)
        radioLayout.setOnCheckedChangeListener { _, index, selected ->
            if(selected){
                when(index){
                    0->{
                        viewFrameWrapper.setFrame(FrameWrapper.FRAME_CONTAINER)
                    }
                    1->{
                        viewFrameWrapper.setFrame(FrameWrapper.FRAME_PROGRESS)
                    }
                    2->{
                        viewFrameWrapper.setFrame(R.id.customEmptyLayout)
                    }
                    3->{
                        viewFrameWrapper.setFrame(R.id.customErrorLayout)
                    }
                }
            }
        }
    }
}
