package com.thecode.infotify.database.source

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "source")
data class SourceEntity(

    @SerializedName("id")
    @PrimaryKey var id: String? = null,

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