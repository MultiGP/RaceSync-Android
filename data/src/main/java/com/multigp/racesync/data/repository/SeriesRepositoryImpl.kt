package com.multigp.racesync.data.repository

import com.multigp.racesync.data.api.RaceSyncApi
import com.multigp.racesync.data.prefs.DataStoreManager
import com.multigp.racesync.domain.model.Series
import com.multigp.racesync.domain.model.requests.BaseRequest
import com.multigp.racesync.domain.model.requests.SeriesRequest
import com.multigp.racesync.domain.repositories.SeriesRepository

class SeriesRepositoryImpl(
    private val raceSyncApi: RaceSyncApi,
    private val dataStore: DataStoreManager,
    private val apiKey: String
) : SeriesRepository {

    override suspend fun fetchSeries(): List<Series> {
        val request = BaseRequest(
            apiKey = apiKey,
            data = SeriesRequest(),
            sessionId = dataStore.getSessionId()!!
        )

        val response = raceSyncApi.fetchSeries(
            page = 0,
            pageSize = 100,
            request = request
        )

        if (!response.status) {
            throw Exception(response.errorMessage())
        }
        return response.data ?: emptyList()
    }
}
