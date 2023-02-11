package com.example.obssmovieapp_1

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.obssmovieapp_1.data.MovieAppDatabase
import com.example.obssmovieapp_1.interfaces.PopularMoviesAPI
import com.example.obssmovieapp_1.models.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PopularMoviesPagingSource(val apiKey: String, val popularsApi: PopularMoviesAPI, val db: MovieAppDatabase) :
    PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val currPage = params.key ?: 1
            val response = popularsApi.getPopularMovies(apiKey, currPage)
            val data = response.body()?.moviesList!!

            withContext(Dispatchers.IO) {
                data.let {
                    for (i in it) {
                        i.isFavorite = db.getDao().getAll(i.id!!)
                    }
                }
            }

            val responseData = mutableListOf<Movie>()
            responseData.addAll(data)

            LoadResult.Page(
                data = responseData,
                prevKey = if (currPage == 1) null else -1,
                nextKey = if (data.size == 0) null else currPage.plus(1)

            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
