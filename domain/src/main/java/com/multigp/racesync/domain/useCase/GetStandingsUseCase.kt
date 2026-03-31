package com.multigp.racesync.domain.useCase

import com.multigp.racesync.domain.model.Standing
import com.multigp.racesync.domain.model.StandingSeason
import com.multigp.racesync.domain.repositories.StandingsRepository
import kotlinx.coroutines.flow.Flow

class GetStandingsUseCase(
    private val standingsRepository: StandingsRepository
) {
    suspend operator fun invoke(season: StandingSeason): Flow<List<Standing>> {
        return standingsRepository.fetchStandings(season)
    }
}
