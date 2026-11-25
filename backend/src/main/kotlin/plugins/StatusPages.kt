package com.brawlpulse.api.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.response.respond
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Maps exceptions escaping the routes to HTTP responses. Clients only ever get
 * a generic message: exception details stay in the server logs.
 */
fun Application.configureStatusPages() {
    install(StatusPages) {
        // Malformed or unconvertible input: unparseable JSON body, wrong field
        // types, missing or invalid parameters (Ktor wraps these in BadRequestException).
        exception<BadRequestException> { call, cause ->
            call.respondBadRequest(cause.cause?.message ?: cause.message)
        }
        // Body that cannot be converted at all (empty body, non-JSON Content-Type).
        // Ktor answers 415 itself, before StatusPages sees any exception, and its
        // default body echoes the target class name: replace that body.
        status(HttpStatusCode.UnsupportedMediaType) { call, status ->
            call.respond(status, buildJsonObject {
                put("error", "Unsupported or empty request body")
            })
        }

        exception<Throwable> { call, cause ->
            call.application.log.error(
                "Unhandled exception on {} {}",
                call.request.httpMethod.value,
                call.request.path(),
                cause
            )
            call.respond(HttpStatusCode.InternalServerError, buildJsonObject {
                put("error", "Internal server error")
            })
        }
    }
}

private suspend fun ApplicationCall.respondBadRequest(reason: String?) {
    application.log.info("Bad request on {} {}: {}", request.httpMethod.value, request.path(), reason)
    respond(HttpStatusCode.BadRequest, buildJsonObject {
        put("error", "Bad request")
    })
}
