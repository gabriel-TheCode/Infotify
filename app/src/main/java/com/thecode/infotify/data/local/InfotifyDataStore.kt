package com.thecode.infotify.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.thecode.infotify.domain.model.ThemeMode
import com.thecode.infotify.utils.AppConstants.DEFAULT_LANGUAGE
import com.thecode.infotify.utils.AppConstants.PREFERENCE_NAME
import com.thecode.infotify.utils.extensions.getValueFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(PREFERENCE_NAME)

@Singleton
class InfotifyDataStore @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    fun themeMode(): Flow<ThemeMode> =
        dataStore.getValueFlow(THEME_MODE, ThemeMode.Default.name).map(ThemeMode::fromName)

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[THEME_MODE] = mode.name }
    }

    fun languageCode(): Flow<String> = dataStore.getValueFlow(LANGUAGE, DEFAULT_LANGUAGE)

    suspend fun setLanguageCode(code: String) {
        dataStore.edit { it[LANGUAGE] = code }
    }

    fun isOnboardingCompleted(): Flow<Boolean> =
        dataStore.getValueFlow(ONBOARDING_COMPLETED, false)

    suspend fun setOnboardingCompleted() {
        dataStore.edit { it[ONBOARDING_COMPLETED] = true }
    }

    private companion object {
        val THEME_MODE = stringPreferencesKey("THEME_MODE")
        val LANGUAGE = stringPreferencesKey("USER_LANGUAGE")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("IS_ONBOARDING_COMPLETED")
    }
}
