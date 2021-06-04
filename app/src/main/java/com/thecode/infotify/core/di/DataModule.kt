package com.thecode.infotify.core.di

import android.content.Context
import com.thecode.infotify.application.InfotifyDataStore
import com.thecode.infotify.core.local.InfotifyLocalDataSource
import com.thecode.infotify.core.local.InfotifyLocalDataSourceImpl
import com.thecode.infotify.database.AppDatabase
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
        return AppDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideInfotifyDataStore(dataStore: InfotifyDataStore): InfotifyLocalDataSource {
        return InfotifyLocalDataSourceImpl(dataStore)
    }
}
