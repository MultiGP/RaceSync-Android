package com.multigp.racesync.data.repository

import android.util.Log
import com.multigp.racesync.domain.model.Standing
import com.multigp.racesync.domain.model.StandingSeason
import com.multigp.racesync.domain.repositories.StandingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class StandingsRepositoryImpl : StandingsRepository {

    private val cache = mutableMapOf<StandingSeason, List<Standing>>()

    override suspend fun fetchStandings(season: StandingSeason): Flow<List<Standing>> = flow {
        // Return cached data if available
        cache[season]?.let {
            emit(it)
            return@flow
        }

        val urlString = buildStandingsUrl(season)
        Log.d("Standings", "Fetching standings from: $urlString")

        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 20000
        connection.readTimeout = 20000

        try {
            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw Exception("Failed to fetch standings: HTTP $responseCode")
            }

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val rawHtml = reader.readText()
            reader.close()

            Log.d("Standings", "Response length: ${rawHtml.length}")

            val csvHeaders = buildHeaders(season)
            val csv = extractCleanCSV(rawHtml, csvHeaders)
            val standings = parseCSV(csv, season)

            Log.d("Standings", "Parsed ${standings.size} standings")

            cache[season] = standings
            emit(standings)
        } finally {
            connection.disconnect()
        }
    }.flowOn(Dispatchers.IO)

    private fun buildStandingsUrl(season: StandingSeason): String {
        val baseUrl = "https://www.multigp.com/MultiGP/views/viewZipperSeasonResults.php"

        return if (season.isDualSeason) {
            "$baseUrl?season1=${season.value}Summer&season2=${season.value}Spring&exportcsv=true"
        } else {
            "$baseUrl?season1=${season.value}&exportcsv=true"
        }
    }

    private fun buildHeaders(season: StandingSeason): List<String> {
        val headers = mutableListOf(
            "position", "firstName", "userName", "lastName", "userId",
            "chapterName", "email", "country", "season1", "season1Score"
        )
        if (season.isDualSeason) {
            headers.addAll(listOf("season2", "season2Score"))
        }
        return headers
    }

    private fun extractCleanCSV(html: String, headers: List<String>): String {
        // Decode HTML entities first
        var text = html
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&nbsp;", " ")

        // Replace <br> tags with newlines before stripping
        text = text.replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")

        // Strip all HTML tags
        text = text.replace(Regex("<[^>]*>"), "")

        // Trim leading/trailing whitespace
        text = text.trim()

        Log.d("Standings", "After HTML strip (first 500 chars): ${text.take(500)}")

        // Find start of data (first line starting with "1,")
        val dataStart = text.indexOf("\n1,")
        if (dataStart >= 0) {
            text = text.substring(dataStart)
        } else {
            val altStart = text.indexOf("1,")
            if (altStart >= 0) {
                text = text.substring(altStart)
            }
        }

        // Clean up email artifacts
        text = text.replace("[email protected]", "")

        // Inject known headers
        val headerLine = headers.joinToString(",")
        return "$headerLine\n$text"
    }

    private fun parseCSV(csv: String, season: StandingSeason): List<Standing> {
        val lines = csv.split("\n").filter { it.isNotBlank() }
        if (lines.isEmpty()) return emptyList()

        val keys = lines.first().split(",").map { it.trim() }

        Log.d("Standings", "CSV headers (${keys.size}): $keys")
        Log.d("Standings", "Total data lines: ${lines.size - 1}")
        if (lines.size > 1) {
            val firstValues = lines[1].split(",")
            Log.d("Standings", "First data line (${firstValues.size} cols): ${lines[1]}")
        }

        val standings = mutableListOf<Standing>()

        for (line in lines.drop(1)) {
            val values = line.split(",").map { it.trim() }
            if (values.size < keys.size) {
                Log.d("Standings", "Skipping line (${values.size} values vs ${keys.size} keys): $line")
                continue
            }
            if (values.size > keys.size) {
                Log.d("Standings", "Extra columns (${values.size} values vs ${keys.size} keys), using first ${keys.size}: $line")
            }

            // Zip only as many values as we have keys (ignore extra columns)
            val map = keys.zip(values).toMap()

            val standing = Standing(
                position = map["position"]?.toIntOrNull() ?: 0,
                firstName = map["firstName"] ?: "",
                userName = map["userName"] ?: "",
                lastName = map["lastName"] ?: "",
                userId = map["userId"] ?: "",
                chapterName = map["chapterName"] ?: "",
                country = map["country"] ?: "",
                season1 = map["season1"] ?: "",
                season1Score = map["season1Score"]?.toDoubleOrNull() ?: 0.0,
                season2 = if (season.isDualSeason) (map["season2"] ?: "") else "",
                season2Score = if (season.isDualSeason) (map["season2Score"]?.toDoubleOrNull() ?: 0.0) else 0.0
            )

            if (standing.position > 0) {
                standings.add(standing)
            }
        }

        return standings
    }
}
