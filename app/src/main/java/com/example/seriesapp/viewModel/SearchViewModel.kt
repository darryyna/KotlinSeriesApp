package com.example.seriesapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seriesapp.models.TvShow
import com.example.seriesapp.repository.FavoritesRepository
import com.example.seriesapp.repository.TvShowsDataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    sealed class State {
        object Loading : State()
        data class Success(
            val searchText: String = "",
            val minSeasons: Int = 1,
            val sortByRating: Boolean = false,
            val filteredShows: List<TvShow> = emptyList()
        ) : State()
    }

    val state = MutableStateFlow<State>(State.Loading)

    init {
        viewModelScope.launch {
            favoritesRepository.fetchTvShows()
            val shows = (favoritesRepository.allShowsState.value as? TvShowsDataState.Success)?.tvShows.orEmpty()
            state.value = State.Success(filteredShows = shows)
        }
    }

    fun handleEvent(event: Event) {
        when (val current = state.value) {
            is State.Success -> {
                when (event) {
                    is Event.SearchTextChanged -> updateState(current.copy(searchText = event.text))
                    is Event.MinSeasonsChanged -> updateState(current.copy(minSeasons = event.minSeasons))
                    is Event.SortByRatingToggled -> updateState(current.copy(sortByRating = event.enabled))
                    is Event.ToggleFavorite -> {
                        viewModelScope.launch {
                            val updated = event.show.copy(isFavorite = !event.show.isFavorite)
                            favoritesRepository.updateShowLocally(updated)
                            updateFilteredShows()
                        }
                    }
                }
            }
            else -> {}
        }
    }

    private fun updateFilteredShows() {
        val current = state.value as? State.Success ?: return
        val all = (favoritesRepository.allShowsState.value as? TvShowsDataState.Success)?.tvShows.orEmpty()
        val filtered = all
            .filter { it.title.contains(current.searchText, ignoreCase = true) }
            //.filter { it.seasons >= current.minSeasons }
            .let { list ->
                if (current.sortByRating) list.sortedByDescending { it.rating } else list
            }
        updateState(current.copy(filteredShows = filtered))
    }

    private fun updateState(newState: State.Success) {
        state.value = newState
    }

    sealed class Event {
        data class SearchTextChanged(val text: String) : Event()
        data class MinSeasonsChanged(val minSeasons: Int) : Event()
        data class SortByRatingToggled(val enabled: Boolean) : Event()
        data class ToggleFavorite(val show: TvShow) : Event()
    }
}