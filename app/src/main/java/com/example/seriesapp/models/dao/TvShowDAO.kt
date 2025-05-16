package com.example.seriesapp.models.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.seriesapp.models.TvShow
import kotlinx.coroutines.flow.Flow

@Dao
interface TvShowDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvShow(tvShow: TvShow)

    @Delete
    suspend fun deleteTvShow(tvShow: TvShow)

    @Update
    suspend fun updateTvShow(tvShow: TvShow)

    @Query("SELECT * FROM tv_shows WHERE id = :tvShowId")
    fun getTvShowById(tvShowId: Int): Flow<TvShow?>

    @Query("SELECT * FROM tv_shows")
    fun getAllTvShows(): Flow<List<TvShow>>
}