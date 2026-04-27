package com.brawlpulse.api.infrastructure.brawlhalla.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerStatsGlobal(
    @SerialName("brawlhalla_id") val brawlhallaId: Int,
    @SerialName("name") val name: String,
    @SerialName("xp") val xp: Int,
    @SerialName("level") val level: Int,
    @SerialName("xp_percentage") val xpPercentage: Double,
    @SerialName("games") val games: Int,
    @SerialName("wins") val wins: Int,
    @SerialName("damagebomb") val damageBomb: String,
    @SerialName("damagemine") val damageMine: String,
    @SerialName("damagespikeball") val damageSpikeball: String,
    @SerialName("damagesidekick") val damageSidekick: String,
    @SerialName("hitsnowball") val hitSnowball: Int,
    @SerialName("kobomb") val koBomb: Int,
    @SerialName("komine") val koMine: Int,
    @SerialName("kospikeball") val koSpikeball: Int,
    @SerialName("kosidekick") val koSidekick: Int,
    @SerialName("kosnowball") val koSnowball: Int,
    @SerialName("legends") val legends : List<LegendStats>
)