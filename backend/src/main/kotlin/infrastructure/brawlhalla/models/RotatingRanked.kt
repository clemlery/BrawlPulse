package com.brawlpulse.api.infrastructure.brawlhalla.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RotatingRanked(
    @SerialName("name") val name: String,
    @SerialName("brawlhalla_id") val brawlhallaId: Long,
    @SerialName("rating") val rating: Int,
    @SerialName("peak_rating") val peakRating: Int,
    @SerialName("tier") val tier: String,
    @SerialName("wins") val wins: Int,
    @SerialName("games") val games: Int,
    @SerialName("region") val region: String
)