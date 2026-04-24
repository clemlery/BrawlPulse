package com.brawlpulse.api.features.snapshot

import com.brawlpulse.api.features.player.PlayerTable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone
import org.jetbrains.exposed.sql.json.jsonb
import java.time.OffsetDateTime
import java.time.ZoneOffset

val format = Json { prettyPrint = true }

object DailySnapshotsTable : Table("daily_snapshots") {
    val id = integer("id").autoIncrement()
    val playerId = integer("player_id").references(PlayerTable.id)
    val snapshotDate = date("snapshot_date")
    val wins = integer("wins")
    val games = integer("games")
    val rating = integer("rating")
    val peakRating = integer("peak_rating")
    val legendsRaw = jsonb<Legend>("legends_raw", format)
    val createdAt = timestampWithTimeZone("created_at")
        .clientDefault { OffsetDateTime.now(ZoneOffset.UTC) }

    init {
        uniqueIndex(
            customIndexName = "idx_snapshots_player_date",
            columns = arrayOf(playerId, snapshotDate),
            functions = null
        )
    }
}