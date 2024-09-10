package com.thecode.infotify.core.remote

import com.thecode.infotify.core.domain.News

interface InfotifyRemoteDataSource {

    suspend fun fetchNews(query: String, language: String, sortBy: String): News

    suspend fun fetchTopHeadlinesByCategory(category: String): News
}