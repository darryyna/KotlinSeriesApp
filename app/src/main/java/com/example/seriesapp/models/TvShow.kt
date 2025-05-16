package com.example.seriesapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tv_shows")
data class TvShow(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val imageName: String,
    val genre: String,
    val rating: Float,
    var seasonsWatched: Int,
    val totalSeasons: Int,
    var isFavorite: Boolean = false,
    val nextEpisodeDate: String? = null
)

val testShows = listOf(
    TvShow(1, "Stranger Things", "st", "Sci-Fi", 4.8f, 2, 4, false),
    TvShow(2, "Game of Thrones", "got", "Fantasy", 4.5f, 4, 8, false),
    TvShow(3, "Day of the Jackal", "dj", "Drama", 4.9f, 1, 2, true, "May 19, 2025"),
    TvShow(4, "Breaking Bad", "br", "Crime", 4.7f, 5, 5, true),
)