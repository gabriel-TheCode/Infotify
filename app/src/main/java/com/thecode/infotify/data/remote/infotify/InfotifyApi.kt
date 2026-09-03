package com.thecode.infotify.data.remote.infotify

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Infotify's own feed endpoint, served by the proxy at infotify.nativia.co.
 *
 * The app no longer talks to NewsData.io directly, and therefore no longer carries an API
 * key: the key lives on the server. That also means the provider can change without an app
 * release, and that a thousand users asking for the same feed cost one upstream credit
 * instead of a thousand.
 */
interface InfotifyApi {

    /**
     * @param categories comma-separated topic values, at most 5 (the provider's cap).
     * @param country comma-separated ISO country codes for a region, at most 5.
     * @param query full-text search; when present, categories are ignored server-side.
     * @param cursor opaque token from the previous page's [FeedResponse.nextCursor].
     */
    @GET("v1/feed")
    suspend fun feed(
        @Query("language") language: String,
        @Query("categories") categories: String? = null,
        @Query("country") country: String? = null,
        @Query("q") query: String? = null,
        @Query("page") cursor: String? = null
    ): FeedResponse
}
