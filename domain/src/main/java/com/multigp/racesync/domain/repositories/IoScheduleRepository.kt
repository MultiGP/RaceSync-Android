package com.multigp.racesync.domain.repositories

import com.multigp.racesync.domain.model.io.Event
import kotlinx.coroutines.flow.Flow

interface IoScheduleRepository {
    /**
     * Cache-first stream. Emits the cached [Event] immediately if one exists,
     * then the freshly-fetched event after a network refresh.
     * Throws only when no cache exists and the network fetch fails.
     */
    suspend fun fetchEvent(): Flow<Event>

    /** Removes the on-disk cache. Next [fetchEvent] will be network-only. */
    fun clearCache()
}
