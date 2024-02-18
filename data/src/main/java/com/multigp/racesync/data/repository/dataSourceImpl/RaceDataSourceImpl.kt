package com.multigp.racesync.data.repository.dataSourceImpl

import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.paging.JoinedRacesPagingSources
import com.multigp.racesync.data.paging.NearbyRacesPagingSources
import com.multigp.racesync.data.prefs.DataStoreManager
import com.multigp.racesync.data.repository.dataSource.RaceDataSource
import com.multigp.racesync.domain.model.BaseResponse
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.RaceRequest

class RaceDataSourceImpl(
    private val raceSyncApi: RaceSyncApi
) : RaceDataSource {
    override fun fetchNearbyRaces(body: BaseRequest<RaceRequest>): NearbyRacesPagingSources {
        return NearbyRacesPagingSources(raceSyncApi, body)
    }

    override fun fetchJoinedRaces(body: BaseRequest<RaceRequest>): JoinedRacesPagingSources {
        return JoinedRacesPagingSources(raceSyncApi, body)
    }
}