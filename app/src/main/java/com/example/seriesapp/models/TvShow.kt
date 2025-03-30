package com.example.seriesapp.models

import com.example.seriesapp.R

data class TvShow(
    val id: Int,
    val title: String,
    val imageResId: Int,
    val genre: String,
    val rating: Float,
    var seasonsWatched: Int,
    val totalSeasons: Int,
    var isFavorite: Boolean = false,
    val nextEpisodeDate: String? = null
)

val initialShows = listOf(
    TvShow(1, "Stranger Things", R.drawable.st, "Sci-Fi", 4.8f, 2, 4, false),
    TvShow(2, "Game of Thrones", R.drawable.got, "Fantasy", 4.5f, 4, 8, false),
    TvShow(3, "Day of the Jackal", R.drawable.dj, "Drama", 4.9f, 1, 2, true, "May 19, 2025"),
    TvShow(4, "Breaking Bad", R.drawable.br, "Crime", 4.7f, 5, 5, true),
)

val recommendedShowsSet = setOf(
    TvShow(5, "The Witcher", R.drawable.witcher, "Fantasy", 4.6f, 0, 3),
    TvShow(6, "Chernobyl", R.drawable.chernobyl, "Drama", 4.9f, 0, 1),
    TvShow(7, "Westworld", R.drawable.westworld, "Sci-Fi", 4.4f, 0, 4),
    TvShow(8, "Better Call Saul", R.drawable.bcs, "Crime", 4.8f, 0, 6),
    TvShow(9, "Shadow and Bone", R.drawable.shadowandbone, "Fantasy", 4.5f, 1, 2),
    TvShow(10, "The Mandalorian", R.drawable.mandalorian, "Sci-Fi", 4.7f, 1, 3),

    TvShow(11, "The Crown", R.drawable.thecrown, "Drama", 4.8f, 2, 5),
    TvShow(12, "Peaky Blinders", R.drawable.peakyblinders, "Drama", 4.7f, 3, 6),
    TvShow(13, "Mindhunter", R.drawable.mindhunter, "Drama", 4.6f, 2, 2),

    TvShow(14, "Black Mirror", R.drawable.blackmirror, "Sci-Fi", 4.8f, 4, 5),
    TvShow(15, "The Expanse", R.drawable.expanse, "Sci-Fi", 4.5f, 3, 6),

    TvShow(16, "Narcos", R.drawable.narcos, "Crime", 4.6f, 3, 6),
    TvShow(17, "True Detective", R.drawable.truedetective, "Crime", 4.7f, 2, 4),
    TvShow(18, "Money Heist", R.drawable.moneyheist, "Crime", 4.6f, 5, 5),
    TvShow(19, "Sherlock", R.drawable.sherlock, "Crime", 4.9f, 4, 4)
)

val allShows = (initialShows + recommendedShowsSet.toList())
    .distinctBy { it.id }