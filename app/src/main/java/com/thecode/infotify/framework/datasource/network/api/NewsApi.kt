package com.thecode.infotify.framework.datasource.network.api

import com.thecode.infotify.framework.datasource.network.model.NewsObjectResponse
import com.thecode.infotify.framework.datasource.network.model.SourceObjectResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    // Top Headlines
    @GET("v2/top-headlines")
    fun getTopHeadlinesByCountry(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String
    ): Response<NewsObjectResponse>

    @GET("v2/top-headlines")
    fun getTopHeadlinesByLanguage(
        @Query("language") language: String,
        @Query("apiKey") apiKey: String
    ): NewsObjectResponse

    @GET("v2/top-headlines")
    fun getTopHeadlinesByLanguageAndCategory(
        @Query("country") language: String,
        @Query("category") category: String,
        @Query("apiKey") apiKey: String
    ): NewsObjectResponse

    @GET("v2/top-headlines")
    fun getTopHeadlinesByQuery(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String
    ): Response<NewsObjectResponse>

    @GET("v2/top-headlines")
    fun getTopHeadlinesBySources(
        @Query("sources") sources: String,
        @Query("apiKey") apiKey: String
    ): Response<NewsObjectResponse>

    @GET("v2/top-headlines")
    fun getTopHeadlinesByCategory(
        @Query("category") category: String,
        @Query("apiKey") apiKey: String
    ): Response<NewsObjectResponse>

    // Everything
    @GET("v2/everything")
    fun getAllNewsByQuery(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String
    ): Response<NewsObjectResponse>

    @GET("v2/everything")
    fun getAllNews(
        @Query("q") query: String,
        @Query("language") language: String,
        @Query("sortBy") sortBy: String,
        @Query("apiKey") apiKey: String
    ): NewsObjectResponse

    //region Sources
    @GET("v2/sources")
    fun getSources(@Query("apiKey") apiKey: String): Response<SourceObjectResponse>

    @GET("v2/sources")
    fun getSourcesByLang(
        @Query("language") language: String,
        @Query("apiKey") apiKey: String
    ): Response<SourceObjectResponse>

    //endregion
}