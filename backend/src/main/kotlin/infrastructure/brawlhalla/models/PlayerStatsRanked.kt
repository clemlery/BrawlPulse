package com.brawlpulse.api.infrastructure.brawlhalla.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerStatsRanked(
    @SerialName("name") val name : String,
    @SerialName("brawlhalla_id") val brawlhallaId : Int,
    @SerialName("rating") val rating : Int,
    @SerialName("peak_rating") val peakRating : Int,
    @SerialName("tier") val tier : String,
    @SerialName("wins") val wins : Int,
    @SerialName("games") val games : Int,
    @SerialName("region") val region : String,
    @SerialName("global_rank") val globalRank : Int, // 0 if less than top 1000
    @SerialName("region_rank") val regionRank : Int, // 0 if less than top 1000
    @SerialName("legends") val legends : List<LegendRank>,
    @SerialName("2v2") val twosStats : List<TeamRank>,
    @SerialName("rotating_ranked") val rotatingRanked : RotatingRanked
)
