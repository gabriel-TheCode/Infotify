package com.thecode.infotify.di

import android.content.Context
import androidx.room.Room
import com.thecode.infotify.data.local.InfotifyDataStore
import com.thecode.infotify.data.local.AppDatabase
import com.thecode.infotify.data.local.datasource.InfotifyLocalDataSource
import com.thecode.infotify.data.local.datasource.InfotifyLocalDataSourceImpl
import com.thecode.infotify.data.local.model.article.ArticlesDao
import com.thecode.infotify.data.local.model.source.SourcesDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "infotify.db"
        )
            .build()
    }

    @Singleton
    @Provides
    fun provideInfotifyDataStore(dataStore: InfotifyDataStore): InfotifyLocalDataSource {
        return InfotifyLocalDataSourceImpl(dataStore)
    }

    @Singleton
    @Provides
    fun provideArticlesDao(database: AppDatabase): ArticlesDao {
        return database.getArticlesDao()
    }

    @Singleton
    @Provides
    fun provideSourcesDao(database: AppDatabase): SourcesDao {
        return database.getSourcesDao()
    }
}
