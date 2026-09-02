package com.thecode.infotify.presentation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecode.infotify.domain.model.ThemeMode
import com.thecode.infotify.domain.usecase.ObserveThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class MainUiState(val themeMode: ThemeMode = ThemeMode.Default)

@HiltViewModel
class MainViewModel @Inject constructor(
    observeThemeMode: ObserveThemeMode
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * False until the stored theme has been read once.
     *
     * The previous MainViewModel launched a coroutine and returned the StateFlow's value
     * on the same line, so it always read false and the app started in light mode
     * regardless of the saved preference.
     */
    var isReady: Boolean = false
        private set

    init {
        viewModelScope.launch {
            observeThemeMode().collect { mode ->
                _uiState.value = MainUiState(themeMode = mode)
                isReady = true
            }
        }
    }
}
