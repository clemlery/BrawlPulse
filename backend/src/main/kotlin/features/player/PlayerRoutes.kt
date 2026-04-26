package com.brawlpulse.api.features.player

import com.brawlpulse.api.features.player.exceptions.BrawlhallaApiUnavailableException
import com.brawlpulse.api.features.player.exceptions.SteamIdNotLinkedException
import com.brawlpulse.api.features.player.result.AddPlayerResult
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class AddPlayerRequest(val steamId: Long)

@Serializable
data class PlayerResponse(
    val steamId: Long,
    val brawlhallaId: Int,
    val currentName: String
)

private fun Player.toResponse() = PlayerResponse(
    steamId = steamId,
    brawlhallaId = brawlhallaId,
    currentName = currentName
)

fun Route.playerRoutes(playerService: PlayerService, bhApiKey: String) {
    post("/player") {
        val request = call.receive<AddPlayerRequest>()
        try {
            when (val result = playerService.addPlayer(request.steamId, bhApiKey)) {
                is AddPlayerResult.Created -> call.respond(HttpStatusCode.Created, result.player.toResponse())
                is AddPlayerResult.AlreadyTracked -> call.respond(HttpStatusCode.OK, result.player.toResponse())
            }
        } catch (e: SteamIdNotLinkedException) {
            call.respondText(e.message ?: "Steam ID not linked to Brawlhalla", status = HttpStatusCode.NotFound)
        } catch (e: BrawlhallaApiUnavailableException) {
            call.respondText(e.message ?: "Brawlhalla API unavailable", status = HttpStatusCode.ServiceUnavailable)
        }
    }
}
