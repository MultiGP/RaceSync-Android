package com.multigp.racesync.domain.model

import com.multigp.racesync.domain.extensions.toDate

/**
 * Sorts races chronologically (earliest start date first). Races with an unparseable
 * or missing `startDate` are placed at the end, preserving their relative order.
 */
fun List<Race>.sortedByStartDateAscending(): List<Race> =
    sortedWith(compareBy(nullsLast()) { it.startDate?.toDate()?.time })
