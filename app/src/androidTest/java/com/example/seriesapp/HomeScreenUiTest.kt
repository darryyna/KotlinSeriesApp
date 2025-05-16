package com.example.seriesapp

import HomeEvent
import HomeState
import HomeViewModel
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.compose.AppTheme
import com.example.seriesapp.models.TvShow
import com.example.seriesapp.models.testShows
import com.example.seriesapp.repository.FavoritesRepository
import com.example.seriesapp.repository.TvShowsDataState
import com.example.seriesapp.views.HomeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

open class PureUITestHomeViewModel : HomeViewModel(createMockRepository()) {
    private val _testState = MutableStateFlow(HomeState())
    override val state: StateFlow<HomeState> = _testState

    fun setStateForTest(newState: HomeState) {
        _testState.value = newState
    }

    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ToggleFavorite -> {
                val updatedShows = _testState.value.tvShows.map {
                    if (it.id == event.showId) it.copy(isFavorite = !it.isFavorite) else it
                }
                _testState.value = _testState.value.copy(tvShows = updatedShows)
            }
            HomeEvent.RefreshShows -> {
            }
        }
    }

    companion object {
        private fun createMockRepository(): FavoritesRepository {
            return mock<FavoritesRepository>().also { repo ->
                whenever(repo.allShowsState).thenReturn(MutableStateFlow(TvShowsDataState.Success(emptyList())))
                whenever(repo.favoriteShows).thenReturn(MutableStateFlow(emptyList()))
            }
        }
    }
}

