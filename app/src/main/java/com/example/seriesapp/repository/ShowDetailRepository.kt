package com.example.seriesapp.repository

import com.example.seriesapp.models.TvShow

class ShowDetailRepository(initialShows: List<TvShow>) {

    private val shows = initialShows.toMutableList()

    fun getShowById(showId: Int): TvShow? {
        return shows.find { it.id == showId }?.copy()
    }

    fun toggleFavorite(showId: Int) {
        shows.find { it.id == showId }?.isFavorite =
            !(shows.find { it.id == showId }?.isFavorite ?: false)
    }

    fun markSeasonWatched(showId: Int) {
        shows.find { it.id == showId }?.let { show ->
            show.seasonsWatched = (show.seasonsWatched + 1).coerceAtMost(show.totalSeasons)
        }
    }
}