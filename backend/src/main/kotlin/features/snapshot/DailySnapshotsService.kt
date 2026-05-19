package com.brawlpulse.api.features.snapshot

import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsGlobal
import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsRanked

open class DailySnapshotsService(
    private val dailySnapshotsRepository: DailySnapshotsRepository
) {

    open suspend fun addDailySnapshot(
        playerId: Int,
        playerStatsGlobal: PlayerStatsGlobal,
        playerStatsRanked: PlayerStatsRanked
    ) {
        dailySnapshotsRepository.addDailySnapshot(playerId, playerStatsGlobal, playerStatsRanked)
    }
}
