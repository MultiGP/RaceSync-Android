package com.multigp.racesync.data.repository.dataSourceImpl

import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.repository.dataSource.RaceDataSource
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.RaceRequest

class RaceDataSourceImpl(
    private val raceSyncApi: RaceSyncApi
) : RaceDataSource {
    override suspend fun fetchRaces(body: BaseRequest<RaceRequest>): BaseResponse<List<Race>> {
        return raceSyncApi.fetchRaces(0, 50, body)
    }
}