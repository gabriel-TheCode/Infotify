package com.thecode.infotify.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thecode.infotify.data.local.bookmark.BookmarkDao
import com.thecode.infotify.data.local.bookmark.BookmarkEntity
import com.thecode.infotify.data.local.feed.CachedArticleEntity
import com.thecode.infotify.data.local.feed.FeedCacheDao

@Database(
    entities = [BookmarkEntity::class, CachedArticleEntity::class],
    version = AppDatabase.VERSION,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao

    abstract fun feedCacheDao(): FeedCacheDao

    companion object {
        const val VERSION = 3
        const val NAME = "infotify.db"
    }
}
