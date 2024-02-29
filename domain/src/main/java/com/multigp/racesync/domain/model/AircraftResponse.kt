package com.multigp.racesync.domain.model

data class AircraftResponse(
    val `data`: List<Aircraft>,
    val status: Boolean
)