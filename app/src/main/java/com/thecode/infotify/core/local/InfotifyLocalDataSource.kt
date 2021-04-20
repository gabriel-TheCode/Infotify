package com.thecode.infotify.core.local

import com.thecode.infotify.application.InfotifySharedPref
import javax.inject.Inject

interface InfotifyLocalDataSource {

    fun isOnboardingCompleted(): Boolean

    fun setOnboardingCompleted()

    fun isNightModeEnabled(): Boolean

    fun setNightModeEnabled(state: Boolean)

    fun clearAppData()
}

class InfotifyLocalDataSourceImpl @Inject constructor(
    private val sharedPref: InfotifySharedPref
) : InfotifyLocalDataSource {
    override fun isOnboardingCompleted(): Boolean {
        return sharedPref.isOnboardingCompleted()
    }

    override fun setOnboardingCompleted() {
        sharedPref.setOnboardingCompleted()
    }

    override fun isNightModeEnabled(): Boolean {
        return sharedPref.isNightModeEnabled()
    }

    override fun setNightModeEnabled(state: Boolean) {
        sharedPref.setNightModeEnabled(state)
    }

    override fun clearAppData() {
        sharedPref.clearSession()
    }
}