package com.thecode.infotify.domain.usecases

import com.thecode.infotify.data.repositories.OnBoardingRepository
import javax.inject.Inject

class SetOnboardingCompleted @Inject constructor(
    private val repository: OnBoardingRepository
) {
    suspend operator fun invoke() {
        repository.setOnboardingCompleted()
    }
}
