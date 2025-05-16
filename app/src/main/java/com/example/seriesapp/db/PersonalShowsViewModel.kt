package com.example.seriesapp.db

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.seriesapp.models.TvShow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PersonalShowsViewModel(private val repository: TvShowRepository) : ViewModel() {

    val allShows: StateFlow<List<TvShow>> = repository.getAllTvShows()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addShow(tvShow: TvShow) = viewModelScope.launch {
        repository.insertTvShow(tvShow)
    }

    fun deleteShow(tvShow: TvShow) = viewModelScope.launch {
        repository.deleteTvShow(tvShow)
    }

    fun updateShow(tvShow: TvShow) = viewModelScope.launch {
        repository.updateTvShow(tvShow)
    }

    fun toggleFavorite(tvShow: TvShow) = viewModelScope.launch {
        repository.toggleFavorite(tvShow)
    }
}


class PersonalShowsViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val database = AppDatabase.getDatabase(context)
    private val repository = TvShowRepository(database.tvShowDao())

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonalShowsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PersonalShowsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

