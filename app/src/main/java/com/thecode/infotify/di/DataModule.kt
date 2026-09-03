package com.thecode.infotify.di

import android.content.Context
import androidx.room.Room
import com.thecode.infotify.data.local.AppDatabase
import com.thecode.infotify.data.local.MIGRATION_1_2
import com.thecode.infotify.data.local.MIGRATION_2_3
import com.thecode.infotify.data.local.bookmark.BookmarkDao
import com.thecode.infotify.data.local.feed.FeedCacheDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()

    @Singleton
    @Provides
    fun provideBookmarkDao(database: AppDatabase): BookmarkDao = database.bookmarkDao()

    @Singleton
    @Provides
    fun provideFeedCacheDao(database: AppDatabase): FeedCacheDao = database.feedCacheDao()
}
