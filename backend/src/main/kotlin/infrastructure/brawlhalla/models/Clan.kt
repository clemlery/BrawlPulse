package com.brawlpulse.api.infrastructure.brawlhalla.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Clan(
    @SerialName("clan_name") val clanName : String,
    @SerialName("clan_id") val clanId : Int,
    @SerialName("clan_xp") val clanXp : String,
    @SerialName("clan_lifetime_xp") val clanLifetimeXp : Int,
    @SerialName("personal_xp") val personalXp : Int
) {
}