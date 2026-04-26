package com.brawlpulse.api.features.player

import java.time.OffsetDateTime

data class Player(
    val id: Int,
    val steamId: Long,
    val brawlhallaId: Int,
    val currentName: String,
    val addedAt: OffsetDateTime
)