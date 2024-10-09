package com.thecode.infotify.domain.usecases

import com.thecode.infotify.data.repositories.OnBoardingRepository
import com.thecode.infotify.domain.model.OnBoardingPart
import javax.inject.Inject

class GetOnBoardingParts @Inject constructor(
    private val repository: OnBoardingRepository
) {
    operator fun invoke(): List<OnBoardingPart> {
        return repository.getOnBoardingList()
    }
}
