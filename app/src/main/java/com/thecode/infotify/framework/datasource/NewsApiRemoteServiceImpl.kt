package com.thecode.infotify.framework.datasource

import com.thecode.infotify.framework.datasource.network.api.NewsApi
import com.thecode.infotify.framework.datasource.network.model.NewsObjectResponse
import com.thecode.infotify.framework.datasource.network.model.SourceObjectResponse
import dagger.Provides
import retrofit2.Response
import javax.inject.Singleton


interface NewsApiRemoteService {
    //region NEWS
    suspend fun getAllNews( query: String, language: String, sortBy: String, apiKey: String): NewsObjectResponse

    suspend fun getTopHeadlinesByLanguage(language: String, apiKey: String): NewsObjectResponse

    suspend fun getTopHeadlinesByLanguageAndCategory(language: String, category: String, apiKey: String): NewsObjectResponse

    suspend fun getTopHeadlinesByCountry(country: String, apiKey: String): Response<NewsObjectResponse>

    suspend fun getTopHeadlinesBySources(sources: String, apiKey: String): Response<NewsObjectResponse>

    suspend fun getTopHeadlinesByCategory(category: String, apiKey: String): Response<NewsObjectResponse>

    suspend fun getAllNewsByQuery(query: String, apiKey: String): Response<NewsObjectResponse>
    //endregion

    //region SOURCE
    suspend fun getSources(apiKey: String): Response<SourceObjectResponse>

    suspend fun getSourcesByLang(language: String, apiKey: String): Response<SourceObjectResponse>
    //endregion
}

class NewsApiRemoteServiceImpl constructor(
    private val newsApi: NewsApi
) : NewsApiRemoteService {

    override suspend fun getTopHeadlinesByLanguage(
        language: String,
        apiKey: String
    ): NewsObjectResponse {
        return newsApi.getTopHeadlinesByLanguage(language, apiKey)
    }

    override suspend fun getTopHeadlinesByLanguageAndCategory(
        language: String,
        category: String,
        apiKey: String
    ): NewsObjectResponse {
        return newsApi.getTopHeadlinesByLanguageAndCategory(language, category, apiKey)
    }

    override suspend fun getTopHeadlinesByCountry(
        country: String,
        apiKey: String
    ): Response<NewsObjectResponse> {
        return newsApi.getTopHeadlinesByCountry(country, apiKey)
    }

    override suspend fun getTopHeadlinesBySources(
        sources: String,
        apiKey: String
    ): Response<NewsObjectResponse> {
        return newsApi.getTopHeadlinesBySources(sources, apiKey)
    }

    override suspend fun getTopHeadlinesByCategory(
        category: String,
        apiKey: String
    ): Response<NewsObjectResponse> {
        return newsApi.getTopHeadlinesByCategory(category, apiKey)
    }

    override suspend fun getAllNewsByQuery(
        query: String,
        apiKey: String
    ): Response<NewsObjectResponse> {
       return newsApi.getAllNewsByQuery(query, apiKey)
    }

    override suspend fun getSources(apiKey: String): Response<SourceObjectResponse> {
       return newsApi.getSources(apiKey)
    }

    override suspend fun getSourcesByLang(
        language: String,
        apiKey: String
    ): Response<SourceObjectResponse> {
       return newsApi.getSourcesByLang(language, apiKey)
    }

    override suspend fun getAllNews(
        query: String,
        language: String,
        sortBy: String,
        apiKey: String
    ): NewsObjectResponse {
        return newsApi.getAllNews(query, language, sortBy, apiKey)
    }
}