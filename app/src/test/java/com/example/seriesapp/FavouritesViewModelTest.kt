package com.example.seriesapp

import FavoritesState
import FavoritesViewModel
import com.example.seriesapp.models.TvShow
import com.example.seriesapp.repository.FavoritesRepository
import com.example.seriesapp.repository.TvShowsDataState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.*


class TestableFavoritesViewModel(
    repository: FavoritesRepository
) : FavoritesViewModel(repository) {

    fun setTestState(state: FavoritesState) {
        (this.state as MutableStateFlow).value = state
    }
}

@ExperimentalCoroutinesApi
class FavouritesViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: TestableFavoritesViewModel
    private val mockRepository: FavoritesRepository = mock()

    private val testShow1 = TvShow(
        id = 1, title = "Show A", genre = "Action", rating = 4.0f, imageName = "test",
        totalSeasons = 1, seasonsWatched = 0, isFavorite = false, nextEpisodeDate = null
    )
    private val testShow2 = testShow1.copy(id = 2, title = "Show B", isFavorite = true)
    private val testShow3 = testShow1.copy(id = 3, title = "Show C", isFavorite = false)
    private val testShow4 = testShow1.copy(id = 4, title = "Show D", isFavorite = true)

    private val initialFavoriteShows = listOf(testShow2, testShow4)
    private val updatedFavoriteShows = listOf(testShow1.copy(isFavorite = true), testShow2, testShow4)

    @Test
    fun init_collectsFavoriteShowsFromRepository() = runTest {
        val repositoryFlow = MutableStateFlow<TvShowsDataState>(
            TvShowsDataState.Success(initialFavoriteShows)
        )
        whenever(mockRepository.allShowsState).thenReturn(repositoryFlow)
        viewModel = TestableFavoritesViewModel(repository = mockRepository)
        advanceUntilIdle()
        assertEquals(initialFavoriteShows, viewModel.state.value.favoriteShows)
        verify(mockRepository).allShowsState
    }

    @Test
    fun init_updatesStateWhenRepositoryFlowEmitsNewValue() = runTest {
        val repositoryFlow = MutableStateFlow<TvShowsDataState>(
            TvShowsDataState.Success(initialFavoriteShows)
        )
        whenever(mockRepository.allShowsState).thenReturn(repositoryFlow)
        viewModel = TestableFavoritesViewModel(repository = mockRepository)
        advanceUntilIdle()
        assertEquals(initialFavoriteShows, viewModel.state.value.favoriteShows)

        repositoryFlow.value = TvShowsDataState.Success(updatedFavoriteShows)
        advanceUntilIdle()
        assertEquals(updatedFavoriteShows.filter { it.isFavorite }, viewModel.state.value.favoriteShows)
    }

    @Test
    fun handleEvent_ToggleFavorite_callsRepositoryUpdateShowLocally() = runTest {
        val repositoryFlow = MutableStateFlow<TvShowsDataState>(TvShowsDataState.Success(initialFavoriteShows))
        whenever(mockRepository.allShowsState).thenReturn(repositoryFlow)

        viewModel = TestableFavoritesViewModel(repository = mockRepository)
        val toggledShow = testShow2.copy(isFavorite = !testShow2.isFavorite)
        viewModel.handleEvent(FavoritesEvent.ToggleFavorite(testShow2))
        advanceUntilIdle()
        verify(mockRepository).updateShowLocally(eq(toggledShow))
    }

    @Test
    fun handleEvent_ToggleFavorite_whenShowNotInList_doesNotThrow() = runTest {
        val repositoryFlow = MutableStateFlow<TvShowsDataState>(
            TvShowsDataState.Success(initialFavoriteShows)
        )
        whenever(mockRepository.allShowsState).thenReturn(repositoryFlow)
        viewModel = TestableFavoritesViewModel(repository = mockRepository)

        val nonExistentShow = testShow1.copy(id = 999)
        viewModel.handleEvent(FavoritesEvent.ToggleFavorite(nonExistentShow))
        advanceUntilIdle()

        verify(mockRepository).updateShowLocally(eq(nonExistentShow.copy(isFavorite = true)))
    }
}