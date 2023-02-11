package com.example.obssmovieapp_1.interfaces // ktlint-disable package-name

import com.example.obssmovieapp_1.models.Movies
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PopularMoviesAPI {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") pageNum: Int
    ): Response<Movies>
}
