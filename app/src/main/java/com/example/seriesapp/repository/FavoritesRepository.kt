package com.example.seriesapp.repository

import com.example.seriesapp.models.TvShow
import com.example.seriesapp.models.allShows
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FavoritesRepository {
    private val _allShows = MutableStateFlow(allShows)
    private val _favoriteShows = MutableStateFlow<List<TvShow>>(emptyList())
    val favoriteShows: StateFlow<List<TvShow>> = _favoriteShows

    init {
        updateFavorites()
    }

    fun updateFavorites(show: TvShow) {
        _allShows.value = _allShows.value.map {
            if (it.id == show.id) show else it
        }
        updateFavorites()
    }

    fun updateFavorites() {
        _favoriteShows.value = _allShows.value.filter { it.isFavorite }
    }
}