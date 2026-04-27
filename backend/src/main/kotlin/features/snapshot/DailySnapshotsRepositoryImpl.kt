package com.brawlpulse.api.features.snapshot

import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsGlobal
import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsRanked
import com.brawlpulse.api.plugins.dbQuery
import java.time.LocalDate

class DailySnapshotsRepositoryImpl : DailySnapshotsRepository {

    override suspend fun addFirstSnapshot(
        id: Int,
        playerStatsGlobal: PlayerStatsGlobal,
        playerStatsRanked: PlayerStatsRanked
    ): DailySnapshot = dbQuery {
        return@dbQuery daoToModel(DailySnapshotsDAO.new {
            playerId = id
            snapshotDate = LocalDate.now()
            wins = playerStatsGlobal.wins
            games = playerStatsGlobal.games
            rating = playerStatsRanked.rating
            peakRating = playerStatsRanked.peakRating
            legendsRaw = playerStatsGlobal.legends
        })
    }

    override suspend fun addDailySnapshot(
        id: Int,
        playerStatsGlobal: PlayerStatsGlobal,
        playerStatsRanked: PlayerStatsRanked
    ): DailySnapshot = dbQuery {
        return@dbQuery daoToModel(DailySnapshotsDAO.new {
            playerId = id
            snapshotDate = LocalDate.now()
            wins = playerStatsGlobal.wins
            games = playerStatsGlobal.games
            rating = playerStatsRanked.rating
            peakRating = playerStatsRanked.peakRating
            legendsRaw = playerStatsGlobal.legends
        })
    }

    override suspend fun deleteAllSnapshots(steamId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllSnapshots(steamId: Long): List<DailySnapshot> {
        TODO("Not yet implemented")
    }
}