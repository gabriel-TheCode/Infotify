package com.thecode.infotify.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.thecode.infotify.InfotifyApp

class NetworkChangeReceiver : BroadcastReceiver() {
    var isConnected = false
    override fun onReceive(context: Context, arg1: Intent) {
        isConnected =
            NetworkUtil.getConnectivityStatus(InfotifyApp.instance!!.applicationContext) > 0
        if (connectivityReceiverListener != null) {
            connectivityReceiverListener!!.onNetworkConnectionChanged(
                isConnected
            )
        }
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
        fun onInternetUnavailable()
    }

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? =
            null

        fun isConnected(): Boolean {
            return NetworkUtil.getConnectivityStatus(
                InfotifyApp.instance!!.appContext.applicationContext
            ) > 0
        }

        fun connectionNotAvaillable() {
            connectivityReceiverListener!!.onInternetUnavailable()
        }
    }
}