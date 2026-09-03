package com.thecode.infotify.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.thecode.infotify.domain.model.Interests
import com.thecode.infotify.domain.model.Region
import com.thecode.infotify.domain.model.ThemeMode
import com.thecode.infotify.domain.model.Topic
import com.thecode.infotify.utils.AppConstants.DEFAULT_LANGUAGE
import com.thecode.infotify.utils.AppConstants.PREFERENCE_NAME
import com.thecode.infotify.utils.extensions.getValueFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalTime
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

    /**
     * Topics and region are stored separately but surface as one [Interests] value, so no
     * caller can observe a half-updated selection.
     *
     * Unknown stored names are dropped rather than failing: a subject removed from the
     * provider must not brick a user's saved preferences.
     */
    fun interests(): Flow<Interests> = combine(
        dataStore.getValueFlow(TOPICS, emptySet()),
        dataStore.getValueFlow(REGION, "")
    ) { topicNames, regionName ->
        Interests(
            topics = topicNames.mapNotNull { name ->
                Topic.entries.firstOrNull { it.name == name }
            }.toSet(),
            region = Region.entries.firstOrNull { it.name == regionName }
        )
    }

    suspend fun setInterests(interests: Interests) {
        dataStore.edit { preferences ->
            preferences[TOPICS] = interests.topics.map { it.name }.toSet()
            preferences[REGION] = interests.region?.name.orEmpty()
        }
    }

    fun dailyBriefingEnabled(): Flow<Boolean> =
        dataStore.getValueFlow(DAILY_BRIEFING, false)

    suspend fun setDailyBriefingEnabled(enabled: Boolean) {
        dataStore.edit { it[DAILY_BRIEFING] = enabled }
    }

    /** Stored as minutes since midnight: no timezone to get wrong, no format to parse. */
    fun briefingTime(): Flow<LocalTime> =
        dataStore.getValueFlow(BRIEFING_MINUTE, DEFAULT_BRIEFING_MINUTE)
            .map { LocalTime.of(it / 60, it % 60) }

    suspend fun setBriefingTime(time: LocalTime) {
        dataStore.edit { it[BRIEFING_MINUTE] = time.hour * 60 + time.minute }
    }

    fun isOnboardingCompleted(): Flow<Boolean> =
        dataStore.getValueFlow(ONBOARDING_COMPLETED, false)

    suspend fun setOnboardingCompleted() {
        dataStore.edit { it[ONBOARDING_COMPLETED] = true }
    }

    private companion object {
        val THEME_MODE = stringPreferencesKey("THEME_MODE")
        val LANGUAGE = stringPreferencesKey("USER_LANGUAGE")
        val TOPICS = stringSetPreferencesKey("INTEREST_TOPICS")
        val REGION = stringPreferencesKey("INTEREST_REGION")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("IS_ONBOARDING_COMPLETED")
        val DAILY_BRIEFING = booleanPreferencesKey("DAILY_BRIEFING")
        val BRIEFING_MINUTE = intPreferencesKey("BRIEFING_MINUTE")

        /** 07:30 local. A fixed, explainable default beats guessing at a "smart" hour. */
        const val DEFAULT_BRIEFING_MINUTE = 7 * 60 + 30
    }
}
