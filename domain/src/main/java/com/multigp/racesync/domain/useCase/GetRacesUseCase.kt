package com.multigp.racesync.domain.useCase

import android.location.Location
import com.multigp.racesync.domain.extensions.calculateDistance
import com.multigp.racesync.domain.location.LocationCoordinate
import com.multigp.racesync.domain.location.LocationProvider
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.RaceView
import com.multigp.racesync.domain.repositories.RacesRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class GetRacesUseCase(
    private val racesRepository: RacesRepository,
    private val loginInfoUserCase: GetLoginInfoUseCase,
    private val profileUseCase: GetProfileUseCase,
    private val locationProvider: LocationProvider,
) {

    // ── Nearby Races ────────────────────────────────────────────────

    /**
     * Resolves the user's current location with fallback to their profile
     * coordinates (matching iOS's LocationManager → myUser.latitude/longitude
     * fallback chain).
     */
    suspend fun resolveUserLocation(): LocationCoordinate? {
        // Priority 1: Fresh GPS fix (matches iOS startUpdatingLocation)
        locationProvider.getCurrentLocation()?.let { return it }

        // Priority 2: Last cached system location
        locationProvider.getLastKnownLocation()?.let { return it }

        // Priority 3: User profile coordinates (matches iOS myUser.latitude/longitude)
        try {
            val profile = profileUseCase().first()
            if (profile.latitude != null && profile.longitude != null) {
                return LocationCoordinate(profile.latitude, profile.longitude)
            }
        } catch (_: Exception) {
            // Profile unavailable — fall through to null
        }

        return null
    }

    /**
     * Fetches nearby races: resolves location, converts radius to miles,
     * calls the API, and calculates display distances for each race.
     *
     * Returns a [NearbyRacesResult] containing both the races and the
     * coordinate used, so the caller can cache it for distance display.
     */
    suspend fun fetchNearbyRaces(): NearbyRacesResult {
        val coordinate = resolveUserLocation()
            ?: return NearbyRacesResult(emptyList(), null)

        val (radius, unit) = racesRepository.fetchSearchRadius().first()
        val radiusMiles = if (unit == "mi") radius else radius * 0.621371

        val races = racesRepository.fetchNearbyRaces(coordinate, radiusMiles)

        // Calculate display distances for each race (like iOS's RaceViewModel.distance)
        val racesWithDistance = races.map { race ->
            if (race.latitude != null && race.longitude != null) {
                val distanceMiles = LatLng(race.latitude, race.longitude)
                    .calculateDistance(LatLng(coordinate.latitude, coordinate.longitude))
                race.distance = if (unit == "mi") {
                    String.format("%.2f mi", distanceMiles)
                } else {
                    String.format("%.2f km", distanceMiles / 0.621371)
                }
            }
            race
        }

        // Sort by startDate ascending (soonest first) — matches iOS .descending sorting
        val sorted = racesWithDistance.sortedBy { it.formattedStartDate }

        return NearbyRacesResult(sorted, coordinate)
    }

    // ── Joined Races ────────────────────────────────────────────────

    /**
     * Fetches joined races for the authenticated user.
     * Matches iOS: resolves pilotId, single API call with joined + upcoming filters,
     * sorted by startDate ascending (soonest first) — iOS's .descending sorting.
     */
    suspend fun fetchJoinedRaces(): List<Race> {
        val loginInfo = loginInfoUserCase().first()
        val races = racesRepository.fetchJoinedRaces(loginInfo.second!!.id)
        return races.sortedBy { it.formattedStartDate }
    }

    suspend fun fetchPilotRaces(pilotUserName: String): Flow<List<Race>> {
        profileUseCase.getPilotId(pilotUserName)?.let { pilotId ->
            return racesRepository.fetchPilotRaces(pilotId)
        } ?: run {
            throw Exception("Invalid pilot username")
        }
    }

    // ── Chapter Races ───────────────────────────────────────────────

    suspend fun fetchJoinedChapterRaces(): Flow<List<Race>> {
        val loginInfo = loginInfoUserCase().first()
        return racesRepository.fetchJoinedChapterRaces(loginInfo.second!!.id)
    }

    // ── Single Race ─────────────────────────────────────────────────

    suspend fun fetchRace(raceId: String): Flow<Race> {
        return racesRepository.fetchRace(raceId)
    }

    // ── Search Radius ───────────────────────────────────────────────

    suspend fun saveSearchRadius(radius: Double, unit: String) {
        racesRepository.saveSearchRadius(radius, unit)
    }

    suspend fun fetchSearchRadius(): Double {
        val (radius, unit) = racesRepository.fetchSearchRadius().first()
        return if (unit == "mi") radius else radius * 0.621371
    }

    suspend fun fetchRaceFeedOptions() = racesRepository.fetchSearchRadius()

    // ── Join / Resign ───────────────────────────────────────────────

    suspend fun joinRace(raceId: String, aircraftId: String): Flow<Boolean> {
        val pilotId = loginInfoUserCase().first().second!!.id
        return racesRepository.joinRace(pilotId, raceId, aircraftId)
    }

    suspend fun resignFromRace(raceId: String): Flow<Boolean> {
        return racesRepository.resignFromRace(raceId)
    }

    // ── Race Details ────────────────────────────────────────────────

    suspend fun fetchRaceView(raceId: String): Flow<Triple<Profile, Race, RaceView>> {
        return racesRepository.fetchRaceView(raceId)
            .combine(profileUseCase()) { raceView, profile ->
                Pair(profile, raceView)
            }.combine(fetchRace(raceId)) { (profile, raceView), race ->
                Triple(profile, race, raceView)
            }
    }

    suspend fun calculateRaceDistace(race: Race, currentLocation: Location) {
        racesRepository.calculateRaceDistance(race, currentLocation)
    }
}

/**
 * Encapsulates the result of a nearby races fetch.
 * Keeping the coordinate lets the ViewModel avoid re-resolving location
 * when the user merely scrolls or the screen recomposes.
 */
data class NearbyRacesResult(
    val races: List<Race>,
    val coordinate: LocationCoordinate?
)
