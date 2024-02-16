package com.multigp.racesync.domain.repositories

import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.Race
import kotlinx.coroutines.flow.Flow

interface RacesRepository {
    suspend fun fetchRaces(radius: Double): Flow<Result<BaseResponse<List<Race>>>>

    suspend fun fetchRaces(pilotId: String): Flow<Result<BaseResponse<List<Race>>>>
}