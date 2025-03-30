package com.example.seriesapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seriesapp.models.TvShow
import com.example.seriesapp.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: FavoritesRepository
) : ViewModel() {
    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state

    init {
        viewModelScope.launch {
            repository.favoriteShows.collect { shows ->
                _state.update { it.copy(favoriteShows = shows) }
            }
        }
    }

    fun handleEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.ToggleFavorite -> {
                val currentShows = _state.value.favoriteShows
                currentShows.find { it.id == event.showId }?.let { show ->
                    repository.updateFavorites(show.copy(isFavorite = !show.isFavorite))
                }
            }
        }
    }
}

data class FavoritesState(
    val favoriteShows: List<TvShow> = emptyList()
)

sealed interface FavoritesEvent {
    data class ToggleFavorite(val showId: Int) : FavoritesEvent
}
