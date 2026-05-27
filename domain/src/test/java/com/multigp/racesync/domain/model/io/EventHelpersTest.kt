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
    fun `byCategory All returns everything`() {
        val sessions = listOf(
            session("a", activity = "Pro Spec Qualifying"),
            session("b", activity = "Open Fly")
        )
        assertEquals(sessions, sessions.byCategory(EventActivityCategory.All))
    }

    @Test
    fun `byCategory OpenFly matches both spellings and beats spec keyword`() {
        val sessions = listOf(
            session("a", activity = "Open Fly Block"),
            session("b", activity = "OpenFly Practice"),
            session("c", activity = "Pro Spec Qualifying")
        )
        val ids = sessions.byCategory(EventActivityCategory.OpenFly).map { it.id }
        assertEquals(listOf("a", "b"), ids)
    }

    @Test
    fun `byCategory Spec matches Pro Spec, IO Spec, AER, Freedom Spec`() {
        val sessions = listOf(
            session("ps", activity = "Pro Spec Qualifying"),
            session("io", activity = "IO Spec Oval"),
            session("aer", activity = "AER Individual & Team Qualifying"),
            session("fs", activity = "Freedom Spec Oval"),
            session("noise", activity = "Open Fly"),
        )
        val ids = sessions.byCategory(EventActivityCategory.Spec).map { it.id }
        assertEquals(listOf("ps", "io", "aer", "fs"), ids)
    }

    @Test
    fun `byCategory WorldCup matches Qualifying, Practice, Finals, Rain Day`() {
        val sessions = listOf(
            session("q", activity = "World Cup Qualifying"),
            session("p", activity = "World Cup Practice"),
            session("f", activity = "World Cup Finals Brackets of 8, 64 pilots"),
            session("rd", activity = "World Cup Rain Day"),
            session("noise", activity = "Open Fly"),
        )
        val ids = sessions.byCategory(EventActivityCategory.WorldCup).map { it.id }
        assertEquals(listOf("q", "p", "f", "rd"), ids)
    }

    @Test
    fun `byCategory GlobalQualifier tolerates the upstream typo`() {
        val sessions = listOf(
            session("a", activity = "Global Qualifier"),
            session("b", activity = "Second Global Qualifier"),
            session("typo", activity = "Global Qualifer"), // upstream typo
            session("noise", activity = "Open Fly"),
        )
        val ids = sessions.byCategory(EventActivityCategory.GlobalQualifier).map { it.id }
        assertEquals(listOf("a", "b", "typo"), ids)
    }

    @Test
    fun `byCategory ClassRaces covers lipo, voltage, hammers, cracked, burnt + burn typo`() {
        val sessions = listOf(
            session("l", activity = "Lipo Fires Race"),
            session("v", activity = "High Voltage Race"),
            session("h", activity = "Hammers Race"),
            session("c", activity = "Cracked Props Race"),
            session("b", activity = "Burnt Motors Race"),
            session("typo", activity = "Burn Motors Race"), // upstream typo
        )
        val ids = sessions.byCategory(EventActivityCategory.ClassRaces).map { it.id }
        assertEquals(listOf("l", "v", "h", "c", "b", "typo"), ids)
    }

    @Test
    fun `byCategory Whoop matches Qualifying, Finals, Last Chance`() {
        val sessions = listOf(
            session("q", activity = "Whoop Qualifying"),
            session("f", activity = "Whoop Finals"),
            session("lc", activity = "Last Chance Whoop Qualifying"),
            session("noise", activity = "Open Fly"),
        )
        val ids = sessions.byCategory(EventActivityCategory.Whoop).map { it.id }
        assertEquals(listOf("q", "f", "lc"), ids)
    }

    @Test
    fun `byCategory MySchedule keeps only sessions whose id is bucketed`() {
        val sessions = listOf(
            session("a"),
            session("b"),
            session("c"),
        )
        val ids = sessions.byCategory(EventActivityCategory.MySchedule, bucketedIds = setOf("b"))
            .map { it.id }
        assertEquals(listOf("b"), ids)
    }

    @Test
    fun `byCategory drops null activities and special one-offs from buckets`() {
        val sessions = listOf(
            session("null", activity = null),
            session("movie", activity = "Movie Night After the Racing"),
            session("masters", activity = "40-49 Masters"),
        )
        // None of these match any specific category — only visible under All.
        assertEquals(emptyList<EventSession>(), sessions.byCategory(EventActivityCategory.WorldCup))
        assertEquals(emptyList<EventSession>(), sessions.byCategory(EventActivityCategory.Spec))
        assertEquals(sessions, sessions.byCategory(EventActivityCategory.All))
    }

    @Test
    fun `byTracks with empty set is the unfiltered default`() {
        val sessions = listOf(
            session("a", trackId = "main_stage"),
            session("b", trackId = "spec"),
        )
        assertEquals(sessions, sessions.byTracks(emptySet()))
    }

    @Test
    fun `byTracks keeps only sessions on the selected tracks`() {
        val sessions = listOf(
            session("a", trackId = "main_stage"),
            session("b", trackId = "spec"),
            session("c", trackId = "world_cup_1"),
        )
        val ids = sessions.byTracks(setOf("main_stage", "world_cup_1")).map { it.id }
        assertEquals(listOf("a", "c"), ids)
    }

    @Test
    fun `EventActivityCategory from title round-trips and falls back to All`() {
        assertEquals(EventActivityCategory.MySchedule, EventActivityCategory.fromTitle("My Schedule"))
        assertEquals(EventActivityCategory.WorldCup, EventActivityCategory.fromTitle("World Cup"))
        assertEquals(EventActivityCategory.ClassRaces, EventActivityCategory.fromTitle("class races"))
        assertEquals(EventActivityCategory.All, EventActivityCategory.fromTitle("unknown"))
        assertEquals(EventActivityCategory.All, EventActivityCategory.fromTitle(null))
    }

    @Test
    fun `categorizeActivity returns null for missing or unknown activities`() {
        assertEquals(null, categorizeActivity(null))
        assertEquals(null, categorizeActivity(""))
        assertEquals(null, categorizeActivity("Movie Night"))
        assertEquals(null, categorizeActivity("Kung Fu Fightn"))
    }

    private fun session(
        id: String = "s1",
        rawDate: String? = "2026-06-10",
        rawStartTime: String? = "10:00",
        rawEndTime: String? = "11:00",
        status: String = "scheduled",
        activity: String? = "Open Fly",
        trackId: String? = "main_stage",
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
