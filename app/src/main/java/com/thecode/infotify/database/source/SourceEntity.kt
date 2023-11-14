package com.thecode.infotify.database.source

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "source")
data class SourceEntity(

    @SerializedName("id")
    @PrimaryKey var id: String,

    @SerializedName("name")
    var name: String? = null,

)
