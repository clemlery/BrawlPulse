package com.brawlpulse.api.features.stats

import com.brawlpulse.api.features.stats.result.GetPlayerStatsResult
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerStatsResponse(
    val name: String,
    val period: String,
    @SerialName("effective_days") val effectiveDays: Int,
    @SerialName("wins_delta") val winsDelta: Int,
    @SerialName("games_delta") val gamesDelta: Int,
    val winrate: Double,
    @SerialName("rating_delta") val ratingDelta: Int,
    @SerialName("current_rating") val currentRating: Int,
    @SerialName("snapshot_count") val snapshotCount: Int
)

private fun PlayerStats.toResponse() = PlayerStatsResponse(
    name = name,
    period = period,
    effectiveDays = effectiveDays,
    winsDelta = winsDelta,
    gamesDelta = gamesDelta,
    winrate = winrate,
    ratingDelta = ratingDelta,
    currentRating = currentRating,
    snapshotCount = snapshotCount
)

fun Route.statsRoutes(playerStatsService: PlayerStatsService) {
    get("/players/{steamId}/stats") {
        val steamId = call.parameters["steamId"]?.toLongOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid steamId")

        val period = call.request.queryParameters["period"] ?: "7d"

        when (val result = playerStatsService.getPlayerStats(steamId, period)) {
            is GetPlayerStatsResult.Success -> call.respond(HttpStatusCode.OK, result.stats.toResponse())
            is GetPlayerStatsResult.PlayerNotFound -> call.respond(HttpStatusCode.NotFound, "Player not tracked")
            is GetPlayerStatsResult.NoSnapshots -> call.respond(HttpStatusCode.NoContent)
            is GetPlayerStatsResult.InvalidPeriod ->
                call.respond(HttpStatusCode.BadRequest, "Invalid period. Supported: 7d, 4w, 6m, 1y, all")
        }
    }
}
