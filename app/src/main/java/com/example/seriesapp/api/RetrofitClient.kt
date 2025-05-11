package com.example.seriesapp.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://run.mocky.io/"

    private const val TV_SHOWS_PATH = "v3/caae0a81-dfaf-42c5-a5d3-22dc345673a5"
    private const val USERS_PATH = "v3/aaaa7dfe-6b77-4921-a938-892266bda061"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val tvShowsApiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val usersApiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}