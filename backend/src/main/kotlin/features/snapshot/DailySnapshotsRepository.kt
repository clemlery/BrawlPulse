package com.brawlpulse.api.features.snapshot

import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsGlobal
import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsRanked

interface DailySnapshotsRepository {

    suspend fun addDailySnapshot(id: Int, playerStatsGlobal: PlayerStatsGlobal, playerStatsRanked: PlayerStatsRanked)

    suspend fun deleteAllSnapshots(steamId: Long)

    suspend fun getAllSnapshots(steamId: Long): List<DailySnapshot>
}
