package com.example.seriesapp.viewModel

import com.example.seriesapp.models.TvShow
import com.example.seriesapp.repository.FavoritesRepository
import com.example.seriesapp.repository.ShowDetailRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.Mockito.mock
import org.mockito.kotlin.*

class MainCoroutineRule @OptIn(ExperimentalCoroutinesApi::class) constructor(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun starting(description: Description) {
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun finished(description: Description) {
        kotlinx.coroutines.Dispatchers.resetMain()
    }
}

class TestableShowDetailViewModel(
    repository: ShowDetailRepository,
    favoritesRepository: FavoritesRepository,
    showId: Int
) : ShowDetailViewModel(repository, favoritesRepository ,showId) {

    fun setTestState(state: ShowDetailState) {
        (this.state as MutableStateFlow).value = state
    }
}

@ExperimentalCoroutinesApi
@OptIn(ExperimentalCoroutinesApi::class)
class ShowDetailViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: TestableShowDetailViewModel
    private val mockRepository: ShowDetailRepository = mock()
    private val mockFavoritesRepository: FavoritesRepository = mock()
    private val testShowId = 1

    private val testShow = TvShow(
        id = testShowId,
        title = "Test Show",
        genre = "Drama",
        rating = 4.5f,
        imageName = "test",
        totalSeasons = 5,
        seasonsWatched = 2,
        isFavorite = false,
        nextEpisodeDate = "2025-05-10"
    )

    private val testShowFavorite = testShow.copy(isFavorite = true)
    private val testShowMaxSeasonsWatched = testShow.copy(seasonsWatched = testShow.totalSeasons)

    @Before
    fun setup() {
        viewModel = TestableShowDetailViewModel(
            repository = mockRepository,
            favoritesRepository = mockFavoritesRepository,
            showId = testShowId
        )
    }

    @Test
    fun loadShow_success_updatesStateWithShow() = runTest {
        whenever(mockRepository.getShowById(testShowId)).thenReturn(testShow)
        viewModel.loadShow()
        advanceUntilIdle()
        assertEquals(ShowDetailState(show = testShow, isLoading = false), viewModel.state.value)
    }

    @Test
    fun loadShow_error_updatesStateWithError() = runTest {
        whenever(mockRepository.getShowById(testShowId)).thenThrow(RuntimeException("Network Error"))
        viewModel.loadShow()
        advanceUntilIdle()
        assertEquals(ShowDetailState(isError = true, isLoading = false), viewModel.state.value)
    }


    @Test
    fun toggleFavorite_fromFalseToTrue_updatesStateAndCallsRepository() = runTest {
        viewModel.setTestState(ShowDetailState(show = testShow))
        whenever(mockFavoritesRepository.updateShowLocally(testShowFavorite)).thenReturn(Unit)
        viewModel.toggleFavorite()
        advanceUntilIdle()
        assertEquals(testShowFavorite, viewModel.state.value.show)
        verify(mockFavoritesRepository).updateShowLocally(testShowFavorite)
    }

    @Test
    fun toggleFavorite_fromTrueToFalse_updatesStateAndCallsRepository() = runTest {
        viewModel.setTestState(ShowDetailState(show = testShowFavorite))
        whenever(mockFavoritesRepository.updateShowLocally(testShow)).thenReturn(Unit)
        viewModel.toggleFavorite()
        advanceUntilIdle()
        assertEquals(testShow, viewModel.state.value.show)
        verify(mockFavoritesRepository).updateShowLocally(testShow)
    }

    @Test
    fun toggleFavorite_whenShowIsNull_doesNothing() = runTest {
        viewModel.setTestState(ShowDetailState(show = null))
        viewModel.toggleFavorite()
        advanceUntilIdle()
        assertEquals(ShowDetailState(show = null), viewModel.state.value)
        verify(mockFavoritesRepository, never()).updateShowLocally(any())
    }

    @Test
    fun markSeasonWatched_increasesSeasonsWatchedAndCallsRepository() = runTest {
        val expected = testShow.copy(seasonsWatched = 3)
        viewModel.setTestState(ShowDetailState(show = testShow))
        whenever(mockFavoritesRepository.updateShowLocally(expected)).thenReturn(Unit)
        viewModel.markSeasonWatched()
        advanceUntilIdle()
        assertEquals(expected, viewModel.state.value.show)
        verify(mockFavoritesRepository).updateShowLocally(expected)
    }

    @Test
    fun markSeasonWatched_doesNotExceedTotalSeasonsAndCallsRepository() = runTest {
        viewModel.setTestState(ShowDetailState(show = testShowMaxSeasonsWatched))
        whenever(mockFavoritesRepository.updateShowLocally(testShowMaxSeasonsWatched)).thenReturn(Unit)
        viewModel.markSeasonWatched()
        advanceUntilIdle()
        assertEquals(testShowMaxSeasonsWatched, viewModel.state.value.show)
        verify(mockFavoritesRepository).updateShowLocally(testShowMaxSeasonsWatched)
    }

    @Test
    fun markSeasonWatched_whenShowIsNull_doesNothing() = runTest {
        viewModel.setTestState(ShowDetailState(show = null))
        viewModel.markSeasonWatched()
        advanceUntilIdle()
        assertEquals(ShowDetailState(show = null), viewModel.state.value)
        verify(mockFavoritesRepository, never()).updateShowLocally(any())
    }
}