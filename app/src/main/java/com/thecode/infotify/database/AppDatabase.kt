package com.thecode.infotify.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.thecode.infotify.database.article.ArticleEntity
import com.thecode.infotify.database.article.ArticlesDao
import com.thecode.infotify.database.source.SourceConverter
import com.thecode.infotify.database.source.SourceEntity
import com.thecode.infotify.database.source.SourcesDao

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
