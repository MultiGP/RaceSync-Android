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
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.ChaptersRequest
import com.multigp.racesync.domain.repositories.ChaptersRepository
import kotlinx.coroutines.flow.Flow

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
    override suspend fun fetchChapters(): Flow<PagingData<Chapter>> {

        val request = BaseRequest<ChaptersRequest>(
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
}