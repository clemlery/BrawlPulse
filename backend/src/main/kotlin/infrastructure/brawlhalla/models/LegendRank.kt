package com.brawlpulse.api.infrastructure.brawlhalla.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LegendRank(
    @SerialName("legend_id") val legendId : Int,
    @SerialName("legend_name_key") val legendNameKey : String,
    @SerialName("rating") val rating : Int,
    @SerialName("peak_rating") val peakRating : Int,
    @SerialName("tier") val tier : String,
    @SerialName("wins") val wins : Int,
    @SerialName("games") val games : Int

)
