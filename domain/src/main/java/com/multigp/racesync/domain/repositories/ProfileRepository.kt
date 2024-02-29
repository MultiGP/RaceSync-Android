package com.multigp.racesync.domain.repositories

import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.BaseResponse2
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.model.requests.ProfileRequest
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun fetchProfile(apikey: String): Flow<BaseResponse<Profile>>

    suspend fun fetchAllAircraft(apikey: String, pilotId: Int): Flow<BaseResponse<List<Aircraft>>>
}