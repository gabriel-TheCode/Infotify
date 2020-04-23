package com.thecode.infotify.interfaces

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

abstract class NetworkConnectionInterceptor : Interceptor {
    abstract val isInternetAvailable: Boolean

    abstract fun onInternetUnavailable()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val cacheBuilder = CacheControl.Builder()
        cacheBuilder.maxAge(0, TimeUnit.SECONDS)
        cacheBuilder.maxStale(365, TimeUnit.DAYS)
        val cacheControl = cacheBuilder.build()
        var request = chain.request()
        if (isInternetAvailable) {
            request = request.newBuilder()
                .cacheControl(cacheControl)
                .build()
        }
        val originalResponse = chain.proceed(request)
        return if (isInternetAvailable) {
            val maxAge = 60 // read from cache
            originalResponse.newBuilder()
                .header("Cache-Control", "public, max-age=$maxAge")
                .build()
        } else {
            onInternetUnavailable()
            val maxStale = 60 * 60 * 24 * 28 // tolerate 4-weeks stale
            originalResponse.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                .build()
        }
    }
}