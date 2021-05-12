package com.thecode.infotify.presentation.about

import androidx.lifecycle.ViewModel
import com.thecode.infotify.core.usecases.IsNightModeEnabled
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val isNightModeEnabled: IsNightModeEnabled
) : ViewModel() {
    fun isNightModeActivated(): Boolean {
        return isNightModeEnabled()
    }
}