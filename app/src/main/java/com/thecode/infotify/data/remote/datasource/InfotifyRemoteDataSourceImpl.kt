package com.thecode.infotify.data.remote.datasource

import com.thecode.infotify.data.remote.NewsApiRemoteService
import com.thecode.infotify.data.remote.mapper.NewsMapper
import com.thecode.infotify.domain.model.News
import javax.inject.Inject

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
