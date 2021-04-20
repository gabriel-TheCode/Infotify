package com.thecode.infotify.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.thecode.infotify.BuildConfig
import com.thecode.infotify.application.InfotifySharedPref
import com.thecode.infotify.core.network.InfotifyRemoteDataSourceImpl
import com.thecode.infotify.framework.datasource.NewsApiRemoteService
import com.thecode.infotify.framework.datasource.NewsApiRemoteServiceImpl
import com.thecode.infotify.framework.datasource.network.api.NewsApi
import com.thecode.infotify.framework.datasource.network.mapper.NewsMapper
import com.thecode.infotify.framework.datasource.network.mapper.SourceMapper
import com.thecode.infotify.utils.AppConstants
import com.thecode.infotify.utils.AppConstants.REQUEST_TIMEOUT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder().create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit.Builder {
        return Retrofit.Builder()
            //.baseUrl(BuildConfig.BASE_URL)
            .baseUrl(AppConstants.NEWSAPI_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(getOkHttpService())
    }

    @Singleton
    @Provides
    fun provideWhoService(retrofit: Retrofit.Builder): NewsApi {
        return retrofit
            .build()
            .create(NewsApi::class.java)
    }

    @Singleton
    @Provides
    fun provideInfotifyRemoteService(
        api: NewsApi
    ): NewsApiRemoteService {
        return NewsApiRemoteServiceImpl(api)
    }

    @Singleton
    @Provides
    fun provideRemoteDataSource(
        whoRemoteService: NewsApiRemoteService,
        sharedPref: InfotifySharedPref,
        newsMapper: NewsMapper,
        sourceMapper: SourceMapper,
    ): InfotifyRemoteDataSourceImpl {
        return InfotifyRemoteDataSourceImpl(
            whoRemoteService,
            sharedPref,
            newsMapper,
            sourceMapper,
        )
    }

    private fun getOkHttpService(): OkHttpClient {
        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
                    .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            httpClient.addInterceptor(interceptor)
        }

        return httpClient.build()
    }

}