package com.example.obssmovieapp_1.models // ktlint-disable package-name

import com.google.gson.annotations.SerializedName

class Movies(
    var page: Int? = null,
    @SerializedName("results")
    var moviesList: MutableList<Movie>? = null,
    @SerializedName("total_pages")
    var totalPages: Int? = null,
    @SerializedName("total_results")
    var totalResults: Int? = null

)

