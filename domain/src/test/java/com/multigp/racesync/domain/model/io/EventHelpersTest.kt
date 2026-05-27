package com.multigp.racesync.domain.model.io

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class EventHelpersTest {

    private val zone: TimeZone = TimeZone.getTimeZone(MGP_EVENT_TIMEZONE_ID)

    @Test
    fun `io26Dates returns inclusive day list`() {
        val dates = io26Dates("2026-06-10", "2026-06-14")
        assertEquals(5, dates.size)
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = zone }
        assertEquals(
            listOf("2026-06-10", "2026-06-11", "2026-06-12", "2026-06-13", "2026-06-14"),
            dates.map { fmt.format(it) }
        )
    }

    @Test
    fun `io26Dates returns empty when end is before start`() {
        assertTrue(io26Dates("2026-06-14", "2026-06-10").isEmpty())
    }

    @Test
    fun `io26Dates returns empty for malformed input`() {
        assertTrue(io26Dates("not-a-date", "2026-06-14").isEmpty())
        assertTrue(io26Dates("2026-06-10", "").isEmpty())
    }

    @Test
    fun `parsedDate interprets the raw string in the event timezone`() {
        val date = session(rawDate = "2026-06-10").parsedDate()
        assertNotNull(date)
        val cal = Calendar.getInstance(zone).apply { time = date!! }
        assertEquals(2026, cal.get(Calendar.YEAR))
        assertEquals(Calendar.JUNE, cal.get(Calendar.MONTH))
        assertEquals(10, cal.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun `startInstant combines date and time in the event timezone`() {
        val instant = session(rawDate = "2026-06-10", rawStartTime = "13:30").startInstant()
        assertNotNull(instant)
        val cal = Calendar.getInstance(zone).apply { time = instant!! }
        assertEquals(13, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(30, cal.get(Calendar.MINUTE))
    }

    @Test
    fun `startInstant returns null when either field is missing`() {
        assertNull(session(rawDate = null, rawStartTime = "13:00").startInstant())
        assertNull(session(rawDate = "2026-06-10", rawStartTime = null).startInstant())
    }

    @Test
    fun `initialDate returns today when present in the list`() {
        val dates = io26Dates("2026-06-10", "2026-06-14")
        val today = dates[2] // 2026-06-12
        assertEquals(today, dates.initialDate(zone, today))
    }

    @Test
    fun `initialDate falls back to first date when today is outside the range`() {
        val dates = io26Dates("2026-06-10", "2026-06-14")
        val before = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            .apply { timeZone = zone }.parse("2026-05-01")!!
        assertEquals(dates.first(), dates.initialDate(zone, before))
    }

    @Test
    fun `initialDate returns null for an empty list`() {
        assertNull(emptyList<Date>().initialDate(zone, Date()))
    }

    @Test
    fun `EventStatus maps known and unknown values`() {
        assertEquals(EventStatus.Scheduled, EventStatus.from("scheduled"))
        assertEquals(EventStatus.Closed, EventStatus.from("closed"))
        assertEquals(EventStatus.Scheduled, EventStatus.from("SCHEDULED"))
        assertEquals(EventStatus.Closed, EventStatus.from("unknown"))
        assertEquals(EventStatus.Closed, EventStatus.from(null))
    }

    @Test
    fun `Gson deserializes a full event payload`() {
        val json = """
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
                  "activity": "Open Fly",
                  "trackId": "main_stage",
                  "status": "scheduled",
                  "date": "2026-06-10",
                  "startTime": "10:00",
                  "endTime": "11:00"
                }
              ]
            }
        """.trimIndent()

        val event = Gson().fromJson(json, Event::class.java)
        assertEquals("IO 2026", event.name)
        assertEquals(1, event.tracks.size)
        assertEquals("Main Stage", event.tracks[0].name)
        val s = event.sessions.first()
        assertEquals("s1", s.id)
        assertEquals(EventStatus.Scheduled, s.status)
        assertEquals("Open Fly", s.activity)
        assertNotNull(s.startInstant())
        assertNotNull(s.endInstant())
    }

    @Test
    fun `forDay keeps only sessions on the requested calendar day`() {
        val wed = session(id = "wed", rawDate = "2026-06-10")
        val thu = session(id = "thu", rawDate = "2026-06-11")
        val noDate = session(id = "nodate", rawDate = null)
        val day = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            .apply { timeZone = zone }.parse("2026-06-10")!!

        val ids = listOf(wed, thu, noDate).forDay(day, zone).map { it.id }
        assertEquals(listOf("wed"), ids)
    }

    @Test
    fun `merged collapses consecutive same-activity same-track sessions`() {
        val sessions = listOf(
            session("a", activity = "Spec", trackId = "spec", rawStartTime = "10:00", rawEndTime = "10:30"),
            session("b", activity = "Spec", trackId = "spec", rawStartTime = "10:35", rawEndTime = "11:00"),
            session("c", activity = "Spec", trackId = "spec", rawStartTime = "11:00", rawEndTime = "11:30")
        )
        val merged = sessions.merged(gapMinutes = 5)
        assertEquals(1, merged.size)
        assertEquals("10:00", merged.first().rawStartTime)
        assertEquals("11:30", merged.first().rawEndTime)
    }

    @Test
    fun `merged does not collapse different activities`() {
        val sessions = listOf(
            session("a", activity = "Spec", trackId = "spec", rawStartTime = "10:00", rawEndTime = "10:30"),
            session("b", activity = "Open Fly", trackId = "spec", rawStartTime = "10:30", rawEndTime = "11:00")
        )
        val merged = sessions.merged()
        assertEquals(2, merged.size)
    }

    @Test
    fun `merged does not collapse when the gap is larger than the threshold`() {
        val sessions = listOf(
            session("a", activity = "Spec", trackId = "spec", rawStartTime = "10:00", rawEndTime = "10:30"),
            session("b", activity = "Spec", trackId = "spec", rawStartTime = "10:45", rawEndTime = "11:00")
        )
        val merged = sessions.merged(gapMinutes = 5)
        assertEquals(2, merged.size)
    }

    @Test
    fun `filtered All returns everything`() {
        val sessions = listOf(
            session("a", activity = "Spec Race"),
            session("b", activity = "Open Fly")
        )
        assertEquals(sessions, sessions.filtered(EventSessionFilter.All))
    }

    @Test
    fun `filtered Spec matches both 'spec' and 'AER' case-insensitively`() {
        val sessions = listOf(
            session("a", activity = "Spec Race"),
            session("b", activity = "AER Qualifier"),
            session("c", activity = "Open Fly")
        )
        val ids = sessions.filtered(EventSessionFilter.Spec).map { it.id }
        assertEquals(listOf("a", "b"), ids)
    }

    @Test
    fun `filtered OpenFly matches 'open fly' or 'openfly'`() {
        val sessions = listOf(
            session("a", activity = "Open Fly Block"),
            session("b", activity = "OpenFly Practice"),
            session("c", activity = "Spec Race")
        )
        val ids = sessions.filtered(EventSessionFilter.OpenFly).map { it.id }
        assertEquals(listOf("a", "b"), ids)
    }

    @Test
    fun `filtered MySchedule keeps only sessions whose id is bucketed`() {
        val sessions = listOf(
            session("a"),
            session("b"),
            session("c")
        )
        val ids = sessions.filtered(EventSessionFilter.MySchedule, bucketedIds = setOf("b"))
            .map { it.id }
        assertEquals(listOf("b"), ids)
    }

    @Test
    fun `EventSessionFilter from title round-trips and falls back to All`() {
        assertEquals(EventSessionFilter.MySchedule, EventSessionFilter.fromTitle("My Schedule"))
        assertEquals(EventSessionFilter.Spec, EventSessionFilter.fromTitle("spec"))
        assertEquals(EventSessionFilter.All, EventSessionFilter.fromTitle("unknown"))
        assertEquals(EventSessionFilter.All, EventSessionFilter.fromTitle(null))
    }

    private fun session(
        id: String = "s1",
        rawDate: String? = "2026-06-10",
        rawStartTime: String? = "10:00",
        rawEndTime: String? = "11:00",
        status: String = "scheduled",
        activity: String = "Open Fly",
        trackId: String = "main_stage",
    ) = EventSession(
        id = id,
        dayName = "Wednesday",
        activity = activity,
        trackId = trackId,
        statusRaw = status,
        rawDate = rawDate,
        rawStartTime = rawStartTime,
        rawEndTime = rawEndTime
    )
}
