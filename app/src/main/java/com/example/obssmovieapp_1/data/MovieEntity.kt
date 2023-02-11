package com.example.obssmovieapp_1.data // ktlint-disable package-name

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite-movies")
class MovieEntity(
    var name: String? = null,
    @PrimaryKey(autoGenerate = false)
    var id: Int? = null
)
