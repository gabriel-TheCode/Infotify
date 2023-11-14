package com.thecode.infotify.framework.datasource.network.model

import com.google.gson.annotations.SerializedName

data class SourceObjectResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("sources")
    var sources: List<Result> = listOf()
) {

    data class Result(
        var source: Source
    ) {
        data class Source(
            @SerializedName("id")
            var id: String? = null,

            @SerializedName("cnbc")
            var cnbc: String? = null,

            @SerializedName("name")
            var name: String? = null,

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
}
