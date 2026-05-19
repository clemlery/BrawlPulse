package com.brawlpulse.api.plugins

import com.brawlpulse.api.features.player.PlayerRepositoryImpl
import com.brawlpulse.api.features.player.PlayerService
import com.brawlpulse.api.features.player.playerRoutes
import com.brawlpulse.api.features.snapshot.DailySnapshotJob
import com.brawlpulse.api.features.snapshot.DailySnapshotsRepositoryImpl
import com.brawlpulse.api.features.snapshot.DailySnapshotsService
import com.brawlpulse.api.infrastructure.brawlhalla.BrawlhallaDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

fun Application.configureRouting() {
    val bhApiKey = System.getProperty("BH_API_KEY") ?: error("BH_API_KEY not configured")
    val adminToken = System.getProperty("ADMIN_TOKEN") ?: error("ADMIN_TOKEN not configured")

    val bhClient = BrawlhallaDao()
    val playerRepository = PlayerRepositoryImpl()
    val snapshotRepository = DailySnapshotsRepositoryImpl()
    val dailySnapshotService = DailySnapshotsService(snapshotRepository)
    val playerService = PlayerService(bhClient, playerRepository, dailySnapshotService)

    val snapshotJob = DailySnapshotJob(playerRepository, snapshotRepository, bhClient, bhApiKey)

    // Isolated scope: a crash in the job does not propagate to the Ktor server
    val schedulerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    schedulerScope.launch { snapshotJob.scheduleDaily() }

    environment.monitor.subscribe(ApplicationStopped) { schedulerScope.cancel() }

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

        route("/admin") {
            post("/snapshot/run") {
                val bearer = call.request.header(HttpHeaders.Authorization)
                    ?.removePrefix("Bearer ")
                    ?.trim()
                if (bearer != adminToken) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@post
                }
                schedulerScope.launch { snapshotJob.runDailySnapshot() }
                call.respond(HttpStatusCode.Accepted, "Snapshot job triggered")
            }
        }

        playerRoutes(playerService, bhApiKey)
    }
}
