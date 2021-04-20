package com.thecode.infotify.framework.datasource.network.model

import com.google.gson.annotations.SerializedName

data class SourceObjectResponse(
    @SerializedName("status")
    var status: String? = null,

    @SerializedName("sources")
    var sources: List<Result>
) {

data class Result(
    val `source`: Source
) {
            data class Source(
                var id: String? = null,
                var cnbc: String? = null,
                var name: String? = null,
                var description: String? = null,
                var url: String? = null,
                var category: String? = null,
                var language: String? = null,
                var country: String? = null
            )
        }
    }
