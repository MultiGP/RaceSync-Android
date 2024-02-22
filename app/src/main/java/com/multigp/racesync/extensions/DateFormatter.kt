package com.multigp.racesync.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.formatDate(format: String? = "EEE, MMM d @ h:mm a"): String?{
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(this)
}