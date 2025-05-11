import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seriesapp.models.TvShow
import com.example.seriesapp.repository.FavoritesRepository
import com.example.seriesapp.repository.TvShowsDataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class FavoritesViewModel(
    private val repository: FavoritesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.allShowsState.collect { dataState ->
                when (dataState) {
                    is TvShowsDataState.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is TvShowsDataState.Success -> {
                        _state.update {
                            it.copy(
                                favoriteShows = dataState.tvShows.filter { show -> show.isFavorite },
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is TvShowsDataState.Error -> {
                        _state.update { it.copy(isLoading = false, error = dataState.message) }
                    }
                }
            }
        }
    }

    fun handleEvent(event: FavoritesEvent) {
        when (event) {
            is FavoritesEvent.ToggleFavorite -> {
                viewModelScope.launch {
                    val toggledShow = event.show.copy(isFavorite = !event.show.isFavorite)
                    repository.updateShowLocally(toggledShow)
                }
            }
        }
    }
}

data class FavoritesState(
    val favoriteShows: List<TvShow> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class FavoritesEvent {
    data class ToggleFavorite(val show: TvShow) : FavoritesEvent()
}