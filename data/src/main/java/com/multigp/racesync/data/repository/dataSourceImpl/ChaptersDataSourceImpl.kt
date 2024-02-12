package com.multigp.racesync.data.repository.dataSourceImpl

import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.repository.dataSource.ChaptersDataSource
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.requests.ChaptersRequest

class ChaptersDataSourceImpl(
    private val raceSyncApi: RaceSyncApi
) : ChaptersDataSource {
    override suspend fun fetchChapters(body: BaseRequest<ChaptersRequest>): BaseResponse<List<Chapter>> {
        return raceSyncApi.fetchChapters(body)
    }
}