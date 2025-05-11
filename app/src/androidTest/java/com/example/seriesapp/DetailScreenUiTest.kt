package com.example.seriesapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.seriesapp.models.TvShow
import com.example.seriesapp.repository.FavoritesRepository
import com.example.seriesapp.repository.ShowDetailRepository
import com.example.seriesapp.viewModel.ShowDetailState
import com.example.seriesapp.viewModel.ShowDetailViewModel
import com.example.seriesapp.views.ShowDetailScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mockito.Mockito.mock

open class PureUITestShowDetailViewModel(repository: ShowDetailRepository, favoritesRepository: FavoritesRepository, showId: Int) : ShowDetailViewModel(repository, favoritesRepository, showId) {
    private val _testState = MutableStateFlow(ShowDetailState())
    override val state: StateFlow<ShowDetailState> = _testState.asStateFlow()

    fun setStateForTest(newState: ShowDetailState) {
        _testState.value = newState
    }

    override fun loadShow() {
    }

    override fun toggleFavorite() {
    }

    override fun markSeasonWatched() {
    }
}


@RunWith(AndroidJUnit4::class)
class DetailScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockNavController: NavController = mock()
    private val mockShowDetailViewModel: PureUITestShowDetailViewModel = PureUITestShowDetailViewModel(mock(), mock(), 1)
    private fun setViewModelState(state: ShowDetailState) {
        mockShowDetailViewModel.setStateForTest(state)
        composeTestRule.waitForIdle()
    }

    private fun setupShowDetailScreen(initialState: ShowDetailState) {
        setViewModelState(initialState)

        composeTestRule.setContent {
            ShowDetailScreen(
                showId = 1,
                navController = mockNavController,
                viewModel = mockShowDetailViewModel
            )
        }
    }

    @Test
    fun showDetailScreen_showsShowDetailsWhenLoaded() {
        val testShow = TvShow(
            id = 1,
            title = "Test Show",
            genre = "Drama",
            rating = 4.5f,
            imageName = "br",
            totalSeasons = 5,
            seasonsWatched = 2,
            isFavorite = false,
            nextEpisodeDate = "2025-05-10"
        )
        setupShowDetailScreen(initialState = ShowDetailState(show = testShow, isLoading = false, isError = false))

        composeTestRule.onNodeWithTag("showDetailScreen").assertExists()
        composeTestRule.onNodeWithTag("loadingOrErrorContainer").assertDoesNotExist()

        composeTestRule.onNodeWithTag("backButton").assertExists()
        composeTestRule.onNodeWithTag("favoriteButton").assertExists()
        composeTestRule.onNodeWithTag("showTitle").assertTextEquals(testShow.title)
        composeTestRule.onNodeWithTag("showGenre").assertTextEquals(testShow.genre)
        composeTestRule.onNodeWithTag("showRating").assertTextEquals(testShow.rating.toString())
        composeTestRule.onNodeWithTag("favoriteIcon", useUnmergedTree = true).assertContentDescriptionEquals("Add to favorites")

        composeTestRule.onNodeWithTag("watchingProgressCard").assertExists()
        composeTestRule.onNodeWithText("Watching Progress").assertExists()
        composeTestRule.onNodeWithTag("seasonsWatchedText").assertTextEquals("Seasons: ${testShow.seasonsWatched}/${testShow.totalSeasons}")
        composeTestRule.onNodeWithTag("seasonsProgressBarBackground").assertExists()
        composeTestRule.onNodeWithTag("seasonsProgressBar").assertExists()
        composeTestRule.onNodeWithTag("nextEpisodeRow").assertExists()
        composeTestRule.onNodeWithTag("nextEpisodeText").assertTextEquals("Next episode: ${testShow.nextEpisodeDate}")

        composeTestRule.onNodeWithText("Series Information").assertExists()
        composeTestRule.onNodeWithTag("infoGenreText").assertTextEquals(testShow.genre)
        composeTestRule.onNodeWithTag("infoRatingText").assertTextEquals("${testShow.rating}/5.0")
        composeTestRule.onNodeWithTag("infoTotalSeasonsText").assertTextEquals("${testShow.totalSeasons}")
        composeTestRule.onNodeWithTag("infoStatusText").assertTextEquals("Ongoing")

        composeTestRule.onNodeWithTag("markWatchedButton").assertExists()
        composeTestRule.onNodeWithText("Mark Watched").assertExists()
        composeTestRule.onNodeWithTag("addNoteButton").assertExists()
        composeTestRule.onNodeWithText("Add Note").assertExists()
    }

    @Test
    fun showDetailScreen_favoriteButtonChangesIconWhenFavoriteStateChanges() {
        val initialShow = TvShow(
            id = 1,
            title = "Test Show",
            genre = "Drama",
            rating = 4.5f,
            imageName = "br",
            totalSeasons = 5,
            seasonsWatched = 2,
            isFavorite = false,
            nextEpisodeDate = null
        )
        setupShowDetailScreen(initialState = ShowDetailState(show = initialShow, isLoading = false, isError = false))

        composeTestRule.onNodeWithTag("favoriteIcon", useUnmergedTree = true).assertContentDescriptionEquals("Add to favorites")

        val favoriteShow = initialShow.copy(isFavorite = true)
        setViewModelState(ShowDetailState(show = favoriteShow, isLoading = false, isError = false))

        composeTestRule.onNodeWithTag("favoriteIcon", useUnmergedTree = true).assertContentDescriptionEquals("Remove from favorites")

        setViewModelState(ShowDetailState(show = initialShow, isLoading = false, isError = false))

        composeTestRule.onNodeWithTag("favoriteIcon", useUnmergedTree = true).assertContentDescriptionEquals("Add to favorites")
    }

    @Test
    fun showDetailScreen_hidesNextEpisodeRowWhenNextEpisodeDateIsNull() {
        val testShow = TvShow(
            id = 1,
            title = "Test Show",
            genre = "Drama",
            rating = 4.5f,
            imageName = "br",
            totalSeasons = 5,
            seasonsWatched = 2,
            isFavorite = false,
            nextEpisodeDate = null
        )
        setupShowDetailScreen(initialState = ShowDetailState(show = testShow, isLoading = false, isError = false))

        composeTestRule.onNodeWithTag("nextEpisodeRow").assertDoesNotExist()
        composeTestRule.onNodeWithTag("nextEpisodeText").assertDoesNotExist()
        composeTestRule.onNodeWithTag("infoStatusText").assertTextEquals("Completed")
    }

    @Test
    fun showDetailScreen_backButtonIsClickable() {
        val testShow = TvShow(
            id = 1, title = "Test", genre = "Test", rating = 0.0f, imageName = "br",
            totalSeasons = 1, seasonsWatched = 0, isFavorite = false, nextEpisodeDate = null
        )
        setupShowDetailScreen(initialState = ShowDetailState(show = testShow))

        composeTestRule.onNodeWithTag("backButton").assertExists()
        composeTestRule.onNodeWithTag("backButton").assertIsEnabled()
        composeTestRule.onNodeWithTag("backButton").performClick()
    }

    @Test
    fun showDetailScreen_favoriteButtonIsClickable() {
        val testShow = TvShow(
            id = 1, title = "Test", genre = "Test", rating = 0.0f, imageName = "br",
            totalSeasons = 1, seasonsWatched = 0, isFavorite = false, nextEpisodeDate = null
        )
        setupShowDetailScreen(initialState = ShowDetailState(show = testShow))

        composeTestRule.onNodeWithTag("favoriteButton").assertExists()
        composeTestRule.onNodeWithTag("favoriteButton").assertIsEnabled()
        composeTestRule.onNodeWithTag("favoriteButton").performClick()
    }

    @Test
    fun showDetailScreen_markWatchedButtonIsClickable() {
        val testShow = TvShow(
            id = 1, title = "Test", genre = "Test", rating = 0.0f, imageName = "br",
            totalSeasons = 1, seasonsWatched = 0, isFavorite = false, nextEpisodeDate = null
        )
        setupShowDetailScreen(initialState = ShowDetailState(show = testShow))

        composeTestRule.onNodeWithTag("markWatchedButton").assertExists()
        composeTestRule.onNodeWithTag("markWatchedButton").assertIsEnabled()
        composeTestRule.onNodeWithTag("markWatchedButton").performClick()
    }

    @Test
    fun showDetailScreen_addNoteButtonIsClickable() {
        val testShow = TvShow(
            id = 1, title = "Test", genre = "Test", rating = 0.0f, imageName = "br",
            totalSeasons = 1, seasonsWatched = 0, isFavorite = false, nextEpisodeDate = null
        )
        setupShowDetailScreen(initialState = ShowDetailState(show = testShow))

        composeTestRule.onNodeWithTag("addNoteButton").assertExists()
        composeTestRule.onNodeWithTag("addNoteButton").assertIsEnabled()
        composeTestRule.onNodeWithTag("addNoteButton").performClick()
    }
}
