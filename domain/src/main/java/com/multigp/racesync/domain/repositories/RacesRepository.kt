package com.multigp.racesync.domain.repositories

import androidx.paging.PagingData
import com.multigp.racesync.domain.model.Race
import kotlinx.coroutines.flow.Flow

interface RacesRepository {
    suspend fun fetchRaces(radius: Double): Flow<PagingData<Race>>

    suspend fun fetchRaces(pilotId: String): Flow<PagingData<Race>>

    fun fetchRace(raceId:String): Flow<Race>
}