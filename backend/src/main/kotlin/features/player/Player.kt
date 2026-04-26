package com.brawlpulse.api.features.player

import org.jetbrains.exposed.dao.id.EntityID
import java.time.OffsetDateTime

data class Player(
    val id: EntityID<Int>,
    val steamId: Long,
    val brawlhallaId: Int,
    val currentName: String,
    val addedAt: OffsetDateTime
)