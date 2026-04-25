package com.multigp.racesync.domain.repositories

import com.multigp.racesync.domain.model.Series

interface SeriesRepository {
    suspend fun fetchSeries(): List<Series>
    suspend fun fetchSeriesDetail(seriesId: String): Series

    /** Owner-only: marks [raceId] as approved within [seriesId]. */
    suspend fun approveRace(seriesId: String, raceId: String)

    /** Owner-only: reverts an earlier approval. */
    suspend fun unapproveRace(seriesId: String, raceId: String)

    /** Owner-only: removes the race from the series entirely. */
    suspend fun removeRaceFromSeries(seriesId: String, raceId: String)
}
