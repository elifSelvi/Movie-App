package com.example.obssmovieapp_1.interfaces // ktlint-disable package-name

import com.example.obssmovieapp_1.DetailedMovie
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieAPI {

    @GET("movie/{id}") // search/movie?api_key={api_key}&query=Jack+Reacher
    suspend fun getMovie(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String
    ): Response<DetailedMovie>
}
