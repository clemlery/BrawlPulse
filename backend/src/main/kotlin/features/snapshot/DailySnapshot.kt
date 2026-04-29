package com.brawlpulse.api.features.snapshot

import com.brawlpulse.api.infrastructure.brawlhalla.models.LegendStats
import java.time.LocalDate
import java.time.OffsetDateTime

data class DailySnapshot(
    val id : Int,
    val playerId : Int,
    val snapshotDate : LocalDate,
    val wins : Int,
    val games : Int,
    val rating : Int,
    val peakRating : Int,
    val legendsRaw : List<LegendStats>,
    val createdAt : OffsetDateTime
)
