package com.multigp.racesync.domain.repositories

import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.BaseResponse2
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.model.requests.ProfileRequest
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun fetchProfile(): Flow<Profile>
    suspend fun fetchProfile(pilotName: String): Flow<Profile>
    suspend fun getPilotId(pilotUserName: String): String?
    suspend fun fetchAllAircraft(): Flow<List<Aircraft>>
    suspend fun fetchPilotAircrafts(pilotId: String): Flow<List<Aircraft>>
}