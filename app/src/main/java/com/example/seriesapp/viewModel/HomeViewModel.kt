package com.example.seriesapp.viewModel

import androidx.lifecycle.ViewModel
import com.example.seriesapp.models.TvShow
import com.example.seriesapp.models.allShows
import com.example.seriesapp.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

open class HomeViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    open val state: StateFlow<HomeState> = _state

    open fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ToggleFavorite -> toggleFavorite(event.showId)
        }
    }

    private fun toggleFavorite(showId: Int) {
        _state.update { currentState ->
            val updatedShows = currentState.tvShows.map { show ->
                if (show.id == showId) {
                    val updatedShow = show.copy(isFavorite = !show.isFavorite)
                    favoritesRepository.updateFavorites(updatedShow)
                    updatedShow
                } else show
            }
            currentState.copy(tvShows = updatedShows)
        }
    }
}

data class HomeState(
    val tvShows: List<TvShow> = allShows
)

sealed class HomeEvent {
    data class ToggleFavorite(val showId: Int) : HomeEvent()
}