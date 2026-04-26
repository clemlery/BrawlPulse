package com.brawlpulse.api.features.player.result

import com.brawlpulse.api.features.player.Player

sealed class AddPlayerResult {
    data class Created(val player: Player) : AddPlayerResult()
    data class AlreadyTracked(val player: Player) : AddPlayerResult()
}