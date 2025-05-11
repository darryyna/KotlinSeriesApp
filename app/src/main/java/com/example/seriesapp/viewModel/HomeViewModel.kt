import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seriesapp.models.TvShow
import com.example.seriesapp.repository.FavoritesRepository
import com.example.seriesapp.repository.TvShowsDataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class HomeViewModel(
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    open val state: StateFlow<HomeState> = _state

    init {
        observeShowsState()
        fetchShows()
    }

    private fun observeShowsState() {
        viewModelScope.launch {
            favoritesRepository.allShowsState.collect { dataState ->
                when (dataState) {
                    is TvShowsDataState.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                    is TvShowsDataState.Success -> {
                        _state.update {
                            it.copy(
                                tvShows = dataState.tvShows,
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

    open fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ToggleFavorite -> toggleFavorite(event.showId)
            is HomeEvent.RefreshShows -> refreshShows()
        }
    }

    private fun refreshShows() {
        viewModelScope.launch {
            favoritesRepository.fetchTvShows()
        }
    }

    private fun fetchShows() {
        viewModelScope.launch {
            favoritesRepository.fetchTvShows()
        }
    }

    private fun toggleFavorite(showId: Int) {
        viewModelScope.launch {
            val updatedShow = _state.value.tvShows.find { it.id == showId }
            updatedShow?.let {
                val newShow = it.copy(isFavorite = !it.isFavorite)
                favoritesRepository.updateShowLocally(newShow)
            }
        }
    }
}

data class HomeState(
    val tvShows: List<TvShow> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface HomeEvent {
    data class ToggleFavorite(val showId: Int) : HomeEvent
    object RefreshShows : HomeEvent
}