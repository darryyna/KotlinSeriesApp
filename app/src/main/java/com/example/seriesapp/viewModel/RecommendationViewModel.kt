package com.example.seriesapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seriesapp.models.TvShow
import com.example.seriesapp.repository.RecommendationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecommendationsViewModel(
    private val repository: RecommendationsRepository = RecommendationsRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    init {
        loadRecommendations()
    }

    private fun loadRecommendations() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val allRecommendedShows = repository.getRecommendedShows()

                val recommendations = allRecommendedShows
                _state.update {
                    it.copy(
                        recommendedShows = recommendations,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Failed to load recommendations",
                        isLoading = false
                    )
                }
            }
        }
    }
}

data class State(
    val recommendedShows: Map<String, List<TvShow>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)