package com.multigp.racesync.domain.model

/**
 * True when the given user owns this series and can therefore approve / unapprove / remove
 * its races. Returns false for null user ids or null series owners.
 */
fun Series.canBeEditedBy(userId: String?): Boolean =
    !userId.isNullOrBlank() && ownerId == userId
