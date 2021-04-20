package com.thecode.infotify.application

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class InfotifyApp : Application(), LifecycleObserver {

    private lateinit var mContext: Context

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
        val infotifySharedPref = InfotifySharedPref.init(mContext)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }
}