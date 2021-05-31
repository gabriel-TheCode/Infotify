package com.thecode.infotify.database.source

import androidx.room.TypeConverter
import com.google.gson.Gson

internal class SourceConverter {
    @TypeConverter
    fun stringToSource(string: String?): SourceEntity = Gson().fromJson(string, SourceEntity::class.java)

    @TypeConverter
    fun sourceToString(list: SourceEntity?): String = Gson().toJson(list)
}