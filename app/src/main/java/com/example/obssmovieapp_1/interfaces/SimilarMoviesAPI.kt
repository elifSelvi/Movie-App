package com.example.obssmovieapp_1.interfaces

import com.example.obssmovieapp_1.models.Movies
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SimilarMoviesAPI {

    @GET("movie/{id}/similar")
    suspend fun getSimilarMovies(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<Movies>
}