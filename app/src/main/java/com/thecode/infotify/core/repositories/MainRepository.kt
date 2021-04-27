package com.thecode.infotify.core.repositories

import com.thecode.infotify.core.local.InfotifyLocalDataSourceImpl
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val localDataSource: InfotifyLocalDataSourceImpl
) {
    fun setNightModeEnabled(state: Boolean) {
        localDataSource.setNightModeEnabled(state)
    }

    fun isNightModeEnabled(): Boolean {
        return localDataSource.isNightModeEnabled()
    }

    fun clearAppData() {
        localDataSource.clearAppData()
    }
}