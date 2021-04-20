package com.thecode.infotify.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.thecode.infotify.database.article.ArticleEntity
import com.thecode.infotify.database.article.ArticlesDao
import com.thecode.infotify.database.source.SourceEntity
import com.thecode.infotify.database.source.SourcesDao

@Database(entities = [ArticleEntity::class, SourceEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getArticlesDao(): ArticlesDao
    abstract fun getSourcesDao(): SourcesDao

    companion object {

        /** The only instance  */
        private var sInstance: AppDatabase? = null

        /**
         * Gets the singleton instance of AppDatabase.
         *
         * @param context The context.
         * @return The singleton instance of AppDatabase.
         */

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (sInstance == null) {
                sInstance = Room
                    .databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "infotify.db"
                    )
                    .build()
            }
            return sInstance as AppDatabase
        }
    }
}
