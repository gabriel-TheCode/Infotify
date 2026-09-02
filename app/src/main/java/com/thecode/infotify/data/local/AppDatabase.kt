package com.thecode.infotify.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thecode.infotify.data.local.bookmark.BookmarkDao
import com.thecode.infotify.data.local.bookmark.BookmarkEntity

@Database(
    entities = [BookmarkEntity::class],
    version = AppDatabase.VERSION,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        const val VERSION = 2
        const val NAME = "infotify.db"
    }
}
