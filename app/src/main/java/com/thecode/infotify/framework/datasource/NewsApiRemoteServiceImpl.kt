package com.thecode.infotify.framework.datasource

import com.thecode.infotify.framework.datasource.network.api.NewsApi
import com.thecode.infotify.framework.datasource.network.model.NewsObjectResponse
import com.thecode.infotify.framework.datasource.network.model.SourceObjectResponse
import retrofit2.Response

interface NewsApiRemoteService {
    //region NEWS
    suspend fun getAllNews(query: String, language: String, sortBy: String): NewsObjectResponse

    suspend fun getTopHeadlinesByLanguage(language: String): NewsObjectResponse

    suspend fun getTopHeadlinesByLanguageAndCategory(language: String, category: String): NewsObjectResponse

    suspend fun getTopHeadlinesByCountry(country: String): Response<NewsObjectResponse>

    suspend fun getTopHeadlinesBySources(sources: String): Response<NewsObjectResponse>

    suspend fun getTopHeadlinesByCategory(category: String): Response<NewsObjectResponse>

    suspend fun getAllNewsByQuery(query: String): Response<NewsObjectResponse>
    //endregion

    //region SOURCE
    suspend fun getSources(): Response<SourceObjectResponse>

    suspend fun getSourcesByLang(language: String): Response<SourceObjectResponse>
    //endregion
}

class NewsApiRemoteServiceImpl constructor(
    private val newsApi: NewsApi
) : NewsApiRemoteService {

    override suspend fun getTopHeadlinesByLanguage(
        language: String
    ): NewsObjectResponse {
        return newsApi.getTopHeadlinesByLanguage(language)
    }

    override suspend fun getTopHeadlinesByLanguageAndCategory(
        language: String,
        category: String
    ): NewsObjectResponse {
        return newsApi.getTopHeadlinesByLanguageAndCategory(language, category)
    }

    override suspend fun getTopHeadlinesByCountry(
        country: String
    ): Response<NewsObjectResponse> {
        return newsApi.getTopHeadlinesByCountry(country)
    }

    override suspend fun getTopHeadlinesBySources(
        sources: String
    ): Response<NewsObjectResponse> {
        return newsApi.getTopHeadlinesBySources(sources)
    }

    override suspend fun getTopHeadlinesByCategory(
        category: String
    ): Response<NewsObjectResponse> {
        return newsApi.getTopHeadlinesByCategory(category)
    }

    override suspend fun getAllNewsByQuery(
        query: String
    ): Response<NewsObjectResponse> {
       return newsApi.getAllNewsByQuery(query)
    }

    override suspend fun getSources(): Response<SourceObjectResponse> {
       return newsApi.getSources()
    }

    override suspend fun getSourcesByLang(
        language: String
    ): Response<SourceObjectResponse> {
       return newsApi.getSourcesByLang(language)
    }

    override suspend fun getAllNews(
        query: String,
        language: String,
        sortBy: String
    ): NewsObjectResponse {
        return newsApi.getAllNews(query, language, sortBy)
    }
}