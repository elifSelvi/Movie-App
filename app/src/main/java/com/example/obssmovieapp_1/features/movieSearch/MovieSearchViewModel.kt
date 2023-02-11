package com.example.obssmovieapp_1.features.movieSearch // ktlint-disable package-name

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.obssmovieapp_1.PopularMoviesPagingSource
import com.example.obssmovieapp_1.SearchPagingSource
import com.example.obssmovieapp_1.data.MovieAppDatabase
import com.example.obssmovieapp_1.data.MovieEntity
import com.example.obssmovieapp_1.interfaces.MovieSearchAPI
import com.example.obssmovieapp_1.models.Movie
import com.example.obssmovieapp_1.models.Movies
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MovieSearchViewModel @Inject constructor(
    val apiKey: String,
    val searchApi: MovieSearchAPI,
    val db: MovieAppDatabase
) : ViewModel() {

    var myQuery = ""
    var pagingSource: SearchPagingSource? = null
    get(){
       if(field == null || field?.invalid == true){
           field = SearchPagingSource(apiKey,searchApi,myQuery,db)
       }

        return field
    }
    val searchData = Pager(PagingConfig(pageSize = 1)) {
      pagingSource!!
    }.flow.cachedIn(viewModelScope) // could have been .livedata instead of .flow too

    var currSearchMovieResponse: Movies? = null

    val searchedLiveData = MutableLiveData<Movies>()

    // handle paging
    var searchMoviePageNumber = 1
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    fun submitQuery(query: String){
        myQuery = query
        pagingSource?.invalidate()
    }
    suspend fun getMoviesByQuery(query: String): Response<Movies>? {
        val response = try {
            searchApi.getSearchMovie(apiKey, query, searchMoviePageNumber)
        } catch (e: Exception) {
            return null
        }

        if (response.isSuccessful) {
            return response
        }

        return null
    }

    fun fetchMoviesByQuery(query: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val response = getMoviesByQuery(query)

            if (response != null) {
                response.body()?.moviesList?.let {
                    for (movie in it) {
                        Log.e("New movie named: ", movie.title!!)
                    }
                }

                response.body()?.let { result ->
                    searchMoviePageNumber++

                    result.moviesList?.let {
                        for (i in it) {
                            i.isFavorite = db.getDao().getAll(i.id!!)
                        }
                    }

                    if (currSearchMovieResponse == null) {
                        currSearchMovieResponse = result
                    } else {
                        val oldMovies = currSearchMovieResponse?.moviesList
                        val newMovies = result.moviesList
                        oldMovies?.addAll(newMovies!!)
                    }
                    if (currSearchMovieResponse == null) {
                        searchedLiveData.postValue(result)
                    }

                    searchedLiveData.postValue(currSearchMovieResponse)
                }
            } else {
                searchedLiveData.postValue(currSearchMovieResponse)
            }
        }
    }

    fun clearData() {
        currSearchMovieResponse = null
        searchMoviePageNumber = 1
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
