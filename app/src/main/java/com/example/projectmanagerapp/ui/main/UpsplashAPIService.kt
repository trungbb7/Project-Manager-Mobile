package com.example.projectmanagerapp.ui.main

import retrofit2.http.GET
import retrofit2.http.Query

interface UpsplashAPIService {

    @GET("photos/random")
    suspend fun getRandomPhotos(
        @Query("client_id") clientId: String,
        @Query("count") count: Int = 30,
        @Query("orientation") orientation: String = "landscape"
    ): List<UnsplashPhoto>
}