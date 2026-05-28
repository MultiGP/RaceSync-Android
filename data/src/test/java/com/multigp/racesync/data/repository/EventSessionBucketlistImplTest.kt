package com.multigp.racesync.data.repository

import com.multigp.racesync.domain.model.io.EventSession
import com.multigp.racesync.domain.model.io.MGP_EVENT_TIMEZONE_ID
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class EventSessionBucketlistImplTest {

    private lateinit var storeFile: File
    private val zone = TimeZone.getTimeZone(MGP_EVENT_TIMEZONE_ID)
    private val day10 = parseDay("2026-06-10")
    private val day11 = parseDay("2026-06-11")

    @Before
    fun setUp() {
        storeFile = File.createTempFile("bucketlist", ".json").also { it.delete() }
    }

    @After
    fun tearDown() {
        storeFile.delete()
    }

    @Test
    fun `add then forDay returns the session`() = runBlocking {
        val store = EventSessionBucketlistImpl(storeFile)
        store.add(session("s1"), day10)
        assertEquals(listOf("s1"), store.forDay(day10).map { it.id })
    }

    @Test
    fun `duplicate add by id is ignored`() = runBlocking {
        val store = EventSessionBucketlistImpl(storeFile)
        store.add(session("s1", activity = "Open Fly"), day10)
        store.add(session("s1", activity = "Different activity, same id"), day10)
        assertEquals(1, store.forDay(day10).size)
        assertEquals("Open Fly", store.forDay(day10).first().activity)
    }

    @Test
    fun `remove deletes only the matching session id on that day`() = runBlocking {
        val store = EventSessionBucketlistImpl(storeFile)
        store.add(session("s1"), day10)
        store.add(session("s2"), day10)
        store.add(session("s1"), day11)

        store.remove(session("s1"), day10)

        assertEquals(listOf("s2"), store.forDay(day10).map { it.id })
        assertEquals(listOf("s1"), store.forDay(day11).map { it.id })
    }

    @Test
    fun `bucketedIds flow updates as sessions are added and removed`() = runBlocking {
        val store = EventSessionBucketlistImpl(storeFile)
        assertEquals(emptySet<String>(), store.bucketedIds.value)

        store.add(session("s1"), day10)
        store.add(session("s2"), day11)
        assertEquals(setOf("s1", "s2"), store.bucketedIds.value)

        store.remove(session("s1"), day10)
        assertEquals(setOf("s2"), store.bucketedIds.value)
    }

    @Test
    fun `warmUp populates bucketedIds before any other call`() = runBlocking {
        // Persist a bucket using one instance.
        EventSessionBucketlistImpl(storeFile).add(session("s1"), day10)

        // A second instance starts empty until something prompts the lazy load.
        val cold = EventSessionBucketlistImpl(storeFile)
        assertEquals(emptySet<String>(), cold.bucketedIds.value)

        cold.warmUp()
        assertEquals(setOf("s1"), cold.bucketedIds.value)
    }

    @Test
    fun `state survives a fresh instance pointing at the same file`() = runBlocking {
        val first = EventSessionBucketlistImpl(storeFile)
        first.add(session("s1", activity = "Cached"), day10)

        val second = EventSessionBucketlistImpl(storeFile)
        val reloaded = second.forDay(day10)

        assertEquals(1, reloaded.size)
        assertEquals("Cached", reloaded.first().activity)
        assertEquals(setOf("s1"), second.bucketedIds.value)
    }

    @Test
    fun `allSessions flattens across every day`() = runBlocking {
        val store = EventSessionBucketlistImpl(storeFile)
        store.add(session("s1"), day10)
        store.add(session("s2"), day10)
        store.add(session("s3"), day11)

        assertEquals(setOf("s1", "s2", "s3"), store.allSessions().map { it.id }.toSet())
    }

    @Test
    fun `clear empties every day`() = runBlocking {
        val store = EventSessionBucketlistImpl(storeFile)
        store.add(session("s1"), day10)
        store.add(session("s2"), day11)

        store.clear()

        assertTrue(store.forDay(day10).isEmpty())
        assertTrue(store.forDay(day11).isEmpty())
        assertEquals(emptySet<String>(), store.bucketedIds.value)
    }

    @Test
    fun `corrupt store file is discarded and behaves like empty`() = runBlocking {
        storeFile.writeText("{not json")
        val store = EventSessionBucketlistImpl(storeFile)
        assertTrue(store.forDay(day10).isEmpty())
        assertFalse(storeFile.exists())

        // Should still be writable afterwards
        store.add(session("s1"), day10)
        assertEquals(listOf("s1"), store.forDay(day10).map { it.id })
    }

    @Test
    fun `day key uses dd-MM-yyyy in the event timezone`() = runBlocking {
        val store = EventSessionBucketlistImpl(storeFile)
        store.add(session("s1"), day10)
        // The file should serialize the day key as "10-06-2026".
        assertTrue(storeFile.readText().contains("\"10-06-2026\""))
    }

    private fun session(
        id: String,
        activity: String = "Open Fly",
    ) = EventSession(
        id = id,
        dayName = "Wednesday",
        activity = activity,
        trackId = "main_stage",
        statusRaw = "scheduled",
        rawDate = "2026-06-10",
        rawStartTime = "10:00",
        rawEndTime = "11:00"
    )

    private fun parseDay(iso: String): Date =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = zone }.parse(iso)!!
}
