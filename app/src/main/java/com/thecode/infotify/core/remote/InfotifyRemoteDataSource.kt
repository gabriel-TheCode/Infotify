package com.thecode.infotify.core.remote

import com.thecode.infotify.application.InfotifyDataStore
import com.thecode.infotify.core.domain.News
import com.thecode.infotify.framework.datasource.NewsApiRemoteService
import com.thecode.infotify.framework.datasource.network.mapper.NewsMapper
import com.thecode.infotify.framework.datasource.network.mapper.SourceMapper
import javax.inject.Inject

interface InfotifyRemoteDataSource {

    suspend fun fetchNews(query: String, language: String, sortBy: String): News

    suspend fun fetchTopHeadlinesByLanguage(language: String): News

    suspend fun fetchTopHeadlinesByLanguageAndCategory(language: String, category: String): News
}

class InfotifyRemoteDataSourceImpl @Inject constructor(
    private val apiService: NewsApiRemoteService,
    private val dataStore: InfotifyDataStore,
    private val newsMapper: NewsMapper,
    private val sourceMapper: SourceMapper

) : InfotifyRemoteDataSource {
    override suspend fun fetchNews(query: String, language: String, sortBy: String): News {
        return newsMapper.mapToDomain(apiService.getAllNews(query, language, sortBy))
    }

    override suspend fun fetchTopHeadlinesByLanguage(language: String): News {
        return newsMapper.mapToDomain(apiService.getTopHeadlinesByLanguage(language))
    }

    override suspend fun fetchTopHeadlinesByLanguageAndCategory(
        language: String,
        category: String
    ): News {
        return newsMapper.mapToDomain(
            apiService.getTopHeadlinesByLanguageAndCategory(
                language,
                category
            )
        )
    }
}
