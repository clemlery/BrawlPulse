package com.brawlpulse.api.plugins

import com.brawlpulse.api.features.player.PlayerRepositoryImpl
import com.brawlpulse.api.features.player.PlayerService
import com.brawlpulse.api.features.player.playerRoutes
import com.brawlpulse.api.features.snapshot.DailySnapshotsRepositoryImpl
import com.brawlpulse.api.features.snapshot.DailySnapshotsService
import com.brawlpulse.api.infrastructure.brawlhalla.BrawlhallaDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val bhApiKey = System.getProperty("BH_API_KEY") ?: error("BH_API_KEY not configured")
    val dailySnapshotService = DailySnapshotsService(DailySnapshotsRepositoryImpl())
    val playerService = PlayerService(BrawlhallaDao(), PlayerRepositoryImpl(), dailySnapshotService)

    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        route("/health") {
            get("/live") {
                call.respond(HttpStatusCode.OK)
            }
            get("/ready") {
                TODO()
            }
        }
        playerRoutes(playerService, bhApiKey)
    }
}