package com.multigp.racesync.data.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.multigp.racesync.domain.model.io.EventSession
import com.multigp.racesync.domain.model.io.MGP_EVENT_TIMEZONE_ID
import com.multigp.racesync.domain.repositories.EventSessionBucketlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class EventSessionBucketlistImpl(
    private val storeFile: File,
    private val gson: Gson = Gson(),
    private val timeZone: TimeZone = TimeZone.getTimeZone(MGP_EVENT_TIMEZONE_ID),
) : EventSessionBucketlist {

    private val mutex = Mutex()
    private val buckets: MutableMap<String, MutableList<EventSession>> = LinkedHashMap()
    private var loaded = false

    private val _bucketedIds = MutableStateFlow<Set<String>>(emptySet())
    override val bucketedIds: StateFlow<Set<String>> = _bucketedIds.asStateFlow()

    override suspend fun warmUp() = withContext(Dispatchers.IO) {
        mutex.withLock { ensureLoadedLocked() }
    }

    override suspend fun add(session: EventSession, day: Date) = mutate {
        val list = buckets.getOrPut(dayKey(day)) { mutableListOf() }
        if (list.none { it.id == session.id }) list.add(session)
    }

    override suspend fun remove(session: EventSession, day: Date) = mutate {
        val key = dayKey(day)
        buckets[key]?.removeAll { it.id == session.id }
        if (buckets[key].isNullOrEmpty()) buckets.remove(key)
    }

    override suspend fun forDay(day: Date): List<EventSession> = withContext(Dispatchers.IO) {
        mutex.withLock {
            ensureLoadedLocked()
            buckets[dayKey(day)].orEmpty().toList()
        }
    }

    override suspend fun allSessions(): List<EventSession> = withContext(Dispatchers.IO) {
        mutex.withLock {
            ensureLoadedLocked()
            buckets.values.flatten().toList()
        }
    }

    override suspend fun clear() = mutate { buckets.clear() }

    private suspend fun mutate(op: () -> Unit) = withContext(Dispatchers.IO) {
        mutex.withLock {
            ensureLoadedLocked()
            op()
            persistLocked()
            emitIdsLocked()
        }
    }

    private fun ensureLoadedLocked() {
        if (loaded) return
        loaded = true
        if (!storeFile.exists()) return
        try {
            val text = storeFile.readText()
            if (text.isBlank()) return
            val type = object : TypeToken<Map<String, List<EventSession>>>() {}.type
            val parsed: Map<String, List<EventSession>>? = gson.fromJson(text, type)
            parsed?.forEach { (k, v) -> buckets[k] = v.toMutableList() }
            emitIdsLocked()
        } catch (t: Throwable) {
            Log.w(TAG, "Discarding unreadable bucketlist", t)
            buckets.clear()
            storeFile.delete()
        }
    }

    private fun persistLocked() {
        try {
            storeFile.parentFile?.mkdirs()
            storeFile.writeText(gson.toJson(buckets))
        } catch (t: Throwable) {
            Log.w(TAG, "Failed to persist bucketlist", t)
        }
    }

    private fun emitIdsLocked() {
        _bucketedIds.value = buckets.values.asSequence().flatten().map { it.id }.toSet()
    }

    private fun dayKey(day: Date): String =
        SimpleDateFormat("dd-MM-yyyy", Locale.US)
            .apply { timeZone = this@EventSessionBucketlistImpl.timeZone }
            .format(day)

    companion object {
        private const val TAG = "EventBucketlist"
        const val FILENAME = "mgp_io26.json"

        fun fromContext(context: Context): EventSessionBucketlistImpl =
            EventSessionBucketlistImpl(storeFile = File(context.filesDir, FILENAME))
    }
}
