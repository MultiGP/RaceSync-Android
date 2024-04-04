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
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.Pilot
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
import kotlinx.coroutines.flow.first
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
    private val pilotDao = raceSyncDB.pilotDao()

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun fetchRaces(radius: Double): Flow<PagingData<Race>> {
        val location = locationClient.lastLocation.await()
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

    override suspend fun fetchJoinedChapterRaces(pilotId: String): Flow<List<Race>> {
        /*
            Three steps involved to fetch joined chapter races
            1. Fetch joined chapters
            2. Fetch list of races associated with chapter -> returns race name name and id only
            3. Fetch each race using name as search string
        */

        val request = BaseRequest(
            data = ChaptersRequest(JoinedChapters(pilotId = pilotId)),
            sessionId = dataStore.getSessionId()!!,
            apiKey = apiKey
        )

        val raceRequest = BaseRequest<Any>(
            apiKey = apiKey,
            sessionId = dataStore.getSessionId()!!
        )

        return flow {
            //Fetch joined chapters
            val response = raceSyncApi.fetchChapters(0, 25, request)
            if (response.status) {
                response.data?.let { chapters ->
                    chapterDao.addChapters(chapters)
                    val races = chapters.map { chapter ->
                        //Fetch race list for each chapter -> return name and id only
                        raceSyncApi.fetchRacesForChapter(chapter.id, raceRequest)
                    }
                        .filter { it.status && it.data != null }
                        .flatMap { it.data!! }
                        .map {
                            val singleRaceRequest = BaseRequest(
                                data = RaceRequest(name = it.name),
                                sessionId = dataStore.getSessionId()!!,
                                apiKey = apiKey
                            )
                            //Fetch complete race details
                            raceSyncApi.fetchRaces(0, 1, singleRaceRequest)
                        }
                        .filter { it.status && it.data != null }
                        .flatMap { it.data!! }
                        .filter { it.isUpcoming }
                    val uniqueRaces = races.distinctBy { it.id }
                    raceDao.addRaces(uniqueRaces)
                    emit(uniqueRaces)
                } ?: emit(emptyList())
            }
        }
    }


    override fun fetchRace(raceId: String) = raceDao.getRace(raceId)

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

    override suspend fun calculateRaceDistance(race: Race) {
        try {
            locationClient.lastLocation.await()?.let { location ->
                val distance = race.calculateDistance(location)
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


    override suspend fun getPilotsForRace(raceId: String): Flow<List<Pilot>> {
        val request = BaseRequest<Nothing>(apiKey = apiKey, sessionId = dataStore.getSessionId()!!)
        return flow {
            val response = raceSyncApi.getPilotsForRace(raceId, request)
            if (response.isSuccessful) {
                response.body()?.let { baseResponse ->
                    if (baseResponse.status) {
                        baseResponse.data?.let { pilots ->
                            pilotDao.add(pilots)
                            emit(pilots)
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

    override suspend fun fetchRaceView(raceId: String): Flow<RaceView>{
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