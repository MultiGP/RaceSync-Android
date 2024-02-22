package com.multigp.racesync.data.repository

import android.annotation.SuppressLint
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.android.gms.location.FusedLocationProviderClient
import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.db.RaceSyncDB
import com.multigp.racesync.data.paging.RaceRemoteMediator
import com.multigp.racesync.data.prefs.DataStoreManager
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.JoinedRaces
import com.multigp.racesync.domain.model.requests.NearbyRaces
import com.multigp.racesync.domain.model.requests.PastRaces
import com.multigp.racesync.domain.model.requests.RaceRequest
import com.multigp.racesync.domain.model.requests.UpcomingRaces
import com.multigp.racesync.domain.repositories.RacesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
class RacesRepositoryImpl(
    private val raceSyncApi: RaceSyncApi,
    private val raceSyncDB: RaceSyncDB,
    private val locationClient: FusedLocationProviderClient,
    private val dataStore: DataStoreManager,
    private val apiKey: String
) : RacesRepository {
    private val raceDao = raceSyncDB.raceDao()

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun fetchRaces(radius: Double): Flow<PagingData<Race>> {
        val location = locationClient.lastLocation.await()
            ?: throw Exception("Unable to get your location.\nPlease check if your location service is ON and then try again.")
        val raceRequest = RaceRequest(
            joined = null,
            nearBy = NearbyRaces(location.latitude, location.longitude, radius),
//            nearBy = NearbyRaces(3.1319, 101.6841, radius),
            upComing = UpcomingRaces(),
            past = PastRaces()
        )
        val request = BaseRequest(
            apiKey = apiKey,
            data = raceRequest,
            sessionId = dataStore.getSessionId()!!
        )
        val pagingSourceFactory = { raceDao.getAllRaces() }
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = RaceRemoteMediator(
                raceSyncApi,
                raceSyncDB,
                request
            ),
            pagingSourceFactory = pagingSourceFactory,
        ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun fetchRaces(pilotId: String): Flow<PagingData<Race>> {
        val raceRequest = RaceRequest(
            joined = JoinedRaces(pilotId = pilotId),
            upComing = UpcomingRaces(orderByDistance = false),
            past = PastRaces(orderByDistance = false)
        )
        val request = BaseRequest(
            apiKey = apiKey,
            data = raceRequest,
            sessionId = dataStore.getSessionId()!!
        )
        val pagingSourceFactory = { raceDao.getJoinedRaces(true) }
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = RaceRemoteMediator(
                raceSyncApi,
                raceSyncDB,
                request
            ),
            pagingSourceFactory = pagingSourceFactory,
        ).flow
    }

    override fun fetchRace(raceId: String) = raceDao.getRace(raceId)
}