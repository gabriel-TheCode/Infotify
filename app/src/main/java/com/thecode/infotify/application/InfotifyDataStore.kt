package com.thecode.infotify.application

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.thecode.infotify.utils.AppConstants.PREFERENCE_NAME
import com.thecode.infotify.utils.extensions.getValueFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(PREFERENCE_NAME)

class InfotifyDataStore @Inject constructor(@ApplicationContext context: Context) {

    companion object {
        private const val NIGHT_MODE = "NIGHT_MODE"
        private const val IS_ONBOARDING_COMPLETED = "IS_ONBOARDING_COMPLETED"
        private const val USER_LANGUAGE = "USER_LANGUAGE"
    }

    private val dataStore = context.dataStore

    suspend fun setNightModeEnabled(state: Boolean) {
        val dataStoreKey = booleanPreferencesKey(NIGHT_MODE)
        dataStore.edit { preferences ->
            preferences[dataStoreKey] = state
        }
    }

    suspend fun setUserLanguagePreference(lang: String) {
        val dataStoreKey = stringPreferencesKey(USER_LANGUAGE)
        dataStore.edit { preferences ->
            preferences[dataStoreKey] = lang
        }
    }

    fun isNightModeEnabled(): Flow<Boolean> {
        val dataStoreKey = booleanPreferencesKey(NIGHT_MODE)
        return dataStore.getValueFlow(dataStoreKey, false)
    }

    suspend fun setOnboardingCompleted() {
        val dataStoreKey = booleanPreferencesKey(IS_ONBOARDING_COMPLETED)
        dataStore.edit { preferences ->
            preferences[dataStoreKey] = true
        }
    }

    fun isOnboardingCompleted(): Flow<Boolean> {
        val dataStoreKey = booleanPreferencesKey(IS_ONBOARDING_COMPLETED)
        return dataStore.getValueFlow(dataStoreKey, false)
    }

    fun getUserLanguagePreference(): Flow<String> {
        val dataStoreKey = stringPreferencesKey(USER_LANGUAGE)
        return dataStore.getValueFlow(dataStoreKey, "en")
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
