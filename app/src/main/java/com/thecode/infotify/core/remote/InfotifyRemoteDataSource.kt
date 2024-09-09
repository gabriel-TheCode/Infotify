package com.thecode.infotify.core.remote

import com.thecode.infotify.application.InfotifyDataStore
import com.thecode.infotify.core.domain.News
import com.thecode.infotify.framework.datasource.NewsApiRemoteService
import com.thecode.infotify.framework.datasource.network.mapper.NewsMapper
import com.thecode.infotify.framework.datasource.network.mapper.SourceMapper
import javax.inject.Inject

interface InfotifyRemoteDataSource {

    suspend fun fetchNews(query: String, language: String, sortBy: String): News

    suspend fun fetchTopHeadlinesByCategory(category: String): News
}

class InfotifyRemoteDataSourceImpl @Inject constructor(
    private val apiService: NewsApiRemoteService,
    private val newsMapper: NewsMapper

) : InfotifyRemoteDataSource {
    override suspend fun fetchNews(query: String, language: String, sortBy: String): News {
        return newsMapper.mapToDomain(apiService.getAllNews(query, language, sortBy))
    }

    override suspend fun fetchTopHeadlinesByCategory(
        category: String
    ): News {
        return newsMapper.mapToDomain(
            apiService.getTopHeadlinesByCategory(
                category
            )
        )
    }
}
