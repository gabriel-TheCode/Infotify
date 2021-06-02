package com.thecode.infotify.core.repositories

import com.thecode.infotify.R
import com.thecode.infotify.core.domain.OnBoardingPart
import com.thecode.infotify.core.local.InfotifyLocalDataSourceImpl
import javax.inject.Inject

class OnBoardingRepository @Inject constructor(
    private val localDataSource: InfotifyLocalDataSourceImpl
) {

    fun setOnboardingCompleted() {
        localDataSource.setOnboardingCompleted()
    }

    fun isOnboardingCompleted(): Boolean {
        return localDataSource.isOnboardingCompleted()
    }

    fun getOnBoardingList(): List<OnBoardingPart> {
        return listOf(
            OnBoardingPart(
                image = R.raw.lottie_earth,
                title = R.string.title_onboarding_1,
                description = R.string.description_onboarding_1
            ),
            OnBoardingPart(
                image = R.raw.lottie_social_media,
                title = R.string.title_onboarding_2,
                description = R.string.description_onboarding_2
            ),
            OnBoardingPart(
                image = R.raw.lottie_digital,
                title = R.string.title_onboarding_3,
                description = R.string.description_onboarding_3
            )
        )
    }
}
