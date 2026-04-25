package com.multigp.racesync.domain.extensions

import java.util.Locale

/**
 * Formats a lap time given as a "seconds" string into a display string:
 *   - Under 60 s → "12.345s"
 *   - 60 s and over → "1:02.345"
 *
 * Returns null when the input cannot be parsed or is non-positive.
 */
fun formatLapTime(seconds: String?): String? {
    val value = seconds?.toDoubleOrNull() ?: return null
    if (value <= 0) return null

    return if (value < 60.0) {
        String.format(Locale.US, "%.3fs", value)
    } else {
        val minutes = value.toInt() / 60
        val remainder = value - minutes * 60
        String.format(Locale.US, "%d:%06.3f", minutes, remainder)
    }
}
