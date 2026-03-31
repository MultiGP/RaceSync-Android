package com.multigp.racesync.domain.repositories

import com.multigp.racesync.domain.model.Standing
import com.multigp.racesync.domain.model.StandingSeason
import kotlinx.coroutines.flow.Flow

interface StandingsRepository {
    suspend fun fetchStandings(season: StandingSeason): Flow<List<Standing>>
}
