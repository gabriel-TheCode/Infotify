package com.thecode.infotify.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Appends the NewsData.io key to every request.
 *
 * The key comes from BuildConfig, which is populated from local.properties (gitignored)
 * or a CI secret — it is never written into a versioned file. Long term this whole class
 * disappears: the key belongs on a proxy, not in the APK.
 */
class ApiKeyInterceptor @Inject constructor(
    private val apiKey: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.newBuilder()
            .addQueryParameter("apikey", apiKey)
            .build()
        return chain.proceed(request.newBuilder().url(url).build())
    }
}
