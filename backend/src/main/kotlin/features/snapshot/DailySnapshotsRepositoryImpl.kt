package com.brawlpulse.api.features.snapshot

import com.brawlpulse.api.features.player.PlayerTable
import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsGlobal
import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsRanked
import com.brawlpulse.api.plugins.dbQuery
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
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

    override suspend fun deleteAllSnapshots(steamId: Long): Unit = dbQuery {
        val playerId = PlayerTable
            .select(PlayerTable.id)
            .where { PlayerTable.steamId eq steamId }
            .singleOrNull()
            ?.get(PlayerTable.id)
            ?: return@dbQuery
        DailySnapshotsTable.deleteWhere { DailySnapshotsTable.playerId eq playerId.value }
    }

    override suspend fun getAllSnapshots(steamId: Long): List<DailySnapshot> = dbQuery {
        DailySnapshotsTable
            .join(PlayerTable, JoinType.INNER, DailySnapshotsTable.playerId, PlayerTable.id)
            .selectAll()
            .where { PlayerTable.steamId eq steamId }
            .orderBy(DailySnapshotsTable.snapshotDate to SortOrder.ASC)
            .map { row ->
                DailySnapshot(
                    id = row[DailySnapshotsTable.id].value,
                    playerId = row[DailySnapshotsTable.playerId],
                    snapshotDate = row[DailySnapshotsTable.snapshotDate],
                    wins = row[DailySnapshotsTable.wins],
                    games = row[DailySnapshotsTable.games],
                    rating = row[DailySnapshotsTable.rating],
                    peakRating = row[DailySnapshotsTable.peakRating],
                    legendsRaw = row[DailySnapshotsTable.legendsRaw],
                    createdAt = row[DailySnapshotsTable.createdAt]
                )
            }
    }
}
