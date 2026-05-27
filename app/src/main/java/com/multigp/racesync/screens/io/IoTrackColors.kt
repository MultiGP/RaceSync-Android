package com.multigp.racesync.screens.io

import androidx.compose.ui.graphics.Color

private val DefaultTrackColor = Color(0xFF9CA3AF) // matches iOS Color.gray300 fallback

private val trackColors: Map<String, Color> = mapOf(
    "main_stage"    to Color(0xFF4A6CF7),
    "world_cup_1"   to Color(0xFFE8384F),
    "all_skills"    to Color(0xFFCA8A04),
    "whoopville"    to Color(0xFF9B59B6),
    "world_cup_2"   to Color(0xFFF06070),
    "spec"          to Color(0xFF22C55E),
    "gq_rookie"     to Color(0xFF06B6D4),
    // Server key carries the iOS typo; both forms map to the same color.
    "tiny_trainier" to Color(0xFF2DD4BF),
    "tiny_trainer"  to Color(0xFF2DD4BF),
)

fun ioTrackColor(trackId: String?): Color =
    trackId?.let { trackColors[it] } ?: DefaultTrackColor
