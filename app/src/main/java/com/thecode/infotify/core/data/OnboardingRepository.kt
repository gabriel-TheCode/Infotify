package com.thecode.infotify.core.data

import com.thecode.infotify.core.local.InfotifyLocalDataSourceImpl
import javax.inject.Inject

class OnboardingRepository @Inject constructor(
    private val localDataSource: InfotifyLocalDataSourceImpl
) {

    fun setOnboardingCompleted() {
        localDataSource.setOnboardingCompleted()
    }

    fun isOnboardingCompleted(): Boolean {
        return localDataSource.isOnboardingCompleted()
    }
}
