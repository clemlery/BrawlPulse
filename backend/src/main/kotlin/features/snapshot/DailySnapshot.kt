package com.brawlpulse.api.features.snapshot

import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsGlobal
import java.time.OffsetDateTime
import java.util.Date

data class DailySnapshot(
    val id : Int,
    val playerId : Int,
    val snapshotDate : Date,
    val wins : Int,
    val games : Int,
    val rating : Int,
    val peakRating : Int,
    val legendsRaw : PlayerStatsGlobal,
    val createds : OffsetDateTime
)
