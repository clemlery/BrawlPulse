package com.brawlpulse.api.infrastructure.brawlhalla.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class TeamRank(
    @SerialName("brawlhalla_id_one") val brawlhallaIdOne : Int,
    @SerialName("brawlhalla_id_two") val brawlhallaIdTwo : Int,
    @SerialName("rating") val rating : Int,
    @SerialName("peak_rating") val peakRating : Int,
    @SerialName("tier") val tier : String,
    @SerialName("wins") val wins : Int,
    @SerialName("games") val games : Int,
    @SerialName("teamname") val teamName : String,
    @SerialName("region") val region : String,
    @SerialName("global_rank") val globalRank : String

    )
