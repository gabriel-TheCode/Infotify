package com.thecode.infotify.core.local

interface InfotifyLocalDataSource {

    suspend fun isOnboardingCompleted(): Boolean

    suspend fun setOnboardingCompleted(state: Boolean)

    fun isNightModeEnabled(): Boolean

    fun setIsNightModeEnabled(state: Boolean)


}