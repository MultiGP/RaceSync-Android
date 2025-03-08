package com.multigp.racesync.data.repository

import android.annotation.SuppressLint
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.android.gms.location.FusedLocationProviderClient
import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.db.RaceSyncDB
import com.multigp.racesync.data.paging.ChapterRemoteMediator
import com.multigp.racesync.data.prefs.DataStoreManager
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.ChaptersRequest
import com.multigp.racesync.domain.model.requests.JoinedChapters
import com.multigp.racesync.domain.repositories.ChaptersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

@SuppressLint("MissingPermission")
class ChaptersRepositoryImpl(
    private val raceSyncApi: RaceSyncApi,
    private val raceSyncDB: RaceSyncDB,
    private val locationClient: FusedLocationProviderClient,
    private val dataStore: DataStoreManager,
    private val apiKey: String,
) : ChaptersRepository {
    private val chapterDao = raceSyncDB.chapterDao()

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun fetchChapters(pilotId: String): Flow<PagingData<Chapter>> {

        val chapterRequest = ChaptersRequest(
            joined = JoinedChapters(pilotId = pilotId)
        )

        val request = BaseRequest(
            data = chapterRequest,
            sessionId = dataStore.getSessionId()!!,
            apiKey = apiKey
        )

        val pagingSourceFactory = { chapterDao.getAllChapters() }
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = ChapterRemoteMediator(
                raceSyncApi,
                raceSyncDB,
                request
            ),
            pagingSourceFactory = pagingSourceFactory,
        ).flow
    }

    override fun fetchChapter(chapterId: String) = chapterDao.getChapter(chapterId)

    override suspend fun fetchPilotChapters(pilotId: String): Flow<List<Chapter>> {
        val request = BaseRequest(
            data = ChaptersRequest(
                joined = JoinedChapters(pilotId = pilotId)
            ),
            sessionId = dataStore.getSessionId()!!,
            apiKey = apiKey
        )

        return flow {
            val response = raceSyncApi.fetchChapters2(0, 50, request)
            if (response.isSuccessful) {
                response.body()?.let { baseResponse ->
                    if (baseResponse.status) {
                        val listOfChapters = baseResponse.data!!
                        chapterDao.addChapters(listOfChapters)
                        emit(listOfChapters)
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
}