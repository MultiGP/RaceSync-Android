package com.multigp.racesync.data.repository

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class IoScheduleRepositoryImplTest {

    private lateinit var cacheFile: File

    @Before
    fun setUp() {
        cacheFile = File.createTempFile("io26_event", ".json").also { it.delete() }
    }

    @After
    fun tearDown() {
        cacheFile.delete()
    }

    @Test
    fun `fetchEvent emits network result and caches it when cache is empty`() = runBlocking {
        val repo = IoScheduleRepositoryImpl(cacheFile, fetchJson = { sampleJson("Spec Race") })

        val results = repo.fetchEvent().toList()

        assertEquals(1, results.size)
        assertEquals("IO 2026", results[0].name)
        assertEquals("Spec Race", results[0].sessions.first().activity)
        assertTrue(cacheFile.exists())
    }

    @Test
    fun `fetchEvent emits cached then refreshed events when both succeed`() = runBlocking {
        cacheFile.writeText(sampleJson("Cached Race"))
        val repo = IoScheduleRepositoryImpl(cacheFile, fetchJson = { sampleJson("Fresh Race") })

        val activities = repo.fetchEvent().toList().map { it.sessions.first().activity }

        assertEquals(listOf("Cached Race", "Fresh Race"), activities)
        assertTrue(cacheFile.readText().contains("Fresh Race"))
    }

    @Test
    fun `fetchEvent swallows network failure when cache exists`() = runBlocking {
        cacheFile.writeText(sampleJson("Cached Race"))
        val repo = IoScheduleRepositoryImpl(
            cacheFile,
            fetchJson = { throw RuntimeException("offline") }
        )

        val activities = repo.fetchEvent().toList().map { it.sessions.first().activity }

        assertEquals(listOf("Cached Race"), activities)
    }

    @Test(expected = RuntimeException::class)
    fun `fetchEvent propagates failure when no cache exists`(): Unit = runBlocking {
        val repo = IoScheduleRepositoryImpl(
            cacheFile,
            fetchJson = { throw RuntimeException("offline") }
        )
        repo.fetchEvent().toList()
    }

    @Test
    fun `fetcher is invoked once per fetchEvent call`() = runBlocking {
        val calls = AtomicInteger(0)
        val repo = IoScheduleRepositoryImpl(cacheFile, fetchJson = {
            calls.incrementAndGet()
            sampleJson("First")
        })

        repo.fetchEvent().toList()
        repo.fetchEvent().toList()

        assertEquals(2, calls.get())
    }

    @Test
    fun `clearCache removes the on-disk file`() {
        cacheFile.writeText(sampleJson("Cached Race"))
        val repo = IoScheduleRepositoryImpl(cacheFile, fetchJson = { sampleJson("ignored") })

        repo.clearCache()

        assertFalse(cacheFile.exists())
    }

    @Test
    fun `corrupt cache is discarded silently and network result is returned`() = runBlocking {
        cacheFile.writeText("{not valid json")
        val repo = IoScheduleRepositoryImpl(cacheFile, fetchJson = { sampleJson("Fresh Race") })

        val activities = repo.fetchEvent().toList().map { it.sessions.first().activity }

        assertEquals(listOf("Fresh Race"), activities)
    }

    private fun sampleJson(activity: String): String = """
        {
          "event": "IO 2026",
          "venue": "Muncie, Indiana",
          "lastUpdated": "2026-05-27T08:00:00Z",
          "tracks": [
            { "id": "main_stage", "name": "Main Stage", "location": "Indoor" }
          ],
          "sessions": [
            {
              "id": "s1",
              "day": "Wednesday",
              "activity": "$activity",
              "trackId": "main_stage",
              "status": "scheduled",
              "date": "2026-06-10",
              "startTime": "10:00",
              "endTime": "11:00"
            }
          ]
        }
    """.trimIndent()
}
