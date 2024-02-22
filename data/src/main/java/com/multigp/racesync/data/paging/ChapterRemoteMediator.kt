package com.multigp.racesync.data.paging


import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.db.RaceSyncDB
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.ChapterRemoteKeys
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.ChaptersRequest

@OptIn(ExperimentalPagingApi::class)
class ChapterRemoteMediator(
    private val raceSyncApi: RaceSyncApi,
    private val raceSyncDB: RaceSyncDB,
    private val chapterRequest: BaseRequest<ChaptersRequest>
) : RemoteMediator<Int, Chapter>() {

    private val chapterDao = raceSyncDB.chapterDao()
    private val chapterRemoteKeysDao = raceSyncDB.chapterRemoteKeysDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Chapter>): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: 0
                }

                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKeys?.prevKey
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    prevPage
                }

                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    nextPage
                }
            }
            val response = raceSyncApi.fetchChapters(page, state.config.pageSize, chapterRequest)
            var endOfPaginationReached = false
            if (response.status) {
                val chapters: List<Chapter>? = response.data
                endOfPaginationReached = chapters == null || chapters.count() < state.config.pageSize
                chapters?.let { chapters ->
                    raceSyncDB.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            chapterDao.deleteAllChapters()
                            chapterRemoteKeysDao.deleteAllChapterRemoteKeys()
                        }

                        val keys = chapters.map { chapter ->
                            ChapterRemoteKeys(
                                chapterId = chapter.id,
                                prevKey = if (page <= 1) null else page - 1,
                                nextKey = page + 1
                            )
                        }
                        chapterRemoteKeysDao.addAllChapterRemoteKeys(chapterRemoteKeys = keys)
                        chapterDao.addChapters(chapters = chapters)
                    }
                }

            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Chapter>,
    ): ChapterRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                chapterRemoteKeysDao.getChapterRemoteKeys(chapterId = id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, Chapter>,
    ): ChapterRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { Chapter ->
                chapterRemoteKeysDao.getChapterRemoteKeys(chapterId = Chapter.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, Chapter>,
    ): ChapterRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { Chapter ->
                chapterRemoteKeysDao.getChapterRemoteKeys(chapterId = Chapter.id)
            }
    }
}