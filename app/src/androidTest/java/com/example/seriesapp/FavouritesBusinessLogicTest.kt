package com.example.seriesapp

import com.example.seriesapp.models.testShows
import com.example.seriesapp.repository.FavoritesRepository
import com.example.seriesapp.repository.TvShowsDataState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavouritesBusinessLogicTest {

    private lateinit var repository: FavoritesRepository

    private val testInitialTvShows = testShows.map { it.copy() }

    @Before
    fun setUp() {
        repository = FavoritesRepository()

        val initialSuccessState = TvShowsDataState.Success(testInitialTvShows)
        (repository.allShowsState as MutableStateFlow).value = initialSuccessState

        runBlocking {
            repository.updateFavorites()
        }
    }

    @Test
    fun favoriteShows_initiallyContainsOnlyFavorites() {
        val favorites = repository.favoriteShows.value
        Assert.assertEquals(2, favorites.size)
        Assert.assertTrue(favorites.any { it.id == 3 })
        Assert.assertTrue(favorites.any { it.id == 4 })
    }

    @Test
    fun updateFavorites_withShow_updatesShowAndFavorites() = runBlocking {
        val showToUpdate = testInitialTvShows.first { it.id == 1 }.copy(isFavorite = true)
        Assert.assertFalse(repository.favoriteShows.value.any { it.id == 1 })

        repository.updateShowLocally(showToUpdate)

        val updatedFavorites = repository.favoriteShows.value
        Assert.assertEquals(3, updatedFavorites.size)
        Assert.assertTrue(updatedFavorites.any { it.id == 1 && it.isFavorite })
        Assert.assertTrue(updatedFavorites.any { it.id == 3 && it.isFavorite })
        Assert.assertTrue(updatedFavorites.any { it.id == 4 && it.isFavorite })
    }

    @Test
    fun updateFavorites_withShow_fromFavoriteToNonFavorite() = runBlocking {
        val showToUpdate = testInitialTvShows.first { it.id == 3 }.copy(isFavorite = false)
        Assert.assertTrue(repository.favoriteShows.value.any { it.id == 3 })
        Assert.assertEquals(2, repository.favoriteShows.value.size)

        repository.updateShowLocally(showToUpdate)

        val updatedFavorites = repository.favoriteShows.value
        Assert.assertEquals(1, updatedFavorites.size)
        Assert.assertFalse(updatedFavorites.any { it.id == 3 })
        Assert.assertTrue(updatedFavorites.any { it.id == 4 })
    }

    @Test
    fun updateFavorites_withoutShow_recalculatesFavorites() = runBlocking {
        Assert.assertEquals(2, repository.favoriteShows.value.size)
        Assert.assertTrue(repository.favoriteShows.value.any { it.id == 3 })
        Assert.assertTrue(repository.favoriteShows.value.any { it.id == 4 })

        repository.updateFavorites()

        val favoritesAfterRecalculation = repository.favoriteShows.value
        Assert.assertEquals(2, favoritesAfterRecalculation.size)
        Assert.assertTrue(favoritesAfterRecalculation.any { it.id == 3 })
        Assert.assertTrue(favoritesAfterRecalculation.any { it.id == 4 })
    }

    @Test
    fun updateFavorites_withShow_updatesNonFavoriteField() = runBlocking {
        val showToUpdate = testInitialTvShows.first { it.id == 1 }.copy(rating = 5.0f)
        repository.updateShowLocally(showToUpdate)

        Assert.assertEquals(2, repository.favoriteShows.value.size)
        Assert.assertFalse(repository.favoriteShows.value.any { it.id == 1 })
    }
}
