package com.thecode.infotify.framework;

import androidx.annotation.NonNull;
import com.thecode.infotify.BuildConfig;
import com.thecode.infotify.http.service.ApiInterface;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient;

    @NonNull
    public static ApiInterface get() {

        if (okHttpClient == null)
            initOkHttp();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("newsapi.com")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(ApiInterface.class);
    }

    private static void initOkHttp() {
        int REQUEST_TIMEOUT = 60;

        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder();
        try {
            TLSSocketFactory tlsSocketFactory = new TLSSocketFactory();
            if (tlsSocketFactory.getTrustManager() != null) {
                httpClient = new OkHttpClient.Builder()
                        .sslSocketFactory(tlsSocketFactory, tlsSocketFactory.getTrustManager())
                        .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                        .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
            }
        } catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(interceptor);
        }

        okHttpClient = httpClient.build();
    }
}
