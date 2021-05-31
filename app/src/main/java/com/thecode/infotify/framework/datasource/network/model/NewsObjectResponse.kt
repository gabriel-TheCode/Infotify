package com.thecode.infotify.framework.datasource.network.model

import com.google.gson.annotations.SerializedName

class NewsObjectResponse {

    @SerializedName("status")
    var status: String? = null

    @SerializedName("totalResults")
    var totalResults: String? = null

    @SerializedName("articles")
    val articles: List<Result> = listOf()


    inner class Result(

            @SerializedName("source")
            val source: SourceItem,

            @SerializedName("author")
            var author: String? = null,

            @SerializedName("title")
            var title: String? = null,

            @SerializedName("description")
            var description: String? = null,

            @SerializedName("url")
            var url: String,

            @SerializedName("urlToImage")
            var urlToImage: String? = null,

            @SerializedName("publishedAt")
            var publishedAt: String? = null,

            @SerializedName("content")
            var content: String? = null
        )


    inner class SourceItem(
        @SerializedName("id")
        var id: String,

        @SerializedName("cnbc")
        var cnbc: String? = null,

        @SerializedName("name")
        var name: String,

        @SerializedName("description")
        var description: String? = null,

        @SerializedName("url")
        var url: String? = null,

        @SerializedName("category")
        var category: String? = null,

        @SerializedName("language")
        var language: String? = null,

        @SerializedName("country")
        var country: String? = null
    )

}
