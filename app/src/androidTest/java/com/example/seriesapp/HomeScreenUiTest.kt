package com.example.seriesapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.seriesapp.models.TvShow
import com.example.seriesapp.viewModel.HomeEvent
import com.example.seriesapp.viewModel.HomeState
import com.example.seriesapp.viewModel.HomeViewModel
import com.example.seriesapp.views.HomeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mockito.Mockito.mock

open class PureUITestHomeViewModel : HomeViewModel(mock()) {
    private val _testState = MutableStateFlow(HomeState())
    override val state: StateFlow<HomeState> = _testState.asStateFlow()

    fun setStateForTest(newState: HomeState) {
        _testState.value = newState
    }

    override fun onEvent(event: HomeEvent) {
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
            HomeScreen(
                navController = mockNavController,
                viewModel = mockHomeViewModel
            )
        }
    }

    @Test
    fun homeScreen_elementsAreDisplayed() {
        setupHomeScreen(initialState = HomeState(tvShows = emptyList()))

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
            TvShow(id = 1, title = "Show A", genre = "Action", rating = 4.0f, imageResId = R.drawable.ic_launcher_foreground, totalSeasons = 1, seasonsWatched = 0, isFavorite = false, nextEpisodeDate = null),
            TvShow(id = 2, title = "Show B", genre = "Comedy", rating = 4.5f, imageResId = R.drawable.ic_launcher_foreground, totalSeasons = 2, seasonsWatched = 1, isFavorite = true, nextEpisodeDate = "Tomorrow")
        )
        setupHomeScreen(initialState = HomeState(tvShows = testShows))

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
        setupHomeScreen(initialState = HomeState(tvShows = emptyList()))

        composeTestRule.onNodeWithTag("showList").assertExists()
        composeTestRule.onNodeWithTag("showItem_1").assertDoesNotExist()
    }

    @Test
    fun homeScreen_searchIconIsClickable() {
        setupHomeScreen(initialState = HomeState(tvShows = emptyList()))

        composeTestRule.onNodeWithTag("searchIcon").assertExists()
        composeTestRule.onNodeWithTag("searchIcon").assertIsEnabled()
        composeTestRule.onNodeWithTag("searchIcon").performClick()
    }

    @Test
    fun homeScreen_showListItemIsClickable() {
        val testShows = listOf(
            TvShow(id = 1, title = "Show A", genre = "Action", rating = 4.0f, imageResId = R.drawable.ic_launcher_foreground, totalSeasons = 1, seasonsWatched = 0, isFavorite = false, nextEpisodeDate = null)
        )
        setupHomeScreen(initialState = HomeState(tvShows = testShows))

        val showId = testShows.first().id
        composeTestRule.onNodeWithTag("showItem_${showId}").assertExists()
        composeTestRule.onNodeWithTag("showItem_${showId}").assertIsEnabled()
        composeTestRule.onNodeWithTag("showItem_${showId}").performClick()
    }

    @Test
    fun homeScreen_favoriteIconInListItemIsClickable() {
        val testShows = listOf(
            TvShow(id = 1, title = "Show A", genre = "Action", rating = 4.0f, imageResId = R.drawable.ic_launcher_foreground, totalSeasons = 1, seasonsWatched = 0, isFavorite = false, nextEpisodeDate = null)
        )
        setupHomeScreen(initialState = HomeState(tvShows = testShows))

        val showId = testShows.first().id
        composeTestRule.onNodeWithTag("favoriteIcon_${showId}", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("favoriteIcon_${showId}", useUnmergedTree = true).assertIsEnabled()
        composeTestRule.onNodeWithTag("favoriteIcon_${showId}", useUnmergedTree = true).performClick()
    }

    @Test
    fun homeScreen_favoriteIconInListItemChangesWhenFavoriteStateChanges() {
        val initialShow = TvShow(id = 1, title = "Show A", genre = "Action", rating = 4.0f, imageResId = R.drawable.ic_launcher_foreground, totalSeasons = 1, seasonsWatched = 0, isFavorite = false, nextEpisodeDate = null)
        val favoriteShow = initialShow.copy(isFavorite = true)

        setupHomeScreen(initialState = HomeState(tvShows = listOf(initialShow)))
        composeTestRule.onNodeWithTag("favoriteIcon_${initialShow.id}", useUnmergedTree = true).assertContentDescriptionEquals("Add to favorites")

        setViewModelState(HomeState(tvShows = listOf(favoriteShow)))
        composeTestRule.onNodeWithTag("favoriteIcon_${favoriteShow.id}", useUnmergedTree = true).assertContentDescriptionEquals("Remove from favorites")

        setViewModelState(HomeState(tvShows = listOf(initialShow)))
        composeTestRule.onNodeWithTag("favoriteIcon_${initialShow.id}", useUnmergedTree = true).assertContentDescriptionEquals("Add to favorites")
    }

    @Test
    fun homeScreen_showListItemHidesNextEpisodeWhenNull() {
        val showWithNextEpisode = TvShow(id = 1, title = "Show A", genre = "Action", rating = 4.0f, imageResId = R.drawable.ic_launcher_foreground, totalSeasons = 1, seasonsWatched = 0, isFavorite = false, nextEpisodeDate = "Tomorrow")
        val showWithoutNextEpisode = showWithNextEpisode.copy(id = 1, nextEpisodeDate = null)

        setupHomeScreen(initialState = HomeState(tvShows = listOf(showWithNextEpisode)))

        composeTestRule.onNodeWithTag("showNextEpisode_${showWithNextEpisode.id}", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("showNextEpisode_${showWithNextEpisode.id}", useUnmergedTree = true).assertTextEquals("Next episode: ${showWithNextEpisode.nextEpisodeDate}")

        setViewModelState(HomeState(tvShows = listOf(showWithoutNextEpisode)))

        composeTestRule.onNodeWithTag("showNextEpisode_${showWithoutNextEpisode.id}", useUnmergedTree = true).assertDoesNotExist()
    }
}