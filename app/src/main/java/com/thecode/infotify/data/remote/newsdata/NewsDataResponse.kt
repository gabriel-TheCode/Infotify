package com.thecode.infotify.data.remote.newsdata

import com.google.gson.annotations.SerializedName

/**
 * Wire format of NewsData.io. Kept as a faithful mirror of the API — no cleanup here,
 * so that a provider change stays contained to this package plus its mapper.
 */
data class NewsDataResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("totalResults") val totalResults: Int?,
    @SerializedName("results") val results: List<NewsDataArticle>?,
    /** Opaque cursor for the following page; null on the last page. */
    @SerializedName("nextPage") val nextPage: String?
)

data class NewsDataArticle(
    @SerializedName("article_id") val articleId: String?,
    @SerializedName("link") val link: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("pubDate") val pubDate: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("category") val category: List<String>?,
    @SerializedName("source_id") val sourceId: String?,
    @SerializedName("source_name") val sourceName: String?,
    @SerializedName("source_icon") val sourceIcon: String?,
    @SerializedName("duplicate") val duplicate: Boolean?
)
