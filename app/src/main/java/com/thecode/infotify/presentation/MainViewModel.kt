package com.thecode.infotify.presentation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.domain.model.ThemeMode
import com.thecode.infotify.domain.usecase.IsOnboardingCompleted
import com.thecode.infotify.domain.usecase.ObserveThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class MainUiState(
    val themeMode: ThemeMode = ThemeMode.Default,
    val onboardingCompleted: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    observeThemeMode: ObserveThemeMode,
    isOnboardingCompleted: IsOnboardingCompleted
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * False until theme and onboarding status have both been read once.
     *
     * The splash is held while this is false, so the app never paints the wrong theme or
     * flashes the feed before deciding it should have shown onboarding. The previous
     * MainViewModel launched a coroutine and read the StateFlow on the same line, so it
     * always resolved to the default and the app started in light mode regardless of the
     * saved preference.
     */
    var isReady: Boolean = false
        private set

    init {
        viewModelScope.launch {
            combine(observeThemeMode(), isOnboardingCompleted()) { mode, completed ->
                MainUiState(themeMode = mode, onboardingCompleted = completed)
            }.collect { state ->
                _uiState.value = state
                isReady = true
            }
        }
    }
}
