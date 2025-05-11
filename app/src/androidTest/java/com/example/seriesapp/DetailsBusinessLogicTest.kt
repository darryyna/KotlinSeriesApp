package com.example.seriesapp

import com.example.seriesapp.models.testShows
import com.example.seriesapp.repository.ShowDetailRepository
import com.example.seriesapp.repository.TvShowsDataState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */




@ExperimentalCoroutinesApi
class DetailsBusinessLogicTest {

    private lateinit var repository: ShowDetailRepository
    private lateinit var allShowsState: MutableStateFlow<TvShowsDataState>
    private val testInitialShows = testShows.map { it.copy() }

    @Before
    fun setUp() {
        allShowsState = MutableStateFlow(TvShowsDataState.Success(testInitialShows))
        repository = ShowDetailRepository(allShowsState)
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
}
