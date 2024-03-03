package com.multigp.racesync.data.paging


import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.db.RaceSyncDB
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.RaceRemoteKeys
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.RaceRequest

@OptIn(ExperimentalPagingApi::class)
class RaceRemoteMediator(
    private val raceSyncApi: RaceSyncApi,
    private val raceSyncDB: RaceSyncDB,
    private val raceRequest: BaseRequest<RaceRequest>
) : RemoteMediator<Int, Race>() {

    private val raceDao = raceSyncDB.raceDao()
    private val raceRemoteKeysDao = raceSyncDB.raceRemoteKeysDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Race>): MediatorResult {
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
            val response = raceSyncApi.fetchRaces(page, state.config.pageSize, raceRequest)
            var endOfPaginationReached = false
            if (response.status) {
                val races: List<Race>? = response.data
                endOfPaginationReached = races == null || races.count() < state.config.pageSize
                races?.let { races ->
                    raceSyncDB.withTransaction {
                        //TODO:: refresh only after a certain duration. For example, 5 mins
//                        if (loadType == LoadType.REFRESH) {
//                            raceDao.deleteAllRaces()
//                            raceRemoteKeysDao.deleteAllRaceRemoteKeys()
//                        }

                        val keys = races.map { race ->
                            RaceRemoteKeys(
                                raceId = race.id,
                                prevKey = if (page <= 1) null else page - 1,
                                nextKey = page + 1
                            )
                        }
                        raceRemoteKeysDao.addAllRaceRemoteKeys(raceRemoteKeys = keys)
                        raceDao.addRaces(races = races)
                    }
                }

            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Race>,
    ): RaceRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                raceRemoteKeysDao.getRaceRemoteKeys(raceId = id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, Race>,
    ): RaceRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { race ->
                raceRemoteKeysDao.getRaceRemoteKeys(raceId = race.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, Race>,
    ): RaceRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { race ->
                raceRemoteKeysDao.getRaceRemoteKeys(raceId = race.id)
            }
    }
}