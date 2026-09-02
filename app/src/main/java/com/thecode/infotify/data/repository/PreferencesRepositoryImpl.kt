package com.thecode.infotify.data.repository

import com.thecode.infotify.data.local.InfotifyDataStore
import com.thecode.infotify.domain.model.ThemeMode
import com.thecode.infotify.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: InfotifyDataStore
) : PreferencesRepository {

    override fun themeMode(): Flow<ThemeMode> = dataStore.themeMode()

    override suspend fun setThemeMode(mode: ThemeMode) = dataStore.setThemeMode(mode)

    override fun languageCode(): Flow<String> = dataStore.languageCode()

    override suspend fun setLanguageCode(code: String) = dataStore.setLanguageCode(code)

    override fun isOnboardingCompleted(): Flow<Boolean> = dataStore.isOnboardingCompleted()

    override suspend fun setOnboardingCompleted() = dataStore.setOnboardingCompleted()
}
