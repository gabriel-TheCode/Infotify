package com.thecode.infotify.core.repositories

import com.thecode.infotify.core.domain.LanguageName
import com.thecode.infotify.core.local.InfotifyLocalDataSourceImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LanguageRepository @Inject constructor(
    private val localDataSource: InfotifyLocalDataSourceImpl
) {
    fun getLanguages(): List<LanguageName> {
        return LanguageName.values().toList()
    }

    fun getUserLanguagePreference(): Flow<String> {
        return localDataSource.getUserLanguagePreference()
    }

    suspend fun setUserLanguagePreference(lang: String) {
        localDataSource.setUserLanguagePreference(lang)
    }
}