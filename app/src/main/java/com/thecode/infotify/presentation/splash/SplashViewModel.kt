package com.thecode.infotify.presentation.splash

import androidx.lifecycle.ViewModel
import com.thecode.infotify.core.usecases.IsOnboardingCompleted
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val isOnboardingCompleted: IsOnboardingCompleted
) : ViewModel() {

    fun isOnboardingCompleted(): Boolean {
        return isOnboardingCompleted.invoke()
    }
}
