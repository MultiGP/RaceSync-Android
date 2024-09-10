package com.multigp.racesync.domain.useCase

import android.location.Location
import androidx.paging.PagingData
import com.multigp.racesync.domain.model.Pilot
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.RaceView
import com.multigp.racesync.domain.repositories.RacesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.zip

class GetRacesUseCase(
    private val racesRepository: RacesRepository,
    private val loginInfoUserCase: GetLoginInfoUseCase,
    private val profileUseCase: GetProfileUseCase,
) {

    suspend fun fetchNearbyRaces(radius: Double): Flow<PagingData<Race>> {
        return racesRepository.fetchRaces(radius)
    }

    suspend fun fetchJoinedRaces(): Flow<PagingData<Race>> {
        val loginInfo = loginInfoUserCase().first()
        return racesRepository.fetchRaces(loginInfo.second!!.id)
    }

    suspend fun fetchPilotRaces(pilotUserName:String): Flow<List<Race>> {
        profileUseCase.getPilotId(pilotUserName)?.let {pilotId ->
            return racesRepository.fetchPilotRaces(pilotId)
        } ?: kotlin.run {
            throw Exception("Invalid pilot username")
        }
    }

    suspend fun fetchJoinedChapterRaces(): Flow<List<Race>> {
        val loginInfo = loginInfoUserCase().first()
        return racesRepository.fetchJoinedChapterRaces(loginInfo.second!!.id)
    }

    suspend fun fetchRace(raceId: String): Flow<Race> {
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

    suspend fun joinRace(raceId: String, aircraftId: String): Flow<Boolean> {
        val pilotId = loginInfoUserCase().first().second!!.id
        return racesRepository.joinRace(pilotId, raceId, aircraftId)
    }

    suspend fun resignFromRace(raceId: String): Flow<Boolean> {
        return racesRepository.resignFromRace(raceId)
    }


    suspend fun fetchRaceView(raceId: String): Flow<Triple<Profile, Race, RaceView>> {
        return racesRepository.fetchRaceView(raceId)
            .combine(profileUseCase()) { raceView, profile ->
                Pair(profile, raceView)
            }.combine(fetchRace(raceId)){(profile, raceView), race ->
                Triple(profile, race, raceView)
            }
    }


    suspend fun calculateRaceDistace(race: Race, currentLocation: Location) {
        racesRepository.calculateRaceDistance(race, currentLocation)
    }
}