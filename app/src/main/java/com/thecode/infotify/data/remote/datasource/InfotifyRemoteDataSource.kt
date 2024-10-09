package com.thecode.infotify.data.remote.datasource

import com.thecode.infotify.domain.model.News

interface InfotifyRemoteDataSource {

    suspend fun fetchNews(query: String, language: String, sortBy: String): News

    suspend fun fetchTopHeadlinesByCategory(category: String): News
}