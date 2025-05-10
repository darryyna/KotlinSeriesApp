package com.example.seriesapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seriesapp.models.TvShow
import com.example.seriesapp.repository.ShowDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class ShowDetailViewModel(
    private val repository: ShowDetailRepository,
    private val showId: Int
) : ViewModel() {
    private val _state = MutableStateFlow(ShowDetailState())
    open val state: StateFlow<ShowDetailState> = _state

    open fun loadShow() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isError = false) }
            try {
                val show = repository.getShowById(showId)
                _state.update { it.copy(show = show, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isError = true, isLoading = false) }
            }
        }
    }

    open fun toggleFavorite() {
        viewModelScope.launch {
            _state.update { it.copy(isError = false) }
            try {
                _state.value.show?.let { show ->
                    repository.toggleFavorite(show.id)
                    _state.update { it.copy(show = show.copy(isFavorite = !show.isFavorite)) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isError = true) }
            }
        }
    }

    open fun markSeasonWatched() {
        viewModelScope.launch {
            _state.update { it.copy(isError = false) }
            try {
                _state.value.show?.let { show ->
                    repository.markSeasonWatched(show.id)
                    _state.update {
                        it.copy(show = show.copy(
                            seasonsWatched = (show.seasonsWatched + 1).coerceAtMost(show.totalSeasons)
                        ))
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isError = true) }
            }
        }
    }
}

data class ShowDetailState(
    val show: TvShow? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false
)