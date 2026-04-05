package com.multigp.racesync.domain.extensions

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.formatDate(format: String? = "EEE, MMM d @ h:mm a"): String? {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(this)
}


fun String.toDate(format: String? = null): Date? {
    // Primary: 24-hour with seconds (matches iOS DateUtil and most API responses)
    // Fallback: 12-hour with AM/PM (matches iOS secondary format)
    val formats = listOf(
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd h:mm a",
        "yyyy-MM-dd hh:mm a"
    )
    val formatsToTry = if (format != null) listOf(format) + formats else formats
    for (fmt in formatsToTry) {
        try {
            return SimpleDateFormat(fmt, Locale.getDefault()).parse(this)
        } catch (_: ParseException) {
            // Try next format
        }
    }
    return null
}

fun getDateAfterMidnight(): Date{
    val calendar = Calendar.getInstance()

    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar.time
}