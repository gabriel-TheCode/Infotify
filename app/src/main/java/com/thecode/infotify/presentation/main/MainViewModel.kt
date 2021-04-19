package com.thecode.infotify.presentation.main

import androidx.lifecycle.ViewModel
import com.thecode.infotify.core.usecases.IsNightModeEnabled
import com.thecode.infotify.core.usecases.IsOnboardingCompleted
import com.thecode.infotify.core.usecases.SetNightModeEnabled
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val isNightModeEnabled: IsNightModeEnabled,
) : ViewModel() {

    fun isNightModeActivated(): Boolean{
        return isNightModeEnabled()
    }
}