package com.cz.widgets.sample.view.frame

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.android.sample.library.data.DataManager
import com.cz.widgets.sample.R
import com.cz.widgets.sample.view.adapter.SimpleAdapter
import com.cz.widgets.sample.view.frame.trigger.NetworkFrameTrigger
import com.cz.widgets.sample.view.frame.trigger.RecyclerViewFrameTrigger
import kotlinx.android.synthetic.main.activity_view_frame_trigger.*

@SampleSourceCode(".*Trigger.*")
@RefRegister(title=R.string.view_frame_trigger,desc=R.string.view_frame_trigger_desc,category = R.string.view_frame)
class FrameTriggerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_frame_trigger)

        val frameWrapper = FrameWrapper(frameView,R.style.FrameSmallStyle)

        val dataProvider = DataManager.getDataProvider(this)

        if(isNetworkConnected(this)){
            recyclerView.layoutManager= LinearLayoutManager(this)
            val adapter = SimpleAdapter( dataProvider.wordList.toList())
            recyclerView.adapter=adapter
            frameWrapper.addFrameTrigger(RecyclerViewFrameTrigger(adapter))
        } else {
            val trigger= NetworkFrameTrigger(this,recyclerView)
            trigger.setCallback {
                recyclerView.layoutManager= LinearLayoutManager(this@FrameTriggerActivity)
                val adapter = SimpleAdapter(dataProvider.wordList.toList())
                recyclerView.adapter=adapter
                frameWrapper.setFrame(FrameWrapper.FRAME_CONTAINER)
                frameWrapper.addFrameTrigger(RecyclerViewFrameTrigger(adapter))
            }
            frameWrapper.addFrameTrigger(trigger)
        }

        clearButton.setOnClickListener {
            val adapter=recyclerView.adapter as? SimpleAdapter
            adapter?.clear()
        }

        addButton.setOnClickListener {
            val adapter=recyclerView.adapter as? SimpleAdapter
            adapter?.add("New Item")
        }

        val emptyImage1=frameWrapper.findFrameView(FrameWrapper.FRAME_EMPTY,R.id.emptyImage)
        emptyImage1?.setOnClickListener {
            Toast.makeText(applicationContext,"点击图片1",Toast.LENGTH_SHORT).show()
        }

        radioLayout.setOnCheckedChangeListener { _, index, selected ->
            if(selected){
                when(index){
                    0->{
                        frameWrapper.setFrame(FrameWrapper.FRAME_CONTAINER)
                    }
                    1->{
                        frameWrapper.setFrame(FrameWrapper.FRAME_PROGRESS)
                    }
                    2->{
                        frameWrapper.setFrame(FrameWrapper.FRAME_EMPTY)
                    }
                    3->{
                        frameWrapper.setFrame(FrameWrapper.FRAME_ERROR)
                    }
                }
            }
        }
    }

    private fun isNetworkConnected(context: Context): Boolean {
        if (context != null) {
            val mConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = mConnectivityManager.activeNetworkInfo
            return networkInfo?.isAvailable?:false
        }
        return false
    }
}
