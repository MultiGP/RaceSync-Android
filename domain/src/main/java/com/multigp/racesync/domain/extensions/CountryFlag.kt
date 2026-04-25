package com.multigp.racesync.domain.extensions

/**
 * Returns the regional-indicator flag emoji for the given ISO 3166-1 alpha-2 country code,
 * or an empty string if the code isn't a 2-letter ASCII string.
 */
fun countryToFlag(countryCode: String?): String {
    if (countryCode == null || countryCode.length != 2) return ""
    val upper = countryCode.uppercase()
    if (upper[0] !in 'A'..'Z' || upper[1] !in 'A'..'Z') return ""
    val first = Character.codePointAt(upper, 0) - 'A'.code + 0x1F1E6
    val second = Character.codePointAt(upper, 1) - 'A'.code + 0x1F1E6
    return String(Character.toChars(first)) + String(Character.toChars(second))
}
