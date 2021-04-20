package com.thecode.infotify.services

import android.annotation.SuppressLint
import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.thecode.infotify.interfaces.ApiInterface
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by TEKOMBO Gabriel <tekombo.gabriel@gmail.com/>
 */
@SuppressLint("StaticFieldLeak")
object RetrofitClient {
    private lateinit var mContext: Context
    private lateinit var mCoreServiceUrl: String
    private fun getRetrofitInstance(
        context: Context,
        coreServiceUrl: String
    ): Retrofit {
        mContext = context
        mCoreServiceUrl = coreServiceUrl
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
        httpClient.readTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)
            .build()
        httpClient.addInterceptor(logging)
            .addNetworkInterceptor(StethoInterceptor())
        return Retrofit.Builder()
            .baseUrl(mCoreServiceUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }

    /**
     * Get API Service
     *
     * @return API Service
     */
    fun getApiService(
        mContext: Context,
        coreServiceUrl: String
    ): ApiInterface {
        return getRetrofitInstance(mContext, coreServiceUrl)
            .create(ApiInterface::class.java)
    }
}