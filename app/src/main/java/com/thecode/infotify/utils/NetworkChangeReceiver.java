package com.thecode.infotify.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.thecode.infotify.InfotifyApp;


public class NetworkChangeReceiver extends BroadcastReceiver {
    public static ConnectivityReceiverListener connectivityReceiverListener;
    boolean isConnected = false;

    public NetworkChangeReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {

         isConnected = NetworkUtil.getConnectivityStatus(InfotifyApp.getInstance().getApplicationContext()) > 0;
        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }

    }

    public static boolean isConnected() {
        return NetworkUtil.getConnectivityStatus(InfotifyApp.getInstance().getAppContext().getApplicationContext()) > 0;
    }

    public static void connectionNotAvaillable() {

        connectivityReceiverListener.onInternetUnavailable();
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);

        void onInternetUnavailable();

    }
}