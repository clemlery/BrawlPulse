package com.brawlpulse.api.features.snapshot

import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsGlobal
import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsRanked
import com.brawlpulse.api.plugins.dbQuery
import org.jetbrains.exposed.sql.insertIgnore
import java.time.LocalDate
import java.time.OffsetDateTime

class DailySnapshotsRepositoryImpl : DailySnapshotsRepository {

    // insertIgnore generates INSERT … ON CONFLICT DO NOTHING for PostgreSQL
    override suspend fun addDailySnapshot(
        id: Int,
        playerStatsGlobal: PlayerStatsGlobal,
        playerStatsRanked: PlayerStatsRanked
    ): Unit = dbQuery {
        DailySnapshotsTable.insertIgnore {
            it[DailySnapshotsTable.playerId] = id
            it[DailySnapshotsTable.snapshotDate] = LocalDate.now()
            it[DailySnapshotsTable.wins] = playerStatsGlobal.wins
            it[DailySnapshotsTable.games] = playerStatsGlobal.games
            it[DailySnapshotsTable.rating] = playerStatsRanked.rating
            it[DailySnapshotsTable.peakRating] = playerStatsRanked.peakRating
            it[DailySnapshotsTable.legendsRaw] = playerStatsGlobal.legends
            it[DailySnapshotsTable.createdAt] = OffsetDateTime.now()
        }
    }

    override suspend fun deleteAllSnapshots(steamId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllSnapshots(steamId: Long): List<DailySnapshot> {
        TODO("Not yet implemented")
    }
}
