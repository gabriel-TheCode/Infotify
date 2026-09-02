package com.thecode.infotify.data.remote.newsdata

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * NewsData.io "latest" endpoint. One endpoint serves both the feed and search:
 * passing `q` turns it into a full-text query.
 *
 * The API key is added by [com.thecode.infotify.data.remote.ApiKeyInterceptor], never here.
 */
interface NewsDataApi {

    @GET("api/1/latest")
    suspend fun latest(
        @Query("language") language: String,
        @Query("category") category: String? = null,
        @Query("q") query: String? = null,
        @Query("page") cursor: String? = null,
        @Query("size") size: Int = PAGE_SIZE,
        @Query("removeduplicate") removeDuplicate: Int = 1,
        @Query("image") onlyWithImage: Int? = null
    ): NewsDataResponse

    companion object {
        /** Free plan caps a request at 10 articles. */
        const val PAGE_SIZE = 10
    }
}
