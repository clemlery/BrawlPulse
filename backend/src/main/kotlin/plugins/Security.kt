package com.brawlpulse.api.plugins


import io.ktor.server.application.*
import io.ktor.server.auth.*
import java.security.MessageDigest

fun Application.configureSecurity() {
    val adminToken = System.getProperty("ADMIN_TOKEN")
        ?: error("ADMIN_TOKEN not configured")

    authentication {
        bearer("admin-auth") {
            realm = "Admin API"
            authenticate { tokenCredential ->
                if (MessageDigest.isEqual(
                        tokenCredential.token.toByteArray(),
                        adminToken.toByteArray()
                    )
                ) {
                    UserIdPrincipal("admin")
                } else {
                    null
                }
            }
        }
    }
}