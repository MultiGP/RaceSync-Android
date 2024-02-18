package com.multigp.racesync.data.repository

import android.annotation.SuppressLint
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.android.gms.location.FusedLocationProviderClient
import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.paging.JoinedRacesPagingSources
import com.multigp.racesync.data.paging.NearbyRacesPagingSources
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
    private val locationClient: FusedLocationProviderClient,
    private val dataStore: DataStoreManager,
    private val apiKey: String
) : RacesRepository {
    override suspend fun fetchRaces(radius: Double): Flow<PagingData<Race>> {
        val location = locationClient.lastLocation.await()
            ?: throw Exception("Unable to get your location.\nPlease check if your location service is ON and then try again.")
        val raceRequest = RaceRequest(
            joined = null,
            nearBy = NearbyRaces(location.latitude, location.longitude, radius),
            upComing = UpcomingRaces(),
            past = PastRaces()
        )
        val request = BaseRequest(
            apiKey = apiKey,
            data = raceRequest,
            sessionId = dataStore.getSessionId()!!
        )
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = true),
            pagingSourceFactory = { NearbyRacesPagingSources(raceSyncApi, request) }
        )
            .flow
    }

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
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = true),
            pagingSourceFactory = { JoinedRacesPagingSources(raceSyncApi, request) }
        )
            .flow
    }
}