package com.thecode.infotify.services

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.thecode.infotify.InfotifyApp.Companion.instance
import com.thecode.infotify.interfaces.ApiInterface
import com.thecode.infotify.interfaces.NetworkConnectionInterceptor
import com.thecode.infotify.utils.NetworkChangeReceiver.Companion.connectivityReceiverListener
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    var mContext: Context? = null
    var mCoreServiceUrl: String? = null
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
            .addInterceptor(object : NetworkConnectionInterceptor() {
                override fun isInternetAvailable(): Boolean {
                    return instance!!.isInternetAvailable
                }

                override fun onInternetUnavailable() {
                    if (connectivityReceiverListener != null) {
                        connectivityReceiverListener!!.onInternetUnavailable()
                    }
                }
            })
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
        return getRetrofitInstance(mContext, coreServiceUrl).create(
            ApiInterface::class.java
        )
    }
}