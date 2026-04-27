package com.brawlpulse.api.features.snapshot

import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsGlobal

interface DailySnapshotsRepository {

    suspend fun addDailySnapshot(playerStatsGlobal: PlayerStatsGlobal) : DailySnapshot

    suspend fun deleteAllSnapshots(steamId : Long)

    suspend fun getAllSnapshots(steamId: Long) : List<DailySnapshot>
}