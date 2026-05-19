package com.brawlpulse.api.features.snapshot

import com.brawlpulse.api.features.player.PlayerTable
import com.brawlpulse.api.infrastructure.brawlhalla.models.LegendStats
import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsGlobal
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone
import org.jetbrains.exposed.sql.json.jsonb


object DailySnapshotsTable : IntIdTable("daily_snapshots") {
    private val json = Json { ignoreUnknownKeys = true }

    val playerId = integer("player_id")
        .references(PlayerTable.id, onDelete = ReferenceOption.CASCADE)
    val snapshotDate = date("snapshot_date")
    val wins = integer("wins")
    val games = integer("games")
    val rating = integer("rating")
    val peakRating = integer("peak_rating")
    val legendsRaw = jsonb<List<LegendStats>>("legends_raw", json)
    val createdAt = timestampWithTimeZone("created_at")
        .defaultExpression(CurrentTimestampWithTimeZone)
}