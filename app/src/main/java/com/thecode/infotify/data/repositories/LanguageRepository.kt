package com.thecode.infotify.data.repositories

import com.thecode.infotify.data.local.datasource.InfotifyLocalDataSourceImpl
import com.thecode.infotify.domain.model.LanguageName
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LanguageRepository @Inject constructor(
    private val localDataSource: InfotifyLocalDataSourceImpl
) {
    fun getLanguages(): List<LanguageName> {
        return LanguageName.entries
    }

    fun getUserLanguagePreference(): Flow<String> {
        return localDataSource.getUserLanguagePreference()
    }

    suspend fun setUserLanguagePreference(lang: String) {
        localDataSource.setUserLanguagePreference(lang)
    }
}