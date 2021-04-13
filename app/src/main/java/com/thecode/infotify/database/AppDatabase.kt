package com.thecode.infotify.database


import androidx.room.Database
import androidx.room.RoomDatabase
import com.thecode.infotify.database.article.ArticlesDao

@Database(entities = [Article::class], version = 1)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun getArticlesDao(): ArticlesDao
}