package com.example.seriesapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seriesapp.models.TvShow
import com.example.seriesapp.repository.FavoritesRepository
import com.example.seriesapp.repository.ShowDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class ShowDetailViewModel(
    private val repository: ShowDetailRepository,
    private val favoritesRepository: FavoritesRepository,
    private val showId: Int
) : ViewModel() {
    private val _state = MutableStateFlow(ShowDetailState())
    open val state: StateFlow<ShowDetailState> = _state

    init {
        loadShow()
    }

    open fun loadShow() {
        if (_state.value.show != null) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isError = false) }
            try {
                val show = repository.getShowById(showId)
                if (show != null) {
                    _state.update { it.copy(show = show, isLoading = false) }
                } else {
                    _state.update { it.copy(isError = true, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isError = true, isLoading = false) }
            }
        }
    }

    open fun toggleFavorite() {
        viewModelScope.launch {
            _state.value.show?.let { show ->
                try {
                    val updatedShow = show.copy(isFavorite = !show.isFavorite)
                    favoritesRepository.updateShowLocally(updatedShow)
                    _state.update { it.copy(show = updatedShow) }
                } catch (e: Exception) {
                    _state.update { it.copy(isError = true) }
                }
            }
        }
    }

    open fun markSeasonWatched() {
        viewModelScope.launch {
            _state.value.show?.let { show ->
                try {
                    val updatedShow = show.copy(
                        seasonsWatched = (show.seasonsWatched + 1).coerceAtMost(show.totalSeasons)
                    )
                    favoritesRepository.updateShowLocally(updatedShow)
                    _state.update { it.copy(show = updatedShow) }
                } catch (e: Exception) {
                    _state.update { it.copy(isError = true) }
                }
            }
        }
    }
}

data class ShowDetailState(
    val show: TvShow? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false
)
