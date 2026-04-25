package com.multigp.racesync.domain.model

/**
 * Lifecycle of a race within a series, used by the owner-only approver UI.
 *   - [NotApproved] — owner can approve, or remove the race from the series
 *   - [Approved]    — owner can unapprove
 *   - [Completed]   — race is finalized; no actions available
 */
enum class RaceApprovalState { NotApproved, Approved, Completed }

/** True when the API string represents truthy (`"1"` / `"true"`). */
val Race.isApproved: Boolean
    get() = approvedRaw.isApiTruthy()

/** True when the race is finalized — no further approver actions are allowed. */
val Race.isFinalized: Boolean
    get() = finalizedRaw.isApiTruthy()

/** Derives the approval state shown in a series-races row. */
fun Race.approvalState(): RaceApprovalState = when {
    isFinalized -> RaceApprovalState.Completed
    isApproved -> RaceApprovalState.Approved
    else -> RaceApprovalState.NotApproved
}

private fun String?.isApiTruthy(): Boolean =
    this == "1" || this.equals("true", ignoreCase = true)
