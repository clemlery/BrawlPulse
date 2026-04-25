package com.brawlpulse.api.infrastructure.brawlhalla.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerStatsRankedResponse(
    @SerialName("name") val name : String,
    @SerialName("brawlhalla_id") val brawlhallaId : Int,
    @SerialName("rating") val rating : Int,
    @SerialName("peak_rating") val peakRating : Int,
    @SerialName("tier") val tier : Int,
    @SerialName("wins") val wins : Int,
    @SerialName("games") val games : Int,
    @SerialName("region") val region : String,
    @SerialName("global_rank") val globalRank : Int,
    @SerialName("region_rank") val regionRank : Int,
    @SerialName("legends") val legends : List<LegendStats>,
    @SerialName("2v2") val twosStats : List<TeamRank>
)
