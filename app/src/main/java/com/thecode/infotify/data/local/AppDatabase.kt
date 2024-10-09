package com.thecode.infotify.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.thecode.infotify.data.local.model.article.ArticleEntity
import com.thecode.infotify.data.local.model.article.ArticlesDao
import com.thecode.infotify.data.local.model.source.SourceConverter
import com.thecode.infotify.data.local.model.source.SourceEntity
import com.thecode.infotify.data.local.model.source.SourcesDao

@Database(
    entities = [ArticleEntity::class, SourceEntity::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(SourceConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getArticlesDao(): ArticlesDao
    abstract fun getSourcesDao(): SourcesDao
}
