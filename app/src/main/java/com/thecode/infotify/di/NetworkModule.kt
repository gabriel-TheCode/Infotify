package com.thecode.infotify.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.thecode.infotify.BuildConfig
import com.thecode.infotify.data.remote.NewsApiRemoteService
import com.thecode.infotify.data.remote.NewsApiRemoteServiceImpl
import com.thecode.infotify.data.remote.api.NewsApi
import com.thecode.infotify.data.remote.datasource.InfotifyRemoteDataSourceImpl
import com.thecode.infotify.data.remote.mapper.NewsMapper
import com.thecode.infotify.utils.AppConstants
import com.thecode.infotify.utils.AppConstants.REQUEST_TIMEOUT
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
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
        apiService: NewsApiRemoteService,
        newsMapper: NewsMapper
    ): InfotifyRemoteDataSourceImpl {
        return InfotifyRemoteDataSourceImpl(
            apiService,
            newsMapper
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
        httpClient.addInterceptor(BasicAuthInterceptor())

        return httpClient.build()
    }

    class BasicAuthInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val newUrl =
                request.url.newBuilder().addQueryParameter("apiKey", BuildConfig.API_KEY).build()
            val newRequest = request.newBuilder().url(newUrl).build()
            return chain.proceed(newRequest)
        }
    }
}
