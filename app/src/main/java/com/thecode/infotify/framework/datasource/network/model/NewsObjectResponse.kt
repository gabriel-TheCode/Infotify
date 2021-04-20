package com.thecode.infotify.framework.datasource.network.model

import com.google.gson.annotations.SerializedName
import com.thecode.infotify.core.domain.SourceItem

data class NewsObjectResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("totalResults")
    val totalResults: String,

    @SerializedName("articles")
    val articles: List<Result>

) {

    data class Result(
        val `article`: Article
    ) {
        data class Article(
            var source: SourceItem? = null,
            var author: String? = null,
            var title: String? = null,
            var description: String? = null,
            var url: String? = null,
            var urlToImage: String? = null,
            var publishedAt: String? = null,
            var content: String? = null
        )
    }
}
