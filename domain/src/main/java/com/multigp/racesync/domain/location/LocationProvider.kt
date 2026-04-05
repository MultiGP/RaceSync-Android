package com.multigp.racesync.domain.location

/**
 * Abstraction for location services. Allows the domain layer to request
 * location without depending on Android framework classes.
 *
 * Implementations can use FusedLocationProviderClient (production) or
 * return fixed coordinates (tests).
 */
interface LocationProvider {

    /**
     * Request a fresh location fix with high accuracy.
     * Returns null if location is unavailable (permissions denied, GPS off, etc.).
     */
    suspend fun getCurrentLocation(): LocationCoordinate?

    /**
     * Returns the last known location without requesting a new fix.
     * May be null or stale — prefer [getCurrentLocation] when freshness matters.
     */
    suspend fun getLastKnownLocation(): LocationCoordinate?
}

/**
 * Simple data class representing a geographic coordinate.
 * Framework-agnostic — no dependency on Android Location or LatLng.
 */
data class LocationCoordinate(
    val latitude: Double,
    val longitude: Double
)
