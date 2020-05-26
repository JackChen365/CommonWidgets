package com.cz.widgets.sample.view.frame.trigger

import androidx.recyclerview.widget.RecyclerView
import com.cz.widgets.common.frame.AbsFrameWrapper
import com.cz.widgets.common.frame.triiger.FrameTrigger
import com.cz.widgets.sample.view.frame.FrameWrapper

class RecyclerViewFrameTrigger(val adapter: RecyclerView.Adapter<*>): FrameTrigger<Int>() {

    private var observer= InnerAdapterDataObserver(adapter,this)
    init {
        adapter.registerAdapterDataObserver(observer)
    }

    override fun trigger(target: AbsFrameWrapper, count: Int?) {
        if(0 == count){
            target.setFrame(FrameWrapper.FRAME_EMPTY)
        } else {
            target.setFrame(FrameWrapper.FRAME_CONTAINER)
        }
    }

    override fun onDetached() {
        super.onDetached()
        adapter.unregisterAdapterDataObserver(observer)
    }

    class InnerAdapterDataObserver(val adapter:RecyclerView.Adapter<*>, private val trigger: FrameTrigger<Int>):RecyclerView.AdapterDataObserver(){
        override fun onChanged() {
            super.onChanged()
            dataChanged()
        }

        private fun dataChanged() {
            trigger.trigger(adapter.itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            dataChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            dataChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            dataChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            super.onItemRangeChanged(positionStart, itemCount, payload)
            dataChanged()
        }

    }

}