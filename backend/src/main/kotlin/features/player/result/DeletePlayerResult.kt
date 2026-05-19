package com.brawlpulse.api.features.player.result

import com.brawlpulse.api.features.player.Player

sealed class DeletePlayerResult {
    object Removed : DeletePlayerResult()
    data class NotFound(val steamId: Long) : DeletePlayerResult()
}