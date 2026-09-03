package com.thecode.infotify.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.thecode.infotify.BuildConfig
import com.thecode.infotify.data.remote.infotify.InfotifyApi
import com.thecode.infotify.utils.AppConstants
import com.thecode.infotify.utils.AppConstants.REQUEST_TIMEOUT_SECONDS
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
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
    fun provideGson(): Gson = GsonBuilder().create()

    /**
     * A local disk cache on top of the server-side one. The proxy already collapses many
     * users into one upstream credit; this avoids re-fetching on every rotation and tab
     * switch on a single device.
     */
    @Singleton
    @Provides
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient = OkHttpClient.Builder()
        .cache(Cache(context.cacheDir.resolve("http_cache"), HTTP_CACHE_BYTES))
        .connectTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addNetworkInterceptor { chain ->
            chain.proceed(chain.request())
                .newBuilder()
                .header("Cache-Control", "public, max-age=$CACHE_MAX_AGE_SECONDS")
                .removeHeader("Pragma")
                .build()
        }
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }
                )
            }
        }
        .build()

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(AppConstants.INFOTIFY_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    @Singleton
    @Provides
    fun provideInfotifyApi(retrofit: Retrofit): InfotifyApi =
        retrofit.create(InfotifyApi::class.java)

    private const val HTTP_CACHE_BYTES = 10L * 1024 * 1024
    private const val CACHE_MAX_AGE_SECONDS = 300
}
