package com.thecode.infotify.utils

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager

/**
 * Created by TEKOMBO Gabriel </tekombo.gabriel@gmail.com/>
 */

object NetworkUtil {
    private const val TYPE_WIFI = 1
    private const val TYPE_MOBILE = 2
    private const val TYPE_NOT_CONNECTED = 0
    fun getConnectivityStatus(context: Context): Int {
        val cm = (context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        val activeNetwork = cm.activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) return TYPE_WIFI
            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) return TYPE_MOBILE
        }
        return TYPE_NOT_CONNECTED
    }

    fun getConnectivityStatusString(context: Context): String? {
        val conn = getConnectivityStatus(context)
        var status: String? = null
        if (conn == TYPE_WIFI) {
            //status = "Wifi enabled";
            status = AppConstants.CONNECT_TO_WIFI
        } else if (conn == TYPE_MOBILE) {
            //status = "Mobile data enabled";
            System.out.println(AppConstants.CONNECT_TO_MOBILE)
            status = getNetworkClass(context)
        } else if (conn == TYPE_NOT_CONNECTED) {
            status = AppConstants.NOT_CONNECT
        }
        return status
    }

    private fun getNetworkClass(context: Context): String {
        val cm =
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        val info = cm.activeNetworkInfo
        if (info == null || !info.isConnected) return "-" //not connected
        if (info.type == ConnectivityManager.TYPE_WIFI) return "WIFI"
        if (info.type == ConnectivityManager.TYPE_MOBILE) {
            val networkType = info.subtype
            return when (networkType) {
                TelephonyManager.NETWORK_TYPE_HSPAP -> "3G"
                TelephonyManager.NETWORK_TYPE_LTE -> "4G"
                else -> "UNKNOWN"
            }
        }
        return "UNKNOWN"
    }
}