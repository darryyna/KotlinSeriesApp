package com.example.seriesapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seriesapp.models.TvShow
import com.example.seriesapp.models.allShows
import com.example.seriesapp.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SearchViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    sealed interface State {
        data object Loading : State
        data class Success(
            val searchText: String = "",
            val minSeasons: Int = 0,
            val sortByRating: Boolean = false,
            val filteredShows: List<TvShow> = emptyList()
        ) : State
    }

    sealed interface Event {
        data class SearchTextChanged(val text: String) : Event
        data class MinSeasonsChanged(val seasons: Int) : Event
        data class SortByRatingToggled(val enabled: Boolean) : Event
        data class ToggleFavorite(val showId: Int) : Event
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    init {
        _state.value = State.Success(filteredShows = allShows)
        viewModelScope.launch {
            favoritesRepository.favoriteShows.collect { favoriteShows ->
                _state.update { currentState ->
                    if (currentState is State.Success) {
                        currentState.copy(
                            filteredShows = currentState.filteredShows.map { show ->
                                show.copy(isFavorite = favoriteShows.any { it.id == show.id })
                            }
                        )
                    } else currentState
                }
            }
        }
    }
    fun handleEvent(event: Event) {
        when (val currentState = _state.value) {
            is State.Success -> {
                when (event) {
                    is Event.SearchTextChanged -> {
                        _state.value = currentState.copy(
                            searchText = event.text,
                            filteredShows = filterShows(
                                shows = allShows,
                                searchText = event.text,
                                minSeasons = currentState.minSeasons,
                                sortByRating = currentState.sortByRating
                            )
                        )
                    }
                    is Event.MinSeasonsChanged -> {
                        _state.value = currentState.copy(
                            minSeasons = event.seasons,
                            filteredShows = filterShows(
                                shows = allShows,
                                searchText = currentState.searchText,
                                minSeasons = event.seasons,
                                sortByRating = currentState.sortByRating
                            )
                        )
                    }
                    is Event.SortByRatingToggled -> {
                        _state.value = currentState.copy(
                            sortByRating = event.enabled,
                            filteredShows = filterShows(
                                shows = allShows,
                                searchText = currentState.searchText,
                                minSeasons = currentState.minSeasons,
                                sortByRating = event.enabled
                            )
                        )
                    }
                    is Event.ToggleFavorite -> {
                        allShows.find { it.id == event.showId }?.let {
                            favoritesRepository.updateFavorites(
                                it.copy(isFavorite = !it.isFavorite)
                            )
                        }
                    }
                }
            }
            State.Loading -> {}
        }
    }

    private fun filterShows(
        shows: List<TvShow>,
        searchText: String,
        minSeasons: Int,
        sortByRating: Boolean
    ): List<TvShow> {
        return shows
            .filter { it.title.contains(searchText, ignoreCase = true) }
            .filter { it.totalSeasons >= minSeasons }
            .let { if (sortByRating) it.sortedByDescending { it.rating } else it }
    }
}