package com.example.obssmovieapp_1.data // ktlint-disable package-name

import androidx.lifecycle.LiveData
import androidx.room.* // ktlint-disable no-wildcard-imports

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(movie: MovieEntity)

    @Update
    suspend fun update(movie: MovieEntity)

    @Delete
    suspend fun delete(movie: MovieEntity)

    @Query("Select Count(*) from `favorite-movies` Where id = :givenId")
    fun getAll(givenId: Int): Int // LiveData<>

    @Query("Select * from `favorite-movies`")
    fun getAllFavorites(): List<MovieEntity> // LiveData<>
}