@RunWith(AndroidJUnit4::class)
class HomeScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockNavController: NavController = mock()
    private val mockHomeViewModel: PureUITestHomeViewModel = PureUITestHomeViewModel()

    private fun setViewModelState(state: HomeState) {
        mockHomeViewModel.setStateForTest(state)
        composeTestRule.waitForIdle()
    }

    private fun setupHomeScreen(initialState: HomeState) {
        setViewModelState(initialState)

        composeTestRule.setContent {
            AppTheme {
                HomeScreen(
                    navController = mockNavController,
                    viewModel = mockHomeViewModel
                )
            }
        }
    }

    @Test
    fun homeScreen_elementsAreDisplayed() {
        setupHomeScreen(initialState = HomeState(tvShows = emptyList(), isLoading = false))

        composeTestRule.onNodeWithTag("homeScreen").assertExists()
        composeTestRule.onNodeWithTag("homeTitle").assertExists()
        composeTestRule.onNodeWithText("My Series Journal").assertExists()
        composeTestRule.onNodeWithTag("searchIcon").assertExists()
        composeTestRule.onNodeWithContentDescription("Search").assertExists()
        composeTestRule.onNodeWithTag("showList").assertExists()
    }

    @Test
    fun homeScreen_showsListOfShowsWhenLoaded() {
        val testShows = listOf(
            TvShow(id = 1, title = "Show A", genre = "Action", rating = 4.0f, imageName = "br", totalSeasons = 1, seasonsWatched = 0, isFavorite = false, nextEpisodeDate = null),
            TvShow(id = 2, title = "Show B", genre = "Comedy", rating = 4.5f, imageName = "br", totalSeasons = 2, seasonsWatched = 1, isFavorite = true, nextEpisodeDate = "Tomorrow")
        )
        setupHomeScreen(initialState = HomeState(tvShows = testShows, isLoading = false))

        composeTestRule.onNodeWithTag("showList").assertExists()

        testShows.forEach { show ->
            composeTestRule.onNodeWithTag("showItem_${show.id}").assertExists()
            composeTestRule.onNodeWithTag("showTitle_${show.id}", useUnmergedTree = true).assertTextEquals(show.title)
            composeTestRule.onNodeWithTag("showGenre_${show.id}", useUnmergedTree = true).assertTextEquals(show.genre)
            composeTestRule.onNodeWithTag("showSeasonsProgressText_${show.id}", useUnmergedTree = true).assertTextEquals("Seasons: ${show.seasonsWatched}/${show.totalSeasons}")
            composeTestRule.onNodeWithTag("showProgressBackground_${show.id}", useUnmergedTree = true).assertExists()
            composeTestRule.onNodeWithTag("showProgressBar_${show.id}", useUnmergedTree = true).assertExists()
            composeTestRule.onNodeWithTag("showImage_${show.id}", useUnmergedTree = true).assertExists()

            val expectedFavoriteContentDescription = if (show.isFavorite) "Remove from favorites" else "Add to favorites"
            composeTestRule.onNodeWithTag("favoriteIcon_${show.id}", useUnmergedTree = true).assertContentDescriptionEquals(expectedFavoriteContentDescription)

            if (show.nextEpisodeDate != null) {
                composeTestRule.onNodeWithTag("showNextEpisode_${show.id}", useUnmergedTree = true).assertExists()
                composeTestRule.onNodeWithTag("showNextEpisode_${show.id}", useUnmergedTree = true).assertTextEquals("Next episode: ${show.nextEpisodeDate}")
            } else {
                composeTestRule.onNodeWithTag("showNextEpisode_${show.id}", useUnmergedTree = true).assertDoesNotExist()
            }
        }
    }

    @Test
    fun homeScreen_showsEmptyStateWhenNoShows() {
        setupHomeScreen(initialState = HomeState(tvShows = emptyList(), isLoading = false))

        composeTestRule.onNodeWithTag("showList").assertExists()
        composeTestRule.onNodeWithTag("showItem_1").assertDoesNotExist()
    }

    @Test
    fun homeScreen_searchIconIsClickable() {
        setupHomeScreen(initialState = HomeState(tvShows = emptyList(), isLoading = false))

        composeTestRule.onNodeWithTag("searchIcon").assertExists()
        composeTestRule.onNodeWithTag("searchIcon").assertIsEnabled()
        composeTestRule.onNodeWithTag("searchIcon").performClick()
    }

    @Test
    fun homeScreen_showListItemIsClickable() {
        val testShows = listOf(
            TvShow(id = 1, title = "Show A", genre = "Action", rating = 4.0f, imageName = "br", totalSeasons = 1, seasonsWatched = 0, isFavorite = false, nextEpisodeDate = null)
        )
        setupHomeScreen(initialState = HomeState(tvShows = testShows, isLoading = false))

        val showId = testShows.first().id
        composeTestRule.onNodeWithTag("showItem_${showId}").assertExists()
        composeTestRule.onNodeWithTag("showItem_${showId}").assertIsEnabled()
        composeTestRule.onNodeWithTag("showItem_${showId}").performClick()
    }

    @Test
    fun homeScreen_favoriteIconInListItemIsClickable() {
        val testShows = listOf(
            TvShow(id = 1, title = "Show A", genre = "Action", rating = 4.0f, imageName = "br", totalSeasons = 1, seasonsWatched = 0, isFavorite = false, nextEpisodeDate = null)
        )
        setupHomeScreen(initialState = HomeState(tvShows = testShows, isLoading = false))

        val showId = testShows.first().id
        composeTestRule.onNodeWithTag("favoriteIcon_${showId}", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("favoriteIcon_${showId}", useUnmergedTree = true).assertIsEnabled()
        composeTestRule.onNodeWithTag("favoriteIcon_${showId}", useUnmergedTree = true).performClick()
    }

    @Test
    fun homeScreen_favoriteIconInListItemChangesWhenFavoriteStateChanges() {
        val initialShow = TvShow(id = 1, title = "Show A", genre = "Action", rating = 4.0f, imageName = "br", totalSeasons = 1, seasonsWatched = 0, isFavorite = false, nextEpisodeDate = null)
        val favoriteShow = initialShow.copy(isFavorite = true)

        setupHomeScreen(initialState = HomeState(tvShows = testShows, isLoading = false))
        composeTestRule.onNodeWithTag("favoriteIcon_${initialShow.id}", useUnmergedTree = true).assertContentDescriptionEquals("Add to favorites")

        setViewModelState(HomeState(tvShows = listOf(favoriteShow)))
        composeTestRule.onNodeWithTag("favoriteIcon_${favoriteShow.id}", useUnmergedTree = true).assertContentDescriptionEquals("Remove from favorites")

        setViewModelState(HomeState(tvShows = listOf(initialShow)))
        composeTestRule.onNodeWithTag("favoriteIcon_${initialShow.id}", useUnmergedTree = true).assertContentDescriptionEquals("Add to favorites")
    }

    @Test
    fun homeScreen_showListItemHidesNextEpisodeWhenNull() {
        val showWithNextEpisode = TvShow(
            id = 1,
            title = "Show A",
            genre = "Action",
            rating = 4.0f,
            imageName = "br",
            totalSeasons = 1,
            seasonsWatched = 0,
            isFavorite = false,
            nextEpisodeDate = "Tomorrow"
        )
        val showWithoutNextEpisode = showWithNextEpisode.copy(nextEpisodeDate = null)

        setupHomeScreen(initialState = HomeState(tvShows = listOf(showWithNextEpisode), isLoading = false))
        composeTestRule.onNodeWithTag("showNextEpisode_1", useUnmergedTree = true)
            .assertExists()
            .assertTextEquals("Next episode: Tomorrow")

        setViewModelState(HomeState(tvShows = listOf(showWithoutNextEpisode), isLoading = false))
        composeTestRule.onNodeWithTag("showNextEpisode_1", useUnmergedTree = true)
            .assertDoesNotExist()
    }
}