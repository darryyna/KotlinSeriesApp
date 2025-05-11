package com.example.seriesapp.repository

import com.example.seriesapp.api.RetrofitClient
import com.example.seriesapp.models.TvShow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

sealed class TvShowsDataState {
    object Loading : TvShowsDataState()
    data class Success(val tvShows: List<TvShow>) : TvShowsDataState()
    data class Error(val message: String) : TvShowsDataState()
}

class FavoritesRepository {
    private val _allShowsState = MutableStateFlow<TvShowsDataState>(TvShowsDataState.Loading)
    val allShowsState: StateFlow<TvShowsDataState> = _allShowsState

    private val _favoriteShows = MutableStateFlow<List<TvShow>>(emptyList())
    val favoriteShows: StateFlow<List<TvShow>> = _favoriteShows

    suspend fun fetchTvShows() {
        _allShowsState.value = TvShowsDataState.Loading
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.tvShowsApiService.getTvShows()
            }

            if (response.isSuccessful && response.body() != null) {
                val fetchedShows = response.body()!!
                _allShowsState.value = TvShowsDataState.Success(fetchedShows)
                updateFavorites()
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                _allShowsState.value = TvShowsDataState.Error("Failed to fetch TV shows: $errorMessage")
            }
        } catch (e: Exception) {
            _allShowsState.value = TvShowsDataState.Error("Network error: ${e.message}")
        }
    }

    suspend fun updateShowLocally(updatedShow: TvShow) {
        val currentAllShows = (_allShowsState.value as? TvShowsDataState.Success)?.tvShows?.toMutableList()
        currentAllShows?.let { list ->
            val index = list.indexOfFirst { it.id == updatedShow.id }
            if (index != -1) {
                list[index] = updatedShow
                _allShowsState.value = TvShowsDataState.Success(list)
                updateFavorites()
            }
        }
    }

    suspend fun updateFavorites() {
        val currentAllShows = (_allShowsState.value as? TvShowsDataState.Success)?.tvShows
        currentAllShows?.let { shows ->
            _favoriteShows.value = shows.filter { it.isFavorite }
        }
    }
}