package com.cz.widgets.sample.view.frame.trigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.recyclerview.widget.RecyclerView
import com.cz.widgets.common.frame.AbsFrameWrapper
import com.cz.widgets.common.frame.triiger.FrameTrigger
import com.cz.widgets.sample.view.frame.FrameWrapper

/**
 * The network status change trigger.
 * @see ConnectivityManager
 */
class NetworkFrameTrigger(private val context: Context, private val recyclerView: RecyclerView): FrameTrigger<NetworkInfo>() {

    companion object {
        const val MOBILE_INFO = "MOBILE"
        const val WIFI_INFO = "WIFI"
    }
    private val netWorkReceiver= NetworkStatusReceiver(this)

    init {
        context.registerReceiver(netWorkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }


    override fun trigger(target: AbsFrameWrapper, networkInfo: NetworkInfo?) {
        if(null==networkInfo){
            target.setFrame(FrameWrapper.FRAME_ERROR)
        } else if (networkInfo.isAvailable) {
            if (MOBILE_INFO == networkInfo.typeName|| WIFI_INFO == networkInfo.typeName) {
                target.setFrame(FrameWrapper.FRAME_PROGRESS)
                call()
            }
        }
    }

    override fun isActive(): Boolean {
        return null==recyclerView.adapter
    }

    override fun onDetached() {
        super.onDetached()
        context.unregisterReceiver(netWorkReceiver)
    }

    class NetworkStatusReceiver(private val trigger: NetworkFrameTrigger) : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == ConnectivityManager.CONNECTIVITY_ACTION) {
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                trigger.trigger(connectivityManager.activeNetworkInfo)
            }
        }
    }
}