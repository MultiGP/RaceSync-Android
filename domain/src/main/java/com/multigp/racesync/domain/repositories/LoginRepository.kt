package com.multigp.racesync.domain.repositories

import com.multigp.racesync.domain.model.requests.LoginRequest
import com.multigp.racesync.domain.model.BaseResponse2
import com.multigp.racesync.domain.model.UserInfo
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun login(body: LoginRequest): Flow<Result<BaseResponse2<UserInfo>>>
    suspend fun getLoginInfo(): Flow<Pair<String?, UserInfo?>>

    suspend fun logout(): Flow<Boolean>

    suspend fun clearSession()
}