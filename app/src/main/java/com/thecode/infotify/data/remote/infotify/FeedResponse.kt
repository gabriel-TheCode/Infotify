package com.thecode.infotify.data.remote.infotify

import com.google.gson.annotations.SerializedName

/**
 * Infotify's own envelope, produced by the proxy.
 *
 * Unlike the raw provider format this replaces, every article here is already usable:
 * dates are ISO-8601, blanks are null, duplicates and unopenable entries are gone. The
 * cleaning happens server-side so it can be corrected without shipping an app update.
 */
data class FeedResponse(
    @SerializedName("articles") val articles: List<ArticleDto>?,
    @SerializedName("nextCursor") val nextCursor: String?,
    @SerializedName("cachedAt") val cachedAt: String?
)

data class ArticleDto(
    @SerializedName("id") val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("publishedAt") val publishedAt: String?,
    @SerializedName("source") val source: SourceDto?,
    @SerializedName("categories") val categories: List<String>?
)

data class SourceDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("iconUrl") val iconUrl: String?
)
