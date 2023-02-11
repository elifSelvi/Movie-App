package com.example.obssmovieapp_1.data // ktlint-disable package-name

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// @TypeConverters(ResultsTypeConverter::class)
@Database(entities = arrayOf(MovieEntity::class), version = 1)
abstract class MovieAppDatabase : RoomDatabase() {
    abstract fun getDao(): MovieDao

    companion object {

        @Volatile
        private var INSTANCE: MovieAppDatabase? = null

        fun getDB(context: Context): MovieAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieAppDatabase::class.java,
                    "favorite-movies-database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
