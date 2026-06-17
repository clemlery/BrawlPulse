package com.brawlpulse.api.features.stats.result

import com.brawlpulse.api.features.stats.PlayerStats

sealed class GetPlayerStatsResult {
    data class Success(val stats: PlayerStats) : GetPlayerStatsResult()
    object PlayerNotFound : GetPlayerStatsResult()
    object NoSnapshots : GetPlayerStatsResult()
    object InvalidPeriod : GetPlayerStatsResult()
}
