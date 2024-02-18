package com.multigp.racesync.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.RaceRequest
import java.lang.Integer.max

class RacesPagingSources(
    private val apiService: RaceSyncApi,
    private val raceRequest: BaseRequest<RaceRequest>
) : PagingSource<Int, Race>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Race> {
        try {
            val currentPage = params.key ?: 0
            val pageSize = params.loadSize
            val response = apiService.fetchRaces(currentPage, pageSize, raceRequest)
            if (response.status) {
                val raceList = response.data ?: emptyList()
                return LoadResult.Page(
                    data = raceList,
                    prevKey = if (currentPage == 0) null else currentPage - 1,
                    nextKey = if (raceList.isEmpty() || raceList.count() < pageSize) null else currentPage + 1
                )
            } else {
                throw Throwable(response.errorMessage())
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Race>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        return max(0, anchorPosition)
    }
}