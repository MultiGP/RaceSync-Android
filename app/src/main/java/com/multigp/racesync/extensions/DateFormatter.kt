package com.multigp.racesync.extensions

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.formatDate(format: String? = "EEE, MMM d @ h:mm a"): String? {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(this)
}


fun String.toDate(format: String? = "yyyy-MM-dd hh:mm a"): Date? {
    val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
    return try {
        formatter.parse(this)
    } catch (e: ParseException) {
        null
    }
}