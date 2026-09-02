package com.thecode.infotify.di

import com.thecode.infotify.data.repository.BookmarkRepositoryImpl
import com.thecode.infotify.data.repository.NewsRepositoryImpl
import com.thecode.infotify.data.repository.PreferencesRepositoryImpl
import com.thecode.infotify.domain.repository.BookmarkRepository
import com.thecode.infotify.domain.repository.NewsRepository
import com.thecode.infotify.domain.repository.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds domain interfaces to their data-layer implementations.
 *
 * This is the piece that was missing: the previous modules provided concrete classes, so
 * every use case ended up importing from the data layer directly.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNewsRepository(impl: NewsRepositoryImpl): NewsRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(impl: BookmarkRepositoryImpl): BookmarkRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(impl: PreferencesRepositoryImpl): PreferencesRepository
}
