package com.brawlpulse.api.infrastructure.brawlhalla.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SearchPlayerResponse(
    @SerialName("brawlhalla_id") val brawlhallaId : Int,
    @SerialName("name") val name : String
)
