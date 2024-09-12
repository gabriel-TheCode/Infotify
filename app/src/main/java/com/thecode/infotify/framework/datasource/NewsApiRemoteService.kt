package com.thecode.infotify.framework.datasource

import com.thecode.infotify.framework.datasource.network.model.NewsObjectResponse
import com.thecode.infotify.framework.datasource.network.model.SourceObjectResponse
import retrofit2.Response

interface NewsApiRemoteService {
    //region NEWS
    suspend fun getAllNews(query: String, language: String, sortBy: String): NewsObjectResponse

    suspend fun getTopHeadlinesBySources(sources: String): Response<NewsObjectResponse>

    suspend fun getTopHeadlinesByCategory(category: String): NewsObjectResponse

    suspend fun getAllNewsByQuery(query: String): Response<NewsObjectResponse>
    //endregion

    //region SOURCE
    suspend fun getSources(): Response<SourceObjectResponse>

    suspend fun getSourcesByLang(language: String): Response<SourceObjectResponse>
    //endregion
}