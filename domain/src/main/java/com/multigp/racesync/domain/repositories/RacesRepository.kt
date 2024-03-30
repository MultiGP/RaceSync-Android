package com.multigp.racesync.domain.repositories

import androidx.paging.PagingData
import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.Pilot
import com.multigp.racesync.domain.model.Race
import kotlinx.coroutines.flow.Flow

interface RacesRepository {
    suspend fun fetchRaces(radius: Double): Flow<PagingData<Race>>

    suspend fun fetchRaces(pilotId: String): Flow<PagingData<Race>>

    suspend fun fetchJoinedChapterRaces(pilotId: String): Flow<List<Race>>

    fun fetchRace(raceId: String): Flow<Race>

    suspend fun saveSearchRadius(radius: Double, unit:String)

    suspend fun fetchSearchRadius(): Flow<Pair<Double, String>>

    suspend fun joinRace(pilotId:String, raceId:String, aircraftId:String): Flow<Boolean>

    suspend fun resignFromRace(raceId:String): Flow<Boolean>

    suspend fun calculateRaceDistance(race: Race)

    suspend fun getPilotsForRace(raceId: String): Flow<List<Pilot>>
}