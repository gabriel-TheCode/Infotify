package com.thecode.infotify.core.usecases

import com.thecode.infotify.core.data.OnboardingRepository
import javax.inject.Inject

class SetOnboardingCompleted @Inject constructor(
    private val repository: OnboardingRepository
) {
    operator fun invoke() {
        return repository.setOnboardingCompleted()
    }
}