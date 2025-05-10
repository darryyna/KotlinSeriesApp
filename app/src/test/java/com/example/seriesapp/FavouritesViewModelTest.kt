package com.example.seriesapp

import com.example.seriesapp.models.TvShow
import com.example.seriesapp.repository.FavoritesRepository
import com.example.seriesapp.viewModel.FavoritesEvent
import com.example.seriesapp.viewModel.FavoritesState
import com.example.seriesapp.viewModel.FavoritesViewModel
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
        id = 1, title = "Show A", genre = "Action", rating = 4.0f, imageResId = 0,
        totalSeasons = 1, seasonsWatched = 0, isFavorite = false, nextEpisodeDate = null
    )
    private val testShow2 = testShow1.copy(id = 2, title = "Show B", isFavorite = true)
    private val testShow3 = testShow1.copy(id = 3, title = "Show C", isFavorite = false)
    private val testShow4 = testShow1.copy(id = 4, title = "Show D", isFavorite = true)

    private val initialFavoriteShows = listOf(testShow2, testShow4)
    private val updatedFavoriteShows = listOf(testShow1.copy(isFavorite = true), testShow2, testShow4)

    @Test
    fun init_collectsFavoriteShowsFromRepository() = runTest {
        val repositoryFavoriteShowsFlow = MutableStateFlow(initialFavoriteShows)
        whenever(mockRepository.favoriteShows).thenReturn(repositoryFavoriteShowsFlow)
        viewModel = TestableFavoritesViewModel(repository = mockRepository)
        advanceUntilIdle()
        assertEquals(initialFavoriteShows, viewModel.state.value.favoriteShows)
        verify(mockRepository).favoriteShows
    }

    @Test
    fun init_updatesStateWhenRepositoryFlowEmitsNewValue() = runTest {
        val repositoryFavoriteShowsFlow = MutableStateFlow(initialFavoriteShows)
        whenever(mockRepository.favoriteShows).thenReturn(repositoryFavoriteShowsFlow)
        viewModel = TestableFavoritesViewModel(repository = mockRepository)
        advanceUntilIdle()
        assertEquals(initialFavoriteShows, viewModel.state.value.favoriteShows)
        repositoryFavoriteShowsFlow.value = updatedFavoriteShows
        advanceUntilIdle()
        assertEquals(updatedFavoriteShows, viewModel.state.value.favoriteShows)
    }

    @Test
    fun handleEvent_ToggleFavorite_callsRepositoryUpdateFavorites() = runTest {
        val repositoryFavoriteShowsFlow = MutableStateFlow(initialFavoriteShows)
        whenever(mockRepository.favoriteShows).thenReturn(repositoryFavoriteShowsFlow)
        doNothing().whenever(mockRepository).updateFavorites(any())
        viewModel = TestableFavoritesViewModel(repository = mockRepository)
        viewModel.setTestState(FavoritesState(favoriteShows = initialFavoriteShows))

        val showIdToToggle = testShow2.id
        val expectedShowAfterToggle = testShow2.copy(isFavorite = !testShow2.isFavorite)
        viewModel.handleEvent(FavoritesEvent.ToggleFavorite(showIdToToggle))
        advanceUntilIdle()
        verify(mockRepository).updateFavorites(eq(expectedShowAfterToggle))
        assertEquals(initialFavoriteShows, viewModel.state.value.favoriteShows)
    }

    @Test
    fun handleEvent_ToggleFavorite_whenShowNotFound_doesNothing() = runTest {
        val repositoryFavoriteShowsFlow = MutableStateFlow(initialFavoriteShows)
        whenever(mockRepository.favoriteShows).thenReturn(repositoryFavoriteShowsFlow)
        viewModel = TestableFavoritesViewModel(repository = mockRepository)
        val initialState = FavoritesState(favoriteShows = initialFavoriteShows)
        viewModel.setTestState(initialState)
        viewModel.handleEvent(FavoritesEvent.ToggleFavorite(99))
        advanceUntilIdle()
        verify(mockRepository, never()).updateFavorites(any())
        assertEquals(initialState, viewModel.state.value)
    }
}