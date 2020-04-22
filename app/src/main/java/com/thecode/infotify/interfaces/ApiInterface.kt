package com.thecode.infotify.interfaces

import com.thecode.infotify.responses.NewsObjectResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    //Top Headlines
    @GET("top-headlines")
    fun getTopHeadlinesByCountry(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsObjectResponse>

    @GET("top-headlines")
    fun getTopHeadlinesByQuery(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsObjectResponse>

    @GET("top-headlines")
    fun getTopHeadlinesBySources(
        @Query("sources") sources: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsObjectResponse>?

    //Everything
    @GET("everything")
    fun getEverythingByQuery(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsObjectResponse>

    @GET("everything")
    fun getEverythingByDomains(
        @Query("domains") query: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsObjectResponse>

    @GET("everything")
    fun getEverythingByCountry(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsObjectResponse>

    //Sources
    @GET("sources")
    fun getSources(@Query("apiKey") apiKey: String): Call<NewsObjectResponse>

    @GET("sources")
    fun getSourcesByCountry(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String?
    ): Call<NewsObjectResponse>

    @GET("sources")
    fun getSourcesByLang(
        @Query("language") language: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsObjectResponse>
}