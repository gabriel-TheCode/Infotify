package com.thecode.infotify.core.di

import com.thecode.infotify.core.domain.News
import com.thecode.infotify.framework.datasource.network.mapper.EntityMapper
import com.thecode.infotify.framework.datasource.network.mapper.NewsMapper
import com.thecode.infotify.framework.datasource.network.model.NewsObjectResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {

    @Singleton
    @Provides
    fun provideNewsResponseMapper(): EntityMapper<NewsObjectResponse, News> {
        return NewsMapper()
    }

}