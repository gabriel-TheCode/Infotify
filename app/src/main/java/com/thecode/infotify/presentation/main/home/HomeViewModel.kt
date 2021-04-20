package com.thecode.infotify.presentation.main.home

import androidx.lifecycle.ViewModel
import com.thecode.infotify.core.usecases.IsNightModeEnabled
import com.thecode.infotify.core.usecases.SetNightModeEnabled
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val isNightModeEnabled: IsNightModeEnabled,
    private val setNightModeEnabled: SetNightModeEnabled
) : ViewModel() {

    fun setNightMode(state: Boolean) {
        setNightModeEnabled(state)
    }

    fun isNightModeActivated(): Boolean {
        return isNightModeEnabled()
    }
}
