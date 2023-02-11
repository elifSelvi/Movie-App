package com.example.obssmovieapp_1.interfaces // ktlint-disable package-name

import com.example.obssmovieapp_1.models.Movies
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieSearchAPI {

    // search/keyword?
    @GET("search/movie") // search/movie?api_key={api_key}&query=Jack+Reacher
    suspend fun getSearchMovie(
        @Query("api_key") apiKey: String,
        @Query("query") searchString: String,
        @Query("page") pageNum: Int
    ): Response<Movies>
}
