package com.thecode.infotify.core.usecases

import com.thecode.infotify.core.domain.OnBoardingPart
import com.thecode.infotify.core.repositories.OnBoardingRepository
import javax.inject.Inject

class GetOnBoardingParts @Inject constructor(
    private val repository: OnBoardingRepository
) {
    operator fun invoke(): List<OnBoardingPart> {
        return repository.getOnBoardingList()
    }
}