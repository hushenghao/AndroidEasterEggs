package com.dede.android_eggs.api

import com.dede.android_eggs.BuildConfig
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiManager {

    private const val GITHUB_API_URL = "https://api.github.com/"

    val retrofit: Retrofit by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(getLoggingInterceptor())
            .build()
        val moshi = Moshi.Builder()
            .build()
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(GITHUB_API_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    inline fun <reified T : Any> create(): T = retrofit.create(T::class.java)

    private fun getLoggingInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        return interceptor
    }

}