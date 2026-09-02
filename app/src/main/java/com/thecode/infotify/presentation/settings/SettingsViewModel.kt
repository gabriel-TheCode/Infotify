package com.thecode.infotify.presentation.settings

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.domain.model.Language
import com.thecode.infotify.domain.model.ThemeMode
import com.thecode.infotify.domain.usecase.ObserveLanguage
import com.thecode.infotify.domain.usecase.ObserveThemeMode
import com.thecode.infotify.domain.usecase.SetLanguage
import com.thecode.infotify.domain.usecase.SetThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.Default,
    val language: Language = Language.Default
)

/**
 * No Intent/Effect triptych here: a preferences form has no state machine, and inventing
 * one would be boilerplate for its own sake.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeThemeMode: ObserveThemeMode,
    observeLanguage: ObserveLanguage,
    private val setThemeMode: SetThemeMode,
    private val setLanguage: SetLanguage
) : ViewModel() {

    val uiState = combine(observeThemeMode(), observeLanguage()) { mode, code ->
        SettingsUiState(themeMode = mode, language = Language.fromCode(code))
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
}
