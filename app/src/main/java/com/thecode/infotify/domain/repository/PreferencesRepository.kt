package com.thecode.infotify.domain.repository

import com.thecode.infotify.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {

    fun themeMode(): Flow<ThemeMode>

    suspend fun setThemeMode(mode: ThemeMode)

    /** ISO 639-1 code used for every news request. */
    fun languageCode(): Flow<String>

    suspend fun setLanguageCode(code: String)

    fun isOnboardingCompleted(): Flow<Boolean>

    suspend fun setOnboardingCompleted()
}
