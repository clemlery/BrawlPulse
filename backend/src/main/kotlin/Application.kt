package com.brawlpulse.api

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*

fun main(args: Array<String>) {
    dotenv {
        ignoreIfMissing = true
    }.entries().forEach { entry ->
        System.setProperty(entry.key, entry.value)
    }
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureSecurity()
    configureRouting()
}
