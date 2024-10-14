package com.multigp.racesync.data.repository

import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.db.RaceSyncDB
import com.multigp.racesync.data.prefs.DataStoreManager
import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.model.requests.AircraftRequest
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.PilotData
import com.multigp.racesync.domain.model.requests.ProfileRequest
import com.multigp.racesync.domain.model.requests.SearchRequest
import com.multigp.racesync.domain.repositories.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class ProfileRepositoryImpl(
    private val raceSyncApi: RaceSyncApi,
    private val raceSyncDB: RaceSyncDB,
    private val dataStore: DataStoreManager,
    private val apiKey: String
) : ProfileRepository {
    private val aircraftDao = raceSyncDB.aircraftDao()
    private val profileDao = raceSyncDB.profileDao()
    override suspend fun fetchProfile(): Flow<Profile> = flow {
        val profiles = profileDao.getAll().first()
        if (profiles.isNotEmpty()) {
            emit(profiles.first())
        } else {
            val profileRequest = ProfileRequest(apiKey, dataStore.getSessionId())
            try{
                val response = raceSyncApi.fetchProfile(profileRequest)
                if (response.isSuccessful) {
                    response.body()?.let { baseResponse ->
                        if (baseResponse.status) {
                            baseResponse.data?.let { profile ->
                                profileDao.add(profile)
                                emit(profile)
                            }
                        } else {
                            throw Exception(baseResponse.errorMessage())
                        }
                    }
                } else {
                    val errorResponse = BaseResponse.convertFromErrorResponse(response)
                    throw Exception(errorResponse.statusDescription)
                }
            }catch (exception: Exception){
                throw exception
            }
        }
    }
        .flowOn(Dispatchers.IO)


    override suspend fun fetchProfile(pilotName: String) = flow {
        val userInfo = dataStore.getUserInfo()!!
        val profile = profileDao.get(pilotName).firstOrNull()
        if (profile != null) {
            emit(Pair(profile, userInfo))
        }else{
            val request = BaseRequest(apiKey, SearchRequest(pilotName), dataStore.getSessionId()!!)
            val response = raceSyncApi.searchUser(request)
            if (response.isSuccessful) {
                response.body()?.let { baseResponse ->
                    if (baseResponse.status) {
                        baseResponse.data?.let { profile ->
                            profileDao.add(profile)
                            emit(Pair(profile, userInfo))
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
    }
        .flowOn(Dispatchers.IO)

    suspend override fun getPilotId(pilotUserName: String): String?{
        return profileDao.get(pilotUserName).firstOrNull()?.id
    }

    override suspend fun fetchAllAircraft(): Flow<List<Aircraft>> {
        return fetchPilotAircrafts(dataStore.getUserInfo()?.id ?: "")
    }

    override suspend fun fetchPilotAircrafts(pilotId: String): Flow<List<Aircraft>> {
        val session = dataStore.getSessionId()
        val pilotData = PilotData(pilotId.toInt(), false)

        return flow {
            val aircrafts = aircraftDao.getAircrafts(pilotId.toInt()).first()
            if (aircrafts.isNotEmpty()) {
                emit(aircrafts)
            }
            val aircraftRequest = AircraftRequest(apiKey, session, pilotData)
            val response = raceSyncApi.fetchAllAircraft(aircraftRequest)
            if (response.status) {
                response.data?.let { aircrafts ->
                    aircrafts.forEach { it.pilotId = pilotId.toInt() }
                    aircraftDao.add(aircrafts)
                    emit(aircrafts)
                } ?: emit(emptyList())
            }
        }
            .flowOn(Dispatchers.IO)
    }

}