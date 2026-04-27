package com.brawlpulse.api.features.snapshot

import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsGlobal

class DailySnapshotsService(
    snapshotsRepository: DailySnapshotsRepository
) {

    suspend fun addInitialSnapshot(playerId : Int, playerStats : PlayerStatsGlobal) {

        TODO()
    }

    suspend fun addSnapshot(steamId : Long) {
        TODO()
    }

}