package com.brawlpulse.api.plugins

import io.ktor.server.application.*
import io.ktor.server.config.ApplicationConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object DbRegistry {
    private var database: Database? = null
    private var dataSource: HikariDataSource? = null

    fun register(db: Database, ds: HikariDataSource) {
        database = db
        dataSource = ds
    }

    fun database(): Database =
        database ?: error("Database not configured")

    fun isConfigured(): Boolean = database != null
}

fun configureDatabases(config: ApplicationConfig) {
    if (DbRegistry.isConfigured()) {
        println("[DB] Base de données déjà configurée, skip")
        return
    }

    println("[DB] Configuration de la base de données...")
    println("[DB] Password : ${config.property("db.password").getString()}")

    val hikariConfig = HikariConfig().apply {
        jdbcUrl = config.property("db.jdbcUrl").getString()
        driverClassName = config.property("db.driver").getString()
        username = config.property("db.username").getString()
        password = config.property("db.password").getString()
        maximumPoolSize = config.propertyOrNull("db.maximumPoolSize")
            ?.getString()?.toInt() ?: 10
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }

    val dataSource = HikariDataSource(hikariConfig)
    val database = Database.connect(dataSource)

    DbRegistry.register(database, dataSource)
    println("[DB] Base de données configurée avec succès")
}

fun Application.configureDatabases() {
    configureDatabases(environment.config)
}

suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO, db = DbRegistry.database()) {
        block()
    }

suspend fun dbPing() {
    newSuspendedTransaction(Dispatchers.IO, db = DbRegistry.database()) {
        exec("SELECT 1")
    }
}
