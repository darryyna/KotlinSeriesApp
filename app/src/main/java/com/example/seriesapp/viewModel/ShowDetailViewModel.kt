package com.example.seriesapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seriesapp.models.TvShow
import com.example.seriesapp.repository.ShowDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShowDetailViewModel(
    private val repository: ShowDetailRepository,
    private val showId: Int
) : ViewModel() {
    private val _state = MutableStateFlow(ShowDetailState())
    val state: StateFlow<ShowDetailState> = _state

    fun loadShow() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val show = repository.getShowById(showId)
                _state.update { it.copy(show = show, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun toggleFavorite() {
        _state.value.show?.let { show ->
            repository.toggleFavorite(show.id)
            _state.update { it.copy(show = show.copy(isFavorite = !show.isFavorite)) }
        }
    }

    fun markSeasonWatched() {
        _state.value.show?.let { show ->
            repository.markSeasonWatched(show.id)
            _state.update {
                it.copy(show = show.copy(
                    seasonsWatched = (show.seasonsWatched + 1).coerceAtMost(show.totalSeasons)
                ))
            }
        }
    }
}

data class ShowDetailState(
    val show: TvShow? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)