package com.thecode.infotify.framework.datasource.network.model

import com.google.gson.annotations.SerializedName

data class NewsObjectResponse(
    @SerializedName("status")
    val status: String? = null,

    @SerializedName("totalResults")
    val totalResults: Int,

    @SerializedName("articles")
    val articles: List<Result> = listOf()
) {
    data class Result(

        @SerializedName("source")
        val source: SourceItem,

        @SerializedName("author")
        val author: String? = null,

        @SerializedName("title")
        val title: String? = null,

        @SerializedName("description")
        val description: String? = null,

        @SerializedName("url")
        val url: String,

        @SerializedName("urlToImage")
        val urlToImage: String? = null,

        @SerializedName("publishedAt")
        val publishedAt: String? = null,

        @SerializedName("content")
        val content: String? = null
    )

    data class SourceItem(
        @SerializedName("id")
        val id: String,

        @SerializedName("name")
        val name: String? = null,

        @SerializedName("description")
        val description: String? = null,

        @SerializedName("url")
        val url: String? = null,

        @SerializedName("category")
        val category: String? = null,

        @SerializedName("language")
        val language: String? = null,
    )
}
