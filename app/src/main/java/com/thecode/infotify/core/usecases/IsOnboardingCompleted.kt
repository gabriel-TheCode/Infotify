package com.thecode.infotify.core.usecases

import com.thecode.infotify.core.repositories.OnboardingRepository
import javax.inject.Inject

class IsOnboardingCompleted @Inject constructor(
    private val repository: OnboardingRepository
) {
    operator fun invoke(): Boolean {
        return repository.isOnboardingCompleted()
    }
}