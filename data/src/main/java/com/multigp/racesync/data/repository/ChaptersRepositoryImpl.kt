package com.multigp.racesync.data.repository

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.multigp.racesync.data.prefs.DataStoreManager
import com.multigp.racesync.data.repository.dataSource.ChaptersDataSource
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.ChaptersRequest
import com.multigp.racesync.domain.model.requests.JoinedChapters
import com.multigp.racesync.domain.model.requests.NearbyChapters
import com.multigp.racesync.domain.repositories.ChaptersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
class ChaptersRepositoryImpl(
    private val chaptersDataSource: ChaptersDataSource,
    private val locationClient: FusedLocationProviderClient,
    private val dataStore: DataStoreManager
) :
    ChaptersRepository {
    override suspend fun fetchChapters(): Flow<Result<BaseResponse<List<Chapter>>>> {
        return flow {
            val request = BaseRequest<ChaptersRequest>(
                sessionId = dataStore.getSessionId()!!,
                apiKey = "da65552b-0de4-331a-04c2-6991bae6fe27"
            )
            val response = chaptersDataSource.fetchChapters(request)
            emit(Result.success(response))
        }
            .catch { emit(Result.failure(it)) }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun fetchChapters(radius: Double): Flow<Result<BaseResponse<List<Chapter>>>> {
        return flow {
            val location = locationClient.lastLocation.await()
            val chaptersRequest = ChaptersRequest(
                joined = null,
                nearBy = NearbyChapters(location.latitude, location.longitude, radius)
            )
            val request = BaseRequest<ChaptersRequest>(
                sessionId = dataStore.getSessionId()!!,
                apiKey = "da65552b-0de4-331a-04c2-6991bae6fe27",
                data = chaptersRequest
            )
            val response = chaptersDataSource.fetchChapters(request)
            emit(Result.success(response))
        }
            .catch { emit(Result.failure(it)) }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun fetchChapters(pilotId: String): Flow<Result<BaseResponse<List<Chapter>>>> {
        return flow {
            val chaptersRequest = ChaptersRequest(
                joined = JoinedChapters(pilotId = pilotId)
            )
            val request = BaseRequest<ChaptersRequest>(
                sessionId = dataStore.getSessionId()!!,
                apiKey = "da65552b-0de4-331a-04c2-6991bae6fe27",
                data = chaptersRequest
            )
            val response = chaptersDataSource.fetchChapters(request)
            emit(Result.success(response))
        }
            .catch { emit(Result.failure(it)) }
            .flowOn(Dispatchers.IO)
    }
}