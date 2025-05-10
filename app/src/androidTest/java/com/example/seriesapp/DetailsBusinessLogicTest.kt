package com.example.seriesapp

import com.example.seriesapp.models.initialShows
import com.example.seriesapp.repository.ShowDetailRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DetailsBusinessLogicTest {


    private lateinit var repository: ShowDetailRepository
    private val testInitialShows = initialShows.map { it.copy() }

    @Before
    fun setUp() {
        repository = ShowDetailRepository(testInitialShows)
    }

    @Test
    fun getShowById_existingShow_returnsShow() {
        val showId = 1
        val show = repository.getShowById(showId)

        Assert.assertNotNull(show)
        Assert.assertEquals(showId, show?.id)
        Assert.assertEquals("Stranger Things", show?.title)
    }

    @Test
    fun getShowById_nonExistingShow_returnsNull() {
        val showId = 99
        val show = repository.getShowById(showId)
        Assert.assertNull(show)
    }

    @Test
    fun toggleFavorite_fromFalseToTrue_updatesFavoriteStatus() {
        val showId = 1 // Stranger Things, isFavorite = false
        val initialShow = repository.getShowById(showId)!!
        Assert.assertFalse(initialShow.isFavorite)

        repository.toggleFavorite(showId)

        val updatedShow = repository.getShowById(showId)!!
        Assert.assertTrue(updatedShow.isFavorite)
    }

    @Test
    fun toggleFavorite_fromTrueToFalse_updatesFavoriteStatus() {
        val showId = 3 // Day of the Jackal, isFavorite = true
        val initialShow = repository.getShowById(showId)!!
        Assert.assertTrue(initialShow.isFavorite)

        repository.toggleFavorite(showId)

        val updatedShow = repository.getShowById(showId)!!
        Assert.assertFalse(updatedShow.isFavorite)
    }

    @Test
    fun toggleFavorite_nonExistingShow_doesNothing() {
        val showId = 99
        repository.toggleFavorite(showId)

        testInitialShows.forEach { initial ->
            val current = repository.getShowById(initial.id)
            Assert.assertEquals(initial.isFavorite, current?.isFavorite)
        }
    }

    @Test
    fun markSeasonWatched_increasesSeasonsWatched() {
        val showId = 1 // Stranger Things, seasonsWatched = 2, totalSeasons = 4
        val initialShow = repository.getShowById(showId)!!
        Assert.assertEquals(2, initialShow.seasonsWatched)

        repository.markSeasonWatched(showId)

        val updatedShow = repository.getShowById(showId)!!
        Assert.assertEquals(3, updatedShow.seasonsWatched)
    }

    @Test
    fun markSeasonWatched_doesNotExceedTotalSeasons() {
        val showId = 4 // Breaking Bad, seasonsWatched = 5, totalSeasons = 5
        val initialShow = repository.getShowById(showId)!!
        Assert.assertEquals(5, initialShow.seasonsWatched)

        repository.markSeasonWatched(showId)

        val updatedShow = repository.getShowById(showId)!!
        Assert.assertEquals(5, updatedShow.seasonsWatched)
    }

    @Test
    fun markSeasonWatched_nonExistingShow_doesNothing() {
        val showId = 99
        repository.markSeasonWatched(showId)

        testInitialShows.forEach { initial ->
            val current = repository.getShowById(initial.id)
            Assert.assertEquals(initial.seasonsWatched, current?.seasonsWatched)
        }
    }
}