package com.multigp.racesync.domain.repositories

import com.multigp.racesync.domain.model.io.EventSession
import kotlinx.coroutines.flow.StateFlow
import java.util.Date

/**
 * Persists favorited [EventSession]s grouped by day. Stores full session objects (not just ids)
 * so that scheduled notifications survive a schedule refresh that drops or renames a session.
 */
interface EventSessionBucketlist {

    /** All currently-bucketed session ids across every day. Hot stream for UI star-state. */
    val bucketedIds: StateFlow<Set<String>>

    /** Add a session to the bucket for [day]. No-op if a session with the same id is already there. */
    suspend fun add(session: EventSession, day: Date)

    /** Remove a session from the bucket for [day]. No-op if not present. */
    suspend fun remove(session: EventSession, day: Date)

    /** Sessions bucketed for [day], in insertion order. */
    suspend fun forDay(day: Date): List<EventSession>

    /** All bucketed sessions across every day, flattened. Used to re-schedule alarms after reboot. */
    suspend fun allSessions(): List<EventSession>

    /** Remove every bucketed session across all days. */
    suspend fun clear()
}
