package com.brawlpulse.api.features.snapshot

import com.brawlpulse.api.features.player.daoToModel
import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsGlobal
import com.brawlpulse.api.plugins.dbQuery

class DailySnapshotsRepositoryImpl : DailySnapshotsRepository {

    override suspend fun addDailySnapshot(
        playerStatsGlobal: PlayerStatsGlobal
    ): DailySnapshot = dbQuery {
        return@dbQuery daoToModel(DailySnapshotsDAO.new {
            
        })
    }

    override suspend fun deleteAllSnapshots(steamId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllSnapshots(steamId: Long): List<DailySnapshot> {
        TODO("Not yet implemented")
    }
}