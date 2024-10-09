package com.thecode.infotify.data.local.datasource

import kotlinx.coroutines.flow.Flow

interface InfotifyLocalDataSource {

    fun isOnboardingCompleted(): Flow<Boolean>

    suspend fun setOnboardingCompleted()

    fun isNightModeEnabled(): Flow<Boolean>

    suspend fun setNightModeEnabled(state: Boolean)

    fun getUserLanguagePreference(): Flow<String>

    suspend fun setUserLanguagePreference(lang: String)

    suspend fun clearAppData()
}
