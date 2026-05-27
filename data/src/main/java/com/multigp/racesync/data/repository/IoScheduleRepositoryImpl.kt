package com.multigp.racesync.data.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.multigp.racesync.domain.model.io.Event
import com.multigp.racesync.domain.repositories.IoScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class IoScheduleRepositoryImpl(
    private val cacheFile: File,
    private val fetchJson: suspend () -> String,
    private val gson: Gson = Gson()
) : IoScheduleRepository {

    override suspend fun fetchEvent(): Flow<Event> = flow {
        val cached = loadCached()
        if (cached != null) emit(cached)

        try {
            val json = fetchJson()
            val event = gson.fromJson(json, Event::class.java)
                ?: error("IO schedule response did not deserialize")
            writeCache(json)
            emit(event)
        } catch (t: Throwable) {
            Log.w(TAG, "IO schedule refresh failed", t)
            if (cached == null) throw t
        }
    }.flowOn(Dispatchers.IO)

    override fun clearCache() {
        if (cacheFile.exists()) cacheFile.delete()
    }

    private fun loadCached(): Event? {
        if (!cacheFile.exists()) return null
        return try {
            gson.fromJson(cacheFile.readText(), Event::class.java)
        } catch (t: Throwable) {
            Log.w(TAG, "Discarding unreadable IO schedule cache", t)
            cacheFile.delete()
            null
        }
    }

    private fun writeCache(json: String) {
        try {
            cacheFile.parentFile?.mkdirs()
            cacheFile.writeText(json)
        } catch (t: Throwable) {
            Log.w(TAG, "Failed to write IO schedule cache", t)
        }
    }

    companion object {
        private const val TAG = "IoScheduleRepo"
        const val CACHE_FILENAME = "io26_event.json"
        const val IO26_URL =
            "https://script.google.com/macros/s/AKfycbwxgL-ib1uq1EMyfkjrpvmdoMSxzKGG5x--MV4GAMExkM3UEV5FHovTM_UKbTtALQBj/exec"

        fun fromContext(context: Context): IoScheduleRepositoryImpl =
            IoScheduleRepositoryImpl(
                cacheFile = File(context.filesDir, CACHE_FILENAME),
                fetchJson = { httpGet(IO26_URL) }
            )

        private fun httpGet(urlString: String): String {
            val connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 20_000
                readTimeout = 20_000
                instanceFollowRedirects = true
            }
            try {
                val code = connection.responseCode
                if (code !in 200..299) error("IO schedule fetch failed: HTTP $code")
                return connection.inputStream.bufferedReader().use { it.readText() }
            } finally {
                connection.disconnect()
            }
        }
    }
}
