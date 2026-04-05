package com.multigp.racesync.data.location

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.multigp.racesync.domain.location.LocationCoordinate
import com.multigp.racesync.domain.location.LocationProvider
import kotlinx.coroutines.tasks.await

/**
 * Production implementation of [LocationProvider] backed by Google Play Services
 * FusedLocationProviderClient.
 *
 * - [getCurrentLocation] requests a fresh GPS fix at high accuracy
 *   (matching iOS's CLLocationManager.startUpdatingLocation behaviour).
 * - [getLastKnownLocation] returns whatever the system has cached,
 *   which may be null on a fresh boot or stale by hours.
 */
@SuppressLint("MissingPermission")
class FusedLocationProvider(
    private val locationClient: FusedLocationProviderClient
) : LocationProvider {

    override suspend fun getCurrentLocation(): LocationCoordinate? {
        return try {
            val cancellationToken = CancellationTokenSource()
            val location = locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).await()
            location?.let { LocationCoordinate(it.latitude, it.longitude) }
        } catch (e: Exception) {
            // Fall through to null — caller handles the fallback
            null
        }
    }

    override suspend fun getLastKnownLocation(): LocationCoordinate? {
        return try {
            val location = locationClient.lastLocation.await()
            location?.let { LocationCoordinate(it.latitude, it.longitude) }
        } catch (e: Exception) {
            null
        }
    }
}
