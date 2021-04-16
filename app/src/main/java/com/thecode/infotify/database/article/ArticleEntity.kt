package com.thecode.infotify.database.article

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.thecode.infotify.database.source.SourceEntity

@Entity(tableName = "article")
data class ArticleEntity(

    @SerializedName("source")
    var source: SourceEntity? = null,

    @SerializedName("author")
    var author: String? = null,

    @SerializedName("title")
    var title: String? = null,

    @SerializedName("description")
    var description: String? = null,

    @SerializedName("url")
    @NonNull @PrimaryKey var url: String,

    @SerializedName("urlToImage")
    var urlToImage: String? = null,

    @SerializedName("publishedAt")
    var publishedAt: String? = null,

    @SerializedName("content")
    var content: String? = null

)