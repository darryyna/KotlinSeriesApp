package com.example.seriesapp.db

import com.example.seriesapp.models.TvShow
import com.example.seriesapp.models.dao.TvShowDAO
import kotlinx.coroutines.flow.Flow

class TvShowRepository(private val tvShowDao: TvShowDAO) {

    suspend fun insertTvShow(tvShow: TvShow) {
        tvShowDao.insertTvShow(tvShow)
    }

    suspend fun deleteTvShow(tvShow: TvShow) {
        tvShowDao.deleteTvShow(tvShow)
    }

    suspend fun updateTvShow(tvShow: TvShow) {
        tvShowDao.updateTvShow(tvShow)
    }

    fun getTvShowById(tvShowId: Int): Flow<TvShow?> {
        return tvShowDao.getTvShowById(tvShowId)
    }

    fun getAllTvShows(): Flow<List<TvShow>> {
        return tvShowDao.getAllTvShows()
    }

    suspend fun toggleFavorite(tvShow: TvShow) {
        val updatedShow = tvShow.copy(isFavorite = !tvShow.isFavorite)
        tvShowDao.updateTvShow(updatedShow)
    }
}