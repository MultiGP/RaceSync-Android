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
 * i.e. between [paddingDaysBefore] days before the event starts and the end of the day
 * [paddingDaysAfter] days after it ends. Mirrors the iOS "4th tab visible a few days
 * before and after IO" behavior. The cutoff is inclusive of the final day, so an
 * `after = 3` window for an event ending Jun 14 keeps the tab visible through 23:59
 * on Jun 17 and hides it starting midnight Jun 18.
 */
fun isIoTabVisible(
    today: Date = Date(),
    paddingDaysBefore: Int = DEFAULT_PADDING_DAYS_BEFORE,
    paddingDaysAfter: Int = DEFAULT_PADDING_DAYS_AFTER,
    startIso: String = IoScheduleViewModel.EVENT_START,
    endIso: String = IoScheduleViewModel.EVENT_END,
): Boolean {
    val zone = TimeZone.getTimeZone(MGP_EVENT_TIMEZONE_ID)
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = zone }
    val start = runCatching { fmt.parse(startIso) }.getOrNull() ?: return true
    val end = runCatching { fmt.parse(endIso) }.getOrNull() ?: return true

    val windowStart = Calendar.getInstance(zone).apply {
        time = start
        add(Calendar.DAY_OF_YEAR, -paddingDaysBefore)
    }.time
    val windowEnd = Calendar.getInstance(zone).apply {
        time = end
        add(Calendar.DAY_OF_YEAR, paddingDaysAfter)
        // Roll forward to the very end of the cutoff day so the comparison is inclusive.
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.time

    return !today.before(windowStart) && !today.after(windowEnd)
}

/** 30 days of lead-up — plenty of time to plan & bookmark sessions. */
const val DEFAULT_PADDING_DAYS_BEFORE = 30

/** 3 days of grace after the event closes (event runs Jun 10–14, tab hides after Jun 17). */
const val DEFAULT_PADDING_DAYS_AFTER = 3
