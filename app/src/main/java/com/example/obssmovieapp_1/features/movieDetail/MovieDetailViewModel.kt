package com.example.obssmovieapp_1.features.movieDetail // ktlint-disable package-name

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.obssmovieapp_1.DetailedMovie
import com.example.obssmovieapp_1.data.MovieAppDatabase
import com.example.obssmovieapp_1.data.MovieEntity
import com.example.obssmovieapp_1.interfaces.MovieAPI
import com.example.obssmovieapp_1.interfaces.SimilarMoviesAPI
import com.example.obssmovieapp_1.models.Movie
import com.example.obssmovieapp_1.models.Movies
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    val apiKey: String,
    val detailApi: MovieAPI,
    val similarsApi: SimilarMoviesAPI,
    val db: MovieAppDatabase
) : ViewModel() {

    // var isFavoriteMovie = 0
    val movieDetailsLiveData = MutableLiveData<DetailedMovie>()
    val similarsLiveData = MutableLiveData<Movies>()

    suspend fun getMovieDetails(movie: Movie): Response<DetailedMovie>? {
        val response = try {
            detailApi.getMovie(movie.id!!, apiKey)
        } catch (e: Exception) {
            Log.e("exc", e.toString())
            return null
        }

        if (response.isSuccessful) {
            return response
        }

        return null
    }

    fun isInDB(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            movie.isFavorite = db.getDao().getAll(movie.id!!)
        }
    }

    fun fetchMovieDetails(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = getMovieDetails(movie)
            if (response != null) {
                movieDetailsLiveData.postValue(response.body())
            }
        }
    }

    suspend fun getSimilarMovies(movie: Movie): Response<Movies>? {
        val response = try {
            similarsApi.getSimilarMovies(movie.id!!, apiKey, 1)
        } catch (e: Exception) {
            Log.e("exc", e.toString())
            return null
        }

        if (response.isSuccessful) {
            return response
        }

        return null
    }

    fun fetchSimilarMovies(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = getSimilarMovies(movie)

            if (response != null) {
                response.body()?.let { result ->

                    result.moviesList?.let {
                        for (i in it) {
                            i.isFavorite = db.getDao().getAll(i.id!!)
                        }
                    }
                }

                similarsLiveData.postValue(response.body())
            }
        }
    }

    fun addToFavorites(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            movie.isFavorite = 1
            db.getDao().add(MovieEntity(movie.title, movie.id))
        }
    }

    fun removeFromFavorites(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            movie.isFavorite = null
            db.getDao().delete(MovieEntity(movie.title, movie.id))
        }
    }
}
