package com.multigp.racesync.data.repository

import android.util.Log
import com.multigp.racesync.data.BuildConfig
import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.prefs.DataStoreManager
import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.BaseResponse2
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.model.requests.AircraftRequest
import com.multigp.racesync.domain.model.requests.PilotData
import com.multigp.racesync.domain.model.requests.ProfileRequest
import com.multigp.racesync.domain.repositories.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class ProfileRepositoryImpl(
    private val raceSyncApi: RaceSyncApi,
    private val dataStore: DataStoreManager
): ProfileRepository {
    override suspend fun fetchProfile(apikey: String): Flow<BaseResponse<Profile>> = flow {
        val session = dataStore.getSessionId()
        val profileRequest = ProfileRequest(apikey, session)

        val response = raceSyncApi.fetchProfile(profileRequest)
        emit(response)
    }
        .catch {

            Log.d("tag", "Something went wrong") }
        .flowOn(Dispatchers.IO)

    override suspend fun fetchAllAircraft(apikey: String, pilotId: Int): Flow<BaseResponse<List<Aircraft>>> = flow {
        val session = dataStore.getSessionId()
        val pilotData = PilotData(pilotId, false)
        val aircraftRequest = AircraftRequest(apikey, session, pilotData)
        val response = raceSyncApi.fetchAllAircraft(aircraftRequest)

        emit(response)
    }
        .catch {

        Log.d("tag", "Something went wrong") }
        .flowOn(Dispatchers.IO)

}