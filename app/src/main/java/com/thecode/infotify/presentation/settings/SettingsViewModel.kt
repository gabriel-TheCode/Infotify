package com.thecode.infotify.presentation.settings

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.domain.model.Interests
import com.thecode.infotify.domain.model.Language
import com.thecode.infotify.domain.model.ThemeMode
import com.thecode.infotify.domain.usecase.ClearHttpCache
import com.thecode.infotify.domain.usecase.HttpCacheSize
import com.thecode.infotify.domain.usecase.ObserveBriefingTime
import com.thecode.infotify.domain.usecase.ObserveDailyBriefing
import com.thecode.infotify.domain.usecase.ObserveInterests
import com.thecode.infotify.domain.usecase.ObserveLanguage
import com.thecode.infotify.domain.usecase.ObserveThemeMode
import com.thecode.infotify.domain.usecase.SetBriefingTime
import com.thecode.infotify.domain.usecase.SetDailyBriefing
import com.thecode.infotify.domain.usecase.SetLanguage
import com.thecode.infotify.domain.usecase.SetThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@Immutable
data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.Default,
    val language: Language = Language.Default,
    val interests: Interests = Interests.None,
    val dailyBriefingEnabled: Boolean = false,
    val briefingTime: LocalTime = LocalTime.of(7, 30),
    val cacheSize: String = ""
) {
    /** "07:30" — 24-hour, which is unambiguous in every locale the app ships in. */
    val briefingTimeLabel: String
        get() = briefingTime.format(DateTimeFormatter.ofPattern("HH:mm"))
}

/**
 * No Intent/Effect triptych: a preferences form has no state machine, and inventing one
 * would be boilerplate for its own sake.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeThemeMode: ObserveThemeMode,
    observeLanguage: ObserveLanguage,
    observeInterests: ObserveInterests,
    observeDailyBriefing: ObserveDailyBriefing,
    observeBriefingTime: ObserveBriefingTime,
    private val setThemeMode: SetThemeMode,
    private val setLanguage: SetLanguage,
    private val setDailyBriefing: SetDailyBriefing,
    private val setBriefingTime: SetBriefingTime,
    private val httpCacheSize: HttpCacheSize,
    private val clearHttpCache: ClearHttpCache
) : ViewModel() {

    private val cacheSize = MutableStateFlow(httpCacheSize())

    val uiState = combine(
        observeThemeMode(),
        observeLanguage(),
        observeInterests(),
        observeDailyBriefing(),
        observeBriefingTime(),
        cacheSize
    ) { values ->
        SettingsUiState(
            themeMode = values[0] as ThemeMode,
            language = Language.fromCode(values[1] as String),
            interests = values[2] as Interests,
            dailyBriefingEnabled = values[3] as Boolean,
            briefingTime = values[4] as LocalTime,
            cacheSize = values[5] as String
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    fun onThemeModeSelected(mode: ThemeMode) {
        viewModelScope.launch { setThemeMode(mode) }
    }

    fun onLanguageSelected(language: Language) {
        viewModelScope.launch { setLanguage(language.code) }
    }

    /** Persists the choice and schedules — or cancels — the work that delivers it. */
    fun onDailyBriefingChanged(enabled: Boolean) {
        viewModelScope.launch { setDailyBriefing(enabled) }
    }

    /** Changing the hour reschedules the work; the two can never drift apart. */
    fun onBriefingTimeSelected(time: LocalTime) {
        viewModelScope.launch { setBriefingTime(time) }
    }

    fun onClearCache() {
        viewModelScope.launch {
            clearHttpCache()
            cacheSize.value = httpCacheSize()
        }
    }
}
