package com.thecode.infotify.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.thecode.infotify.core.usecases.SetOnboardingCompleted
import javax.inject.Inject

class OnboardingViewModel @Inject constructor(
    private val setOnboardingCompleted: SetOnboardingCompleted
) : ViewModel() {
    fun setOnboardingCompleted() {
        setOnboardingCompleted.invoke()
    }
}