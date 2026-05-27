package com.multigp.racesync.screens.io

import com.multigp.racesync.domain.model.io.MGP_EVENT_TIMEZONE_ID
import com.multigp.racesync.viewmodels.IoScheduleViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * True when today (interpreted in the event timezone) is inside the IO event window —
 * i.e. within [paddingDays] of the event's start/end. Matches the iOS "4th tab visible
 * a few days before and after IO" behavior.
 */
fun isIoTabVisible(
    today: Date = Date(),
    paddingDays: Int = DEFAULT_PADDING_DAYS,
    startIso: String = IoScheduleViewModel.EVENT_START,
    endIso: String = IoScheduleViewModel.EVENT_END,
): Boolean {
    val zone = TimeZone.getTimeZone(MGP_EVENT_TIMEZONE_ID)
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = zone }
    val start = runCatching { fmt.parse(startIso) }.getOrNull() ?: return true
    val end = runCatching { fmt.parse(endIso) }.getOrNull() ?: return true

    val windowStart = Calendar.getInstance(zone).apply {
        time = start; add(Calendar.DAY_OF_YEAR, -paddingDays)
    }.time
    val windowEnd = Calendar.getInstance(zone).apply {
        time = end; add(Calendar.DAY_OF_YEAR, paddingDays)
    }.time

    return !today.before(windowStart) && !today.after(windowEnd)
}

/** ±30 days around the event — generous enough for testing but still self-hiding off-season. */
const val DEFAULT_PADDING_DAYS = 30
