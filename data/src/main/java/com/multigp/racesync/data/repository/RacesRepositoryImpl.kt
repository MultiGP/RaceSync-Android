package com.multigp.racesync.data.repository

import android.annotation.SuppressLint
import android.location.Location
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.android.gms.location.FusedLocationProviderClient
import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.db.RaceSyncDB
import com.multigp.racesync.data.paging.RaceRemoteMediator
import com.multigp.racesync.data.prefs.DataStoreManager
import com.multigp.racesync.domain.extensions.toDate
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.RaceView
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.ChaptersRequest
import com.multigp.racesync.domain.model.requests.JoinRaceRequest
import com.multigp.racesync.domain.model.requests.JoinedChapters
import com.multigp.racesync.domain.model.requests.JoinedRaces
import com.multigp.racesync.domain.model.requests.NearbyRaces
import com.multigp.racesync.domain.model.requests.RaceRequest
import com.multigp.racesync.domain.model.requests.UpcomingRaces
import com.multigp.racesync.domain.repositories.RacesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
    private val chapterDao = raceSyncDB.chapterDao()
    private val profileDao = raceSyncDB.profileDao()

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun fetchRaces(radius: Double): Flow<PagingData<Race>> {
        return locationClient.lastLocation.await()?.let { location ->
            val raceRequest = RaceRequest(
                joined = null,
                nearBy = NearbyRaces(location.latitude, location.longitude, radius),
                upComing = UpcomingRaces(orderByDistance = true)
            )
            val request = BaseRequest(
                apiKey = apiKey, data = raceRequest, sessionId = dataStore.getSessionId()!!
            )
            val pagingSourceFactory = {
                raceDao.getAllRaces()
            }
            Pager(
                //API doesn't support paging. Therefore fetching maximum races
                config = PagingConfig(pageSize = 1000),
                remoteMediator = RaceRemoteMediator(
                    raceSyncApi, raceSyncDB, request
                ),
                pagingSourceFactory = pagingSourceFactory,
            ).flow
        } ?: kotlin.run {
            flow {
                emit(PagingData.empty())
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun fetchRaces(pilotId: String): Flow<PagingData<Race>> {
        val raceRequest = RaceRequest(
            joined = JoinedRaces(pilotId = pilotId),
            upComing = UpcomingRaces(orderByDistance = false)
        )
        val request = BaseRequest(
            apiKey = apiKey, data = raceRequest, sessionId = dataStore.getSessionId()!!
        )
        val pagingSourceFactory = { raceDao.getJoinedRaces(true) }
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = RaceRemoteMediator(
                raceSyncApi, raceSyncDB, request
            ),
            pagingSourceFactory = pagingSourceFactory,
        ).flow
    }

    override suspend fun fetchPilotRaces(pilotId: String): Flow<List<Race>> {
        val request = BaseRequest(
            apiKey = apiKey,
            data = RaceRequest(
                joined = JoinedRaces(pilotId = pilotId)
            ),
            sessionId = dataStore.getSessionId()!!
        )

        return flow {
            val response = raceSyncApi.fetchRaces2(0, 50, request)
            if (response.isSuccessful) {
                response.body()?.let { baseResponse ->
                    if (baseResponse.status) {
                        emit(baseResponse.data!!)
                    } else {
                        throw Exception(baseResponse.errorMessage())
                    }
                }
            } else {
                val errorResponse = BaseResponse.convertFromErrorResponse(response)
                throw Exception(errorResponse.statusDescription)
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun fetchJoinedChapterRaces(pilotId: String): Flow<List<Race>> {
        /*
            Three steps involved to fetch joined chapter races
            1. Fetch joined chapters
            2. Fetch list of races associated with chapter -> returns race name name and id only
            3. Fetch each race using name as search string
        */

        return flow {
            profileDao.getProfile(id = pilotId).firstOrNull()?.let { profile->
                val raceRequest = BaseRequest(
                    apiKey = apiKey,
                    sessionId = dataStore.getSessionId()!!,
                    data = RaceRequest(
                        upComing = UpcomingRaces(orderByDistance = false),
                        chapterId = profile.chapterIds
                    )
                )

                //Fetch joined chapters
                val chapterRaces = raceDao.getChapterRaces(true)
                if (chapterRaces.isNotEmpty()) {
                    chapterRaces.sortedBy { it.startDate!!.toDate() }
                    emit(chapterRaces)
                }

                val races = profile.chapterIds.map { chapterId ->
                    raceSyncApi.fetchRaces(page = 0, pageSize = 25, raceRequest)
                }
                    .filter { it.status && it.data != null }
                    .flatMap { it.data!! }
                    .filter { it.isTodayOrUpcoming }
                    .sortedBy { it.startDate!!.toDate() }

                val uniqueRaces = races.distinctBy { it.id }
                uniqueRaces.forEach {
                    it.isChapterRace = true
                }
                raceDao.addRaces(uniqueRaces)
                emit(uniqueRaces)
            } ?: kotlin.run { emit(emptyList<Race>()) }
        }.flowOn(Dispatchers.IO)
    }


    override suspend fun fetchRace(raceId: String): Flow<Race> {
        return flow {
            val race = raceDao.getRace(raceId).firstOrNull()
            if (race != null) {
                emit(race)
            } else {
                val singleRaceRequest = BaseRequest(
                    data = RaceRequest(id = raceId),
                    sessionId = dataStore.getSessionId()!!,
                    apiKey = apiKey
                )
                //Fetch complete race details
                val response = raceSyncApi.fetchRaces(0, 1, singleRaceRequest)
                if (response.status) {
                    val data = response.data ?: emptyList()
                    if (data.isNotEmpty()) {
                        emit(data[0])
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun saveSearchRadius(radius: Double, unit: String) {
        dataStore.saveSearchRadius(radius, unit)
    }

    override suspend fun fetchSearchRadius() = flow {
        this.emit(dataStore.getSearchRadius())
    }

    override suspend fun joinRace(
        pilotId: String,
        raceId: String,
        aircraftId: String
    ): Flow<Boolean> {
        val request = BaseRequest(
            data = JoinRaceRequest(aircraftId = aircraftId),
            sessionId = dataStore.getSessionId()!!,
            apiKey = apiKey
        )
        return flow {
            val response = raceSyncApi.joinRace(raceId, request)
            if (response.isSuccessful) {
                response.body()?.let { baseResponse ->
                    if (baseResponse.status) {
                        val race = raceDao.getRace(raceId).first()
                        race.isJoined = true
                        race.participantCount += 1
                        raceDao.updateRace(race)
                        emit(true)
                    } else {
                        throw Exception(baseResponse.errorMessage())
                    }
                }
            } else {
                val errorResponse = BaseResponse.convertFromErrorResponse(response)
                throw Exception(errorResponse.statusDescription)
            }
        }
    }

    override suspend fun resignFromRace(raceId: String): Flow<Boolean> {
        val request = BaseRequest(
            data = null,
            sessionId = dataStore.getSessionId()!!,
            apiKey = apiKey
        )
        return flow {
            val response = raceSyncApi.resignFromRace(raceId, request)
            if (response.isSuccessful) {
                response.body()?.let { baseResponse ->
                    if (baseResponse.status) {
                        val race = raceDao.getRace(raceId).first()
                        race.isJoined = false
                        race.participantCount -= 1
                        raceDao.updateRace(race)
                        emit(true)
                    } else {
                        throw Exception(baseResponse.errorMessage())
                    }
                }
            } else {
                val errorResponse = BaseResponse.convertFromErrorResponse(response)
                throw Exception(errorResponse.statusDescription)
            }
        }
    }

    override suspend fun calculateRaceDistance(race: Race, currentLocation: Location) {
        try {
            if (race.distance == null) {
                val distance = race.calculateDistance(currentLocation)
                val (_, unit) = dataStore.getSearchRadius()
                if (unit == "mi") {
                    race.distance = String.format("%.2f mi", distance * 0.621371)
                } else {
                    race.distance = String.format("%.2f km", distance)
                }
                raceDao.updateRace(race)
            }
        } catch (_: Exception) {
        }
    }

    override suspend fun fetchRaceView(raceId: String): Flow<RaceView> {
        val request = BaseRequest<Nothing>(apiKey = apiKey, sessionId = dataStore.getSessionId()!!)
        return flow {
            val response = raceSyncApi.fetchRaceView(raceId, request)
            if (response.isSuccessful) {
                response.body()?.let { baseResponse ->
                    if (baseResponse.status) {
                        baseResponse.data?.let { raceView ->
                            emit(raceView)
                        }
                    } else {
                        throw Exception(baseResponse.errorMessage())
                    }
                }
            } else {
                val errorResponse = BaseResponse.convertFromErrorResponse(response)
                throw Exception(errorResponse.statusDescription)
            }
        }
            .flowOn(Dispatchers.IO)
    }
}