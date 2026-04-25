package com.multigp.racesync.domain.useCase

import com.multigp.racesync.domain.model.Series
import com.multigp.racesync.domain.repositories.SeriesRepository

class GetSeriesUseCase(
    private val seriesRepository: SeriesRepository
) {
    suspend operator fun invoke(): List<Series> = seriesRepository.fetchSeries()

    suspend fun detail(seriesId: String): Series =
        seriesRepository.fetchSeriesDetail(seriesId)

    suspend fun approveRace(seriesId: String, raceId: String) =
        seriesRepository.approveRace(seriesId, raceId)

    suspend fun unapproveRace(seriesId: String, raceId: String) =
        seriesRepository.unapproveRace(seriesId, raceId)

    suspend fun removeRaceFromSeries(seriesId: String, raceId: String) =
        seriesRepository.removeRaceFromSeries(seriesId, raceId)
}
