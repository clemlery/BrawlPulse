package com.brawlpulse.api.plugins

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.configureRouting() {
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

    }
}

@Serializable
@Resource("/articles")
class Articles(val sort: String? = "new")
