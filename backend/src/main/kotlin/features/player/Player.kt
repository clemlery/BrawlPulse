package com.brawlpulse.api.features.player

import org.jetbrains.exposed.dao.id.EntityID
import java.time.OffsetDateTime

data class Player(
    var id: EntityID<Int>? = null,
    var steamId: Long,
    var brawlhallaId: Int,
    var currentName: String,
    var addedAt: OffsetDateTime? = null
)