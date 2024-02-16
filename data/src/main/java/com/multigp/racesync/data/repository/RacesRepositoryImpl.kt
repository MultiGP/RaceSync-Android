package com.multigp.racesync.data.repository

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.multigp.racesync.data.prefs.DataStoreManager
import com.multigp.racesync.data.repository.dataSource.RaceDataSource
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.JoinedRaces
import com.multigp.racesync.domain.model.requests.NearbyRaces
import com.multigp.racesync.domain.model.requests.PastRaces
import com.multigp.racesync.domain.model.requests.RaceRequest
import com.multigp.racesync.domain.model.requests.UpcomingRaces
import com.multigp.racesync.domain.repositories.RacesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
class RacesRepositoryImpl(
    private val raceDataSource: RaceDataSource,
    private val locationClient: FusedLocationProviderClient,
    private val dataStore: DataStoreManager
) : RacesRepository {
    override suspend fun fetchRaces(radius: Double): Flow<Result<BaseResponse<List<Race>>>> {
        return flow {
            val location = locationClient.lastLocation.await()
                ?: throw Exception("Unable to get your location.\nPlease check if your location service is ON and then try again.")
            val raceRequest = RaceRequest(
                joined = null,
                nearBy = NearbyRaces(location.latitude, location.longitude, radius),
                upComing = UpcomingRaces(),
                past = PastRaces()
            )
            val request = BaseRequest<RaceRequest>(
                sessionId = dataStore.getSessionId()!!,
                apiKey = "da65552b-0de4-331a-04c2-6991bae6fe27",
                data = raceRequest
            )
            val response = raceDataSource.fetchRaces(request)
            emit(Result.success(response))
        }
            .catch { emit(Result.failure(it)) }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun fetchRaces(pilotId: String): Flow<Result<BaseResponse<List<Race>>>> {
        return flow {
            val location = locationClient.lastLocation.await()
                ?: throw Exception("Unable to get your location.\nPlease check if your location service is ON and then try again.")

            val raceRequest = RaceRequest(
                joined = JoinedRaces(pilotId = pilotId),
                upComing = UpcomingRaces(orderByDistance = false),
                past = PastRaces(orderByDistance = false)
            )
            val request = BaseRequest<RaceRequest>(
                sessionId = dataStore.getSessionId()!!,
                apiKey = "da65552b-0de4-331a-04c2-6991bae6fe27",
                data = raceRequest
            )
            val response = raceDataSource.fetchRaces(request)
            emit(Result.success(response))
        }
            .catch { emit(Result.failure(it)) }
            .flowOn(Dispatchers.IO)
    }
}