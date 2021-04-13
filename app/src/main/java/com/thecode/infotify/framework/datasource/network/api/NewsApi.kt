package com.thecode.infotify.framework.datasource.network.api

import com.thecode.infotify.framework.datasource.network.model.NewsObjectResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApi {
    //Top Headlines
    @GET("v2/top-headlines")
    fun getTopHeadlinesByCountry(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsObjectResponse>

    @GET("v2/top-headlines")
    fun getTopHeadlinesByLanguage(
        @Query("language") language: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsObjectResponse>

    @GET("v2/top-headlines")
    fun getTopHeadlinesByLanguageAndCategory(
        @Query("country") language: String,
        @Query("category") category: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsObjectResponse>

    @GET("v2/top-headlines")
    fun getTopHeadlinesByQuery(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsObjectResponse>

    @GET("v2/top-headlines")
    fun getTopHeadlinesBySources(
        @Query("sources") sources: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsObjectResponse>

    @GET("v2/top-headlines")
    fun getTopHeadlinesByCategory(
        @Query("category") category: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsObjectResponse>

    //Everything
    @GET("v2/everything")
    fun getEverythingByQuery(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsObjectResponse>


    @GET("v2/everything")
    fun getEverything(
        @Query("q") query: String,
        @Query("language") language: String,
        @Query("sortBy") sortBy: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsObjectResponse>

    //Sources
    @GET("v2/sources")
    fun getSources(@Query("apiKey") apiKey: String): Call<NewsObjectResponse>

    @GET("v2/sources")
    fun getSourcesByLang(
        @Query("language") language: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsObjectResponse>
}