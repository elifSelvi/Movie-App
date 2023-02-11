package com.example.obssmovieapp_1.features.favoritesList // ktlint-disable package-name

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.obssmovieapp_1.DetailedMovie
import com.example.obssmovieapp_1.data.MovieAppDatabase
import com.example.obssmovieapp_1.data.MovieEntity
import com.example.obssmovieapp_1.interfaces.MovieAPI
import com.example.obssmovieapp_1.models.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    val apiKey: String,
    val movieApi: MovieAPI,
    val db: MovieAppDatabase
) : ViewModel() {

    val favsLiveData = MutableLiveData<MutableList<Movie>>()

    suspend fun getMovieDetails(id: Int): Response<DetailedMovie>? {
        val response = try {
            movieApi.getMovie(id, apiKey)
        } catch (e: Exception) {
            return null
        }

        if (response.isSuccessful) {
            return response
        }

        return null
    }

    fun fetchFavoriteMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val allFavorites = db.getDao().getAllFavorites()
            val movieList = mutableListOf<Movie>()

            for (i in allFavorites) {
                val response = getMovieDetails(i.id!!)
                if (response != null) {
                    movieList.add(turnToMovieItem(response.body()!!))
                }
            }

            favsLiveData.postValue(movieList)
        }
    }

    fun turnToMovieItem(detailed: DetailedMovie): Movie {
        val newMovie = Movie()
        newMovie.id = detailed.id
        newMovie.title = detailed.title
        newMovie.releaseDate = detailed.releaseDate
        newMovie.voteAverage = detailed.voteAverage
        newMovie.isFavorite = 1
        newMovie.backdropPath = detailed.backdropPath
        return newMovie
    }
    fun addToFavorites(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            db.getDao().add(MovieEntity(movie.title, movie.id))
            movie.isFavorite = 1
        }
    }

    fun removeFromFavorites(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            db.getDao().delete(MovieEntity(movie.title, movie.id))
            movie.isFavorite = null
        }
    }
}
