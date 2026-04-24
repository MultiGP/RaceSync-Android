package com.multigp.racesync.domain.repositories

import com.multigp.racesync.domain.model.Series

interface SeriesRepository {
    suspend fun fetchSeries(): List<Series>
}
