package com.multigp.racesync.domain.useCase

import androidx.paging.PagingData
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.repositories.RacesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GetRacesUseCase(
    private val racesRepository: RacesRepository,
    private val loginInfoUserCase: GetLoginInfoUserCase
) {

    operator suspend fun invoke(radius: Double) = racesRepository.fetchRaces(radius)

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
}