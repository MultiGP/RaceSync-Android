package com.multigp.racesync.domain.model.io

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private fun dateFormat(): SimpleDateFormat =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone(MGP_EVENT_TIMEZONE_ID)
        isLenient = false
    }

private fun dateTimeFormat(): SimpleDateFormat =
    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).apply {
        timeZone = TimeZone.getTimeZone(MGP_EVENT_TIMEZONE_ID)
        isLenient = false
    }

/** Parses the `yyyy-MM-dd` date in the event timezone. Null if missing or malformed. */
fun EventSession.parsedDate(): Date? =
    rawDate?.takeIf { it.isNotBlank() }?.let { runCatching { dateFormat().parse(it) }.getOrNull() }

/** Combines `date` + `startTime` ("HH:mm") into an instant in the event timezone. */
fun EventSession.startInstant(): Date? = combine(rawDate, rawStartTime)

/** Combines `date` + `endTime` ("HH:mm") into an instant in the event timezone. */
fun EventSession.endInstant(): Date? = combine(rawDate, rawEndTime)

private fun combine(rawDate: String?, rawTime: String?): Date? {
    if (rawDate.isNullOrBlank() || rawTime.isNullOrBlank()) return null
    return runCatching { dateTimeFormat().parse("$rawDate $rawTime") }.getOrNull()
}

/** Inclusive list of dates from [startIso] to [endIso] in the event timezone. Empty if invalid or reversed. */
fun io26Dates(startIso: String, endIso: String): List<Date> {
    val fmt = dateFormat()
    val start = runCatching { fmt.parse(startIso) }.getOrNull() ?: return emptyList()
    val end = runCatching { fmt.parse(endIso) }.getOrNull() ?: return emptyList()
    if (start.after(end)) return emptyList()

    val tz = TimeZone.getTimeZone(MGP_EVENT_TIMEZONE_ID)
    val cal = Calendar.getInstance(tz).apply { time = start }
    val endCal = Calendar.getInstance(tz).apply { time = end }
    val out = mutableListOf<Date>()
    while (!cal.after(endCal)) {
        out += cal.time
        cal.add(Calendar.DAY_OF_YEAR, 1)
    }
    return out
}

/**
 * Returns today (interpreted in [timeZone]) if it appears in the list; otherwise the first date.
 * Null if the list is empty.
 */
fun List<Date>.initialDate(
    timeZone: TimeZone = TimeZone.getTimeZone(MGP_EVENT_TIMEZONE_ID),
    today: Date = Date()
): Date? {
    if (isEmpty()) return null
    return firstOrNull { it.isSameDay(today, timeZone) } ?: first()
}

fun Date.isSameDay(other: Date, timeZone: TimeZone): Boolean {
    val a = Calendar.getInstance(timeZone).apply { time = this@isSameDay }
    val b = Calendar.getInstance(timeZone).apply { time = other }
    return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
        a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
}

/** Sessions whose [EventSession.parsedDate] falls on the same calendar day as [day]. */
fun List<EventSession>.forDay(
    day: Date,
    timeZone: TimeZone = TimeZone.getTimeZone(MGP_EVENT_TIMEZONE_ID),
): List<EventSession> = filter { session ->
    session.parsedDate()?.isSameDay(day, timeZone) == true
}

/**
 * Drops empty / placeholder slots (null or blank activity). The upstream payload uses these
 * to represent closed time blocks (e.g. 8-10 AM every day); they aren't useful as schedule rows.
 */
fun List<EventSession>.withActivity(): List<EventSession> =
    filter { !it.activity.isNullOrBlank() }

/**
 * Collapses consecutive same-activity / same-track sessions whose gap is ≤ [gapMinutes]
 * into a single row that spans from the first start to the last end. Matches iOS's
 * `EventsController.mergedSessions` so a long qualifier isn't displayed as N short rows.
 */
fun List<EventSession>.merged(gapMinutes: Long = 5): List<EventSession> {
    val sorted = sortedBy { it.startInstant()?.time ?: Long.MAX_VALUE }
    val out = mutableListOf<EventSession>()
    for (session in sorted) {
        val matchIndex = out.indexOfLast { candidate ->
            candidate.activity == session.activity &&
                candidate.trackId == session.trackId &&
                isConsecutive(candidate, session, gapMinutes)
        }
        if (matchIndex >= 0) {
            val existing = out[matchIndex]
            out[matchIndex] = existing.copy(
                rawStartTime = earlierTime(existing.rawStartTime, session.rawStartTime),
                rawEndTime = session.rawEndTime
            )
        } else {
            out += session
        }
    }
    return out
}

/**
 * Filter sessions by activity [category]. [bucketedIds] is required for
 * [EventActivityCategory.MySchedule]; sessions whose activity doesn't bucket into the
 * requested category are dropped.
 */
fun List<EventSession>.byCategory(
    category: EventActivityCategory,
    bucketedIds: Set<String> = emptySet(),
): List<EventSession> = when (category) {
    EventActivityCategory.All -> this
    EventActivityCategory.MySchedule -> filter { it.id in bucketedIds }
    else -> filter { categorizeActivity(it.activity) == category }
}

/**
 * Filter sessions by [trackIds]. An empty set is treated as "all tracks" — the unfiltered
 * default — rather than as a never-match.
 */
fun List<EventSession>.byTracks(trackIds: Set<String>): List<EventSession> =
    if (trackIds.isEmpty()) this else filter { it.trackId in trackIds }

private fun isConsecutive(a: EventSession, b: EventSession, gapMinutes: Long): Boolean {
    val aEnd = a.endInstant()?.time ?: return false
    val bStart = b.startInstant()?.time ?: return false
    return (bStart - aEnd) <= (gapMinutes * 60 * 1000)
}

private fun earlierTime(a: String?, b: String?): String? = when {
    a == null -> b
    b == null -> a
    a <= b -> a
    else -> b
}

