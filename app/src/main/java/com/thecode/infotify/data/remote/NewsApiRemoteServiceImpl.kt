package com.thecode.infotify.data.remote

import com.thecode.infotify.data.remote.api.NewsApi
import com.thecode.infotify.data.remote.model.NewsObjectResponse
import com.thecode.infotify.data.remote.model.SourceObjectResponse
import retrofit2.Response


class NewsApiRemoteServiceImpl(
    private val newsApi: NewsApi
) : NewsApiRemoteService {

    override suspend fun getTopHeadlinesBySources(
        sources: String
    ): Response<NewsObjectResponse> {
        return newsApi.getTopHeadlinesBySources(sources)
    }

    override suspend fun getTopHeadlinesByCategory(
        category: String
    ): NewsObjectResponse {
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
