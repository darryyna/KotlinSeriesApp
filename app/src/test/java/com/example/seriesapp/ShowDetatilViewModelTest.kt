package com.example.seriesapp.viewModel

import com.example.seriesapp.models.TvShow
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

class MainCoroutineRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        kotlinx.coroutines.Dispatchers.resetMain()
    }
}

class TestableShowDetailViewModel(
    repository: ShowDetailRepository,
    showId: Int
) : ShowDetailViewModel(repository, showId) {

    fun setTestState(state: ShowDetailState) {
        (this.state as MutableStateFlow).value = state
    }
}

@ExperimentalCoroutinesApi
class ShowDetailViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: TestableShowDetailViewModel
    private val mockRepository: ShowDetailRepository = mock()
    private val testShowId = 1

    private val testShow = TvShow(
        id = testShowId,
        title = "Test Show",
        genre = "Drama",
        rating = 4.5f,
        imageResId = 0,
        totalSeasons = 5,
        seasonsWatched = 2,
        isFavorite = false,
        nextEpisodeDate = "2025-05-10"
    )

    private val testShowFavorite = testShow.copy(isFavorite = true)
    private val testShowMaxSeasonsWatched = testShow.copy(seasonsWatched = testShow.totalSeasons)

    @Before
    fun setup() {
        viewModel = TestableShowDetailViewModel(repository = mockRepository, showId = testShowId)
    }

    @Test
    fun loadShow_success_updatesStateWithShow() = runTest {
        whenever(mockRepository.getShowById(testShowId)).thenReturn(testShow)
        viewModel.loadShow()
        advanceUntilIdle()
        assertEquals(ShowDetailState(show = testShow, isLoading = false), viewModel.state.value)
        verify(mockRepository).getShowById(testShowId)
    }

    @Test
    fun loadShow_error_updatesStateWithError() = runTest {
        whenever(mockRepository.getShowById(testShowId)).thenThrow(RuntimeException("Network Error"))
        viewModel.loadShow()
        advanceUntilIdle()
        assertEquals(ShowDetailState(isError = true, isLoading = false), viewModel.state.value)
        verify(mockRepository).getShowById(testShowId)
    }

    @Test
    fun toggleFavorite_fromFalseToTrue_updatesStateAndCallsRepository() = runTest {
        viewModel.setTestState(ShowDetailState(show = testShow))
        doNothing().whenever(mockRepository).toggleFavorite(testShowId)
        viewModel.toggleFavorite()

        advanceUntilIdle()
        assertTrue(viewModel.state.value.show?.isFavorite ?: false)
        assertEquals(testShowFavorite, viewModel.state.value.show)
        verify(mockRepository).toggleFavorite(testShowId)
    }

    @Test
    fun toggleFavorite_fromTrueToFalse_updatesStateAndCallsRepository() = runTest {
        viewModel.setTestState(ShowDetailState(show = testShowFavorite))
        doNothing().whenever(mockRepository).toggleFavorite(testShowId)
        viewModel.toggleFavorite()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.show?.isFavorite ?: true)
        assertEquals(testShow, viewModel.state.value.show)
        verify(mockRepository).toggleFavorite(testShowId)
    }

    @Test
    fun toggleFavorite_whenShowIsNull_doesNothing() = runTest {
        viewModel.setTestState(ShowDetailState(show = null))
        viewModel.toggleFavorite()
        advanceUntilIdle()
        assertEquals(ShowDetailState(show = null), viewModel.state.value)
        verify(mockRepository, never()).toggleFavorite(any())
    }

    @Test
    fun markSeasonWatched_increasesSeasonsWatchedAndCallsRepository() = runTest {
        viewModel.setTestState(ShowDetailState(show = testShow))
        doNothing().whenever(mockRepository).markSeasonWatched(testShowId)
        viewModel.markSeasonWatched()
        advanceUntilIdle()
        assertEquals(testShow.seasonsWatched + 1, viewModel.state.value.show?.seasonsWatched)
        verify(mockRepository).markSeasonWatched(testShowId)
    }

    @Test
    fun markSeasonWatched_doesNotExceedTotalSeasonsAndCallsRepository() = runTest {
        viewModel.setTestState(ShowDetailState(show = testShowMaxSeasonsWatched))
        doNothing().whenever(mockRepository).markSeasonWatched(testShowId)
        viewModel.markSeasonWatched()
        advanceUntilIdle()
        assertEquals(testShowMaxSeasonsWatched.totalSeasons, viewModel.state.value.show?.seasonsWatched)
        verify(mockRepository).markSeasonWatched(testShowId)
    }

    @Test
    fun markSeasonWatched_whenShowIsNull_doesNothing() = runTest {
        viewModel.setTestState(ShowDetailState(show = null))
        viewModel.markSeasonWatched()
        advanceUntilIdle()
        assertEquals(ShowDetailState(show = null), viewModel.state.value)
        verify(mockRepository, never()).markSeasonWatched(any())
    }
}