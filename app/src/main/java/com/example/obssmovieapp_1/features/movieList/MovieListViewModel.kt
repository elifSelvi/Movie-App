package com.example.obssmovieapp_1.features.movieList // ktlint-disable package-name

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.obssmovieapp_1.PopularMoviesPagingSource
import com.example.obssmovieapp_1.data.MovieAppDatabase
import com.example.obssmovieapp_1.data.MovieEntity
import com.example.obssmovieapp_1.interfaces.PopularMoviesAPI
import com.example.obssmovieapp_1.models.Movie
import com.example.obssmovieapp_1.models.Movies
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    val apiKey: String,
    val popularMoviesApi: PopularMoviesAPI,
    val db: MovieAppDatabase
) : ViewModel() {

    val listData = Pager(PagingConfig(pageSize = 1)) {
        PopularMoviesPagingSource(apiKey, popularMoviesApi, db)
    }.flow.cachedIn(viewModelScope)

    var currPopularMovieResponse: Movies? = null


    val popularsLiveData = MutableLiveData<Movies>()

    // handle paging
    var popularMoviePageNumber = 1
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    /*
    init {

        fetchPopularMovies()
    }
     */

    // get current page data response, return response
    suspend fun getPopularMovies(): Response<Movies>? {
        val response = try {
            popularMoviesApi.getPopularMovies(apiKey, popularMoviePageNumber)
        } catch (e: Exception) {
            return null
        }

        if (response.isSuccessful) {
            return response
        }

        return null
    }

    // after getting the response from the current page, increase page for the next process,
    // add new response body to old response body and post value of the total response body
    // into the livedata
    fun fetchPopularMovies() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = getPopularMovies()

            if (response != null) {
                response.body()?.let { result ->

                    result.moviesList?.let {
                        for (i in it) {
                            Log.e("New Movie at page " + popularMoviePageNumber.toString(), i.title!!)
                        }
                    }

                    popularMoviePageNumber++

                    result.moviesList?.let {
                        for (i in it) {
                            i.isFavorite = db.getDao().getAll(i.id!!)
                        }
                    }

                    if (currPopularMovieResponse == null) {
                        currPopularMovieResponse = result
                    } else {
                        val oldMovies = currPopularMovieResponse?.moviesList
                        val newMovies = result.moviesList
                        oldMovies?.addAll(newMovies!!)
                    }
                    if (currPopularMovieResponse == null) {
                        popularsLiveData.postValue(result)
                    }

                    popularsLiveData.postValue(currPopularMovieResponse)
                }
            } else {
                popularsLiveData.postValue(currPopularMovieResponse)
            }
        }
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

    fun favoritesChanged(movies: List<Movie>) {
        viewModelScope.launch(Dispatchers.IO) {
            movies.let {
                for (i in it) {
                    i.isFavorite = db.getDao().getAll(i.id!!)
                }
            }
        }
    }
}
