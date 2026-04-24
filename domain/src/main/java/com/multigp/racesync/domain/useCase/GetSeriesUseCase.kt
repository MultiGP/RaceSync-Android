package com.multigp.racesync.domain.useCase

import com.multigp.racesync.domain.model.Series
import com.multigp.racesync.domain.repositories.SeriesRepository

class GetSeriesUseCase(
    private val seriesRepository: SeriesRepository
) {
    suspend operator fun invoke(): List<Series> = seriesRepository.fetchSeries()

    suspend fun detail(seriesId: String): Series =
        seriesRepository.fetchSeriesDetail(seriesId)
}
