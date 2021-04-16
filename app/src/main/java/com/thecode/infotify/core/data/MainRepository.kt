package com.thecode.infotify.core.data

import com.thecode.infotify.core.local.InfotifyLocalDataSourceImpl
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val localDataSource: InfotifyLocalDataSourceImpl
) {
    fun setNightModeEnabled(state: Boolean){
        localDataSource.setNightModeEnabled(state)
    }

    fun isNightModeEnabled(){
        localDataSource.isNightModeEnabled()
    }

    fun clearAppData(){
        localDataSource.clearAppData()
    }
}