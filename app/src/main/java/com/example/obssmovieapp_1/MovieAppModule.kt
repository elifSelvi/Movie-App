package com.example.obssmovieapp_1 // ktlint-disable package-name

import android.content.Context
import com.example.obssmovieapp_1.data.MovieAppDatabase
import com.example.obssmovieapp_1.interfaces.MovieAPI
import com.example.obssmovieapp_1.interfaces.MovieSearchAPI
import com.example.obssmovieapp_1.interfaces.PopularMoviesAPI
import com.example.obssmovieapp_1.interfaces.SimilarMoviesAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
internal object MovieAppModule {

    @Provides
    fun getAPIKey(): String = "04fe442d09ffbcf5d13c4f67a248ad3c"


    @Provides
    fun getRetrofit(): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit
    }

    @Provides
    fun getMovieAPI(): MovieAPI {
        val api = getRetrofit().create(MovieAPI::class.java)
        return api
    }

    @Provides
    fun getPopularMoviesAPI(): PopularMoviesAPI {
        val api = getRetrofit().create(PopularMoviesAPI::class.java)
        return api
    }

    @Provides
    fun getSimilarMoviesAPI(): SimilarMoviesAPI {
        val api = getRetrofit().create(SimilarMoviesAPI::class.java)
        return api
    }

    @Provides
    fun getMovieSearchAPI(): MovieSearchAPI {
        val api = getRetrofit().create(MovieSearchAPI::class.java)
        return api
    }

    @Provides
    fun getDatabase(@ApplicationContext applicationContext: Context): MovieAppDatabase {
        return MovieAppDatabase.getDB(applicationContext) // .getDB(applicationContext)
    }
}
