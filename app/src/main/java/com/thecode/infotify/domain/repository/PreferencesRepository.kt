package com.thecode.infotify.domain.repository

import com.thecode.infotify.domain.model.Interests
import com.thecode.infotify.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

interface PreferencesRepository {

    fun themeMode(): Flow<ThemeMode>

    suspend fun setThemeMode(mode: ThemeMode)

    /** ISO 639-1 code used for every news request. */
    fun languageCode(): Flow<String>

    suspend fun setLanguageCode(code: String)

    fun interests(): Flow<Interests>

    suspend fun setInterests(interests: Interests)

    fun dailyBriefingEnabled(): Flow<Boolean>

    suspend fun setDailyBriefingEnabled(enabled: Boolean)

    /** Local time at which the briefing is delivered. Chosen by the user, default 07:30. */
    fun briefingTime(): Flow<LocalTime>

    suspend fun setBriefingTime(time: LocalTime)

    fun isOnboardingCompleted(): Flow<Boolean>

    suspend fun setOnboardingCompleted()
}
