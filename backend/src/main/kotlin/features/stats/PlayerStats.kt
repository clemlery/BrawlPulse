package com.brawlpulse.api.features.stats

data class PlayerStats(
    val name: String,
    val period: String,
    val effectiveDays: Int,
    val winsDelta: Int,
    val gamesDelta: Int,
    val winrate: Double,
    val ratingDelta: Int,
    val currentRating: Int,
    val snapshotCount: Int
)
