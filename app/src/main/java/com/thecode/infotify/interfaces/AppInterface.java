package com.thecode.infotify.interfaces;


import com.thecode.infotify.responses.NewsObjectResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AppInterface {


    //Top Headlines
    @GET("top-headlines")
    Call<NewsObjectResponse> getTopHeadlinesByCountry(@Query("country") String country, @Query("apiKey") String apiKey);

    @GET("top-headlines")
    Call<NewsObjectResponse> getTopHeadlinesByQuery(@Query("q") String query, @Query("apiKey") String apiKey);

    @GET("top-headlines")
    Call<NewsObjectResponse> getTopHeadlinesBySources(@Query("sources") String sources, @Query("apiKey") String apiKey);


    //Everything
    @GET("everything")
    Call<NewsObjectResponse> getEverythingByQuery(@Query("q") String query, @Query("apiKey") String apiKey);

    @GET("everything")
    Call<NewsObjectResponse> getEverythingByDomains(@Query("domains") String query, @Query("apiKey") String apiKey);

    @GET("everything")
    Call<NewsObjectResponse> getEverythingByCountry(@Query("country") String country, @Query("apiKey") String apiKey);


    //Sources
    @GET("sources")
    Call<NewsObjectResponse> getSources(@Query("apiKey") String apiKey);

    @GET("sources")
    Call<NewsObjectResponse> getSourcesByCountry(@Query("country") String country, @Query("apiKey") String apiKey);

    @GET("sources")
    Call<NewsObjectResponse> getSourcesByLang(@Query("language") String language, @Query("apiKey") String apiKey);
}