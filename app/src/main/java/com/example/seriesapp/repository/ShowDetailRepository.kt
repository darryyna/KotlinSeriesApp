package com.example.seriesapp.repository

import com.example.seriesapp.models.TvShow
import kotlinx.coroutines.flow.StateFlow

class ShowDetailRepository(
    private val allShowsState: StateFlow<TvShowsDataState>
) {
    fun getShowById(showId: Int): TvShow? {
        val currentShows = (allShowsState.value as? TvShowsDataState.Success)?.tvShows
        return currentShows?.find { it.id == showId }?.copy()
    }
}