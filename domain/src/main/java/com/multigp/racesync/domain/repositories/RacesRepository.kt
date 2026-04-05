package com.multigp.racesync.domain.repositories

import android.location.Location
import androidx.paging.PagingData
import com.multigp.racesync.domain.location.LocationCoordinate
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.RaceView
import kotlinx.coroutines.flow.Flow

interface RacesRepository {
    suspend fun fetchRaces(pilotId: String): Flow<PagingData<Race>>

    suspend fun fetchPilotRaces(pilotId: String): Flow<List<Race>>

    suspend fun fetchJoinedChapterRaces(pilotId: String): Flow<List<Race>>

    suspend fun fetchRace(raceId: String): Flow<Race>

    suspend fun saveSearchRadius(radius: Double, unit: String)

    suspend fun fetchSearchRadius(): Flow<Pair<Double, String>>

    suspend fun joinRace(pilotId: String, raceId: String, aircraftId: String): Flow<Boolean>

    suspend fun resignFromRace(raceId: String): Flow<Boolean>

    suspend fun calculateRaceDistance(race: Race, currentLocation: Location)

    suspend fun fetchRaceView(raceId: String): Flow<RaceView>

    /**
     * Fetches nearby races from the API using the provided location and radius.
     * Matches iOS behaviour: single API call, no RemoteMediator pagination,
     * returns an in-memory list sorted by distance.
     */
    suspend fun fetchNearbyRaces(
        coordinate: LocationCoordinate,
        radiusMiles: Double
    ): List<Race>
}