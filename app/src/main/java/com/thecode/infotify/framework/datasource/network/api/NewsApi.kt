package com.thecode.infotify.framework.datasource.network.api

import com.thecode.infotify.framework.datasource.network.model.NewsObjectResponse
import com.thecode.infotify.framework.datasource.network.model.SourceObjectResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    //region Top Headlines
    @GET("v2/top-headlines")
    suspend fun getTopHeadlinesByQuery(
        @Query("q") query: String
    ): Response<NewsObjectResponse>

    @GET("v2/top-headlines")
    suspend fun getTopHeadlinesBySources(
        @Query("sources") sources: String
    ): Response<NewsObjectResponse>

    @GET("v2/top-headlines")
    suspend fun getTopHeadlinesByCategory(
        @Query("category") category: String
    ): NewsObjectResponse

    //endregion

    //region Everything
    @GET("v2/everything")
    suspend fun getAllNewsByQuery(
        @Query("q") query: String
    ): Response<NewsObjectResponse>

    @GET("v2/everything")
    suspend fun getAllNews(
        @Query("q") query: String,
        @Query("language") language: String,
        @Query("sortBy") sortBy: String
    ): NewsObjectResponse

    //endregion

    //region Sources
    @GET("v2/sources")
    suspend fun getSources(): Response<SourceObjectResponse>

    @GET("v2/sources")
    suspend fun getSourcesByLang(
        @Query("language") language: String
    ): Response<SourceObjectResponse>

    //endregion
}
