package com.multigp.racesync.domain.useCase

import android.location.Location
import androidx.paging.PagingData
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.repositories.RacesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GetRacesUseCase(
    private val racesRepository: RacesRepository,
    private val loginInfoUserCase: GetLoginInfoUserCase
) {

    suspend fun fetchNearbyRaces(radius: Double): Flow<PagingData<Race>> {
        return racesRepository.fetchRaces(radius)
    }

    suspend fun fetchJoinedRaces(): Flow<PagingData<Race>> {
        val loginInfo = loginInfoUserCase().first()
        return racesRepository.fetchRaces(loginInfo.second.id)
    }

    suspend fun fetchJoinedChapterRaces(): Flow<List<Race>> {
        val loginInfo = loginInfoUserCase().first()
        return racesRepository.fetchJoinedChapterRaces(loginInfo.second.id)
    }

    fun fetchRace(raceId: String): Flow<Race> {
        return racesRepository.fetchRace(raceId)
    }

    suspend fun saveSearchRadius(radius: Double, unit: String) {
        racesRepository.saveSearchRadius(radius, unit)
    }

    suspend fun fetchSearchRadius(): Double {
        val (radius, unit) = racesRepository.fetchSearchRadius().first()
        return if (unit == "mi") radius else radius * 0.621371
    }

    suspend fun fetchRaceFeedOptions() = racesRepository.fetchSearchRadius()

    suspend fun joinRace(raceId:String, aircraftId:String): Flow<Boolean>{
        val pilotId = loginInfoUserCase().first().second.id
        return racesRepository.joinRace(pilotId, raceId, aircraftId)
    }
}