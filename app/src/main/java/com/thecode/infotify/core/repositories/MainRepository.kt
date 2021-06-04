package com.thecode.infotify.core.repositories

import com.thecode.infotify.core.local.InfotifyLocalDataSourceImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val localDataSource: InfotifyLocalDataSourceImpl
) {
    suspend fun setNightModeEnabled(state: Boolean) {
        localDataSource.setNightModeEnabled(state)
    }

    fun isNightModeEnabled(): Flow<Boolean> {
        return localDataSource.isNightModeEnabled()
    }

    suspend fun clearAppData() {
        localDataSource.clearAppData()
    }
}
