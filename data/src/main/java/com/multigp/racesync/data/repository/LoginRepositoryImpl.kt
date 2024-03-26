package com.multigp.racesync.data.repository

import com.multigp.racesync.data.prefs.DataStoreManager
import com.multigp.racesync.data.repository.dataSource.OnboardingDataSource
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.requests.LoginRequest
import com.multigp.racesync.domain.model.BaseResponse2
import com.multigp.racesync.domain.model.UserInfo
import com.multigp.racesync.domain.model.requests.LogoutRequest
import com.multigp.racesync.domain.repositories.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LoginRepositoryImpl(
    private val onboardingDataSource: OnboardingDataSource,
    private val dataStore: DataStoreManager,
    private val apiKey: String
) :
    LoginRepository {
    override suspend fun login(
        body: LoginRequest
    ): Flow<Result<BaseResponse2<UserInfo>>> = flow {
        val loginResponse = onboardingDataSource.login(body)
        if (loginResponse.status == 1) {
            dataStore.saveUserInfo(loginResponse.data)
            dataStore.saveSessionId(loginResponse.sessionId)
        }
        emit(Result.success(loginResponse))
    }
        .catch { emit(Result.failure(it)) }
        .flowOn(Dispatchers.IO)

    override suspend fun getLoginInfo(): Flow<Pair<String?, UserInfo?>> = flow {
        val userInfo = dataStore.getUserInfo()
        val sessionId = dataStore.getSessionId()
        if (userInfo != null && sessionId != null) {
            emit(Pair(sessionId, userInfo))
        }else{
            emit(Pair(null, null))
        }
    }
        .catch { }
        .flowOn(Dispatchers.IO)


    override suspend fun logout() = flow{
        val sessionId = dataStore.getSessionId()
        val request = LogoutRequest(sessionId = sessionId!!, apiKey = apiKey)
        val response = onboardingDataSource.logout(request)
        if (response.isSuccessful) {
            response.body()?.let { baseResponse ->
                if (baseResponse.status) {
                    dataStore.clearSession()
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