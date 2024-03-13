package com.multigp.racesync.data.repository

import android.util.Log
import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.db.RaceSyncDB
import com.multigp.racesync.data.prefs.DataStoreManager
import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.model.requests.AircraftRequest
import com.multigp.racesync.domain.model.requests.PilotData
import com.multigp.racesync.domain.model.requests.ProfileRequest
import com.multigp.racesync.domain.repositories.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class ProfileRepositoryImpl(
    private val raceSyncApi: RaceSyncApi,
    private val raceSyncDB: RaceSyncDB,
    private val dataStore: DataStoreManager,
    private val apiKey: String
) : ProfileRepository {
    private val aircraftDao = raceSyncDB.aircraftDao()
    override suspend fun fetchProfile(apikey: String): Flow<BaseResponse<Profile>> = flow {
        val session = dataStore.getSessionId()
        val profileRequest = ProfileRequest(apikey, session)

        val response = raceSyncApi.fetchProfile(profileRequest)
        emit(response)
    }
        .catch {

            Log.d("tag", "Something went wrong")
        }
        .flowOn(Dispatchers.IO)

    override suspend fun fetchAllAircraft(): Flow<List<Aircraft>> {
        val session = dataStore.getSessionId()
        val pilotData = PilotData((dataStore.getUserInfo()?.id ?: "").toInt(), false)

        return flow {
            val aircrafts = aircraftDao.getAll().first()
            if(aircrafts.isNotEmpty()) {
                emit(aircrafts)
            }
            val aircraftRequest = AircraftRequest(apiKey, session, pilotData)
            val response = raceSyncApi.fetchAllAircraft(aircraftRequest)
            if (response.status) {
                response.data?.let { aircrafts ->
                    aircraftDao.add(aircrafts)
                    emit(aircrafts)
                } ?: emit(emptyList())
            }
        }
            .flowOn(Dispatchers.IO)
    }

}