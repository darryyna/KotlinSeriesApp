package com.example.seriesapp.repository

import com.example.seriesapp.models.TvShow
import com.example.seriesapp.models.recommendedShowsSet


class RecommendationsRepository() {
    fun getRecommendedShows(): Map<String, List<TvShow>> {
        return recommendedShowsSet.groupBy { it.genre }
    }
}