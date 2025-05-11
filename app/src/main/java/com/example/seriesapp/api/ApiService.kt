package com.example.seriesapp.api

import com.example.seriesapp.models.TvShow
import com.example.seriesapp.models.User
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("v3/caae0a81-dfaf-42c5-a5d3-22dc345673a5")
    suspend fun getTvShows(): Response<List<TvShow>>

    @GET("v3/aaaa7dfe-6b77-4921-a938-892266bda061")
    suspend fun getUsers(): Response<List<User>>
}