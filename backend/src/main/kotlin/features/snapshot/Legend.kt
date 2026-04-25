package com.brawlpulse.api.features.snapshot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Legend(
    @SerialName("legend_id") val legendId: Int,
    @SerialName("legend_name_key") val legendNameKey: String,
    @SerialName("damagedealt") val damageDealt: String,
    @SerialName("damagetaken") val damageTaken: String,
    @SerialName("kos") val kos: Int,
    @SerialName("falls") val falls: Int,
    @SerialName("suicides") val suicides: Int,
    @SerialName("teamkos") val teamkos: Int,
    @SerialName("matchtime") val matchtime: Int,
    @SerialName("games") val games: Int,
    @SerialName("wins") val wins: Int,
    @SerialName("damageunarmed") val damageUnarmed: String,
    @SerialName("damagethrownitem") val damageThrownItem: String,
    @SerialName("damageweaponone") val damageWeaponOne: String,
    @SerialName("damageweapontwo") val damageWeaponTwo: String,
    @SerialName("damagegadgets") val damageGadgets: String,
    @SerialName("kounarmed") val koUnarmed: Int,
    @SerialName("kothrownitem") val koThrownItem: Int,
    @SerialName("koweaponone") val koWeaponOne: Int,
    @SerialName("koweapontwo") val koWeaponTwo: Int,
    @SerialName("kogadgets") val koGadgets: Int,
    @SerialName("timeheldweaponone") val timeHeldWeaponOne: Int,
    @SerialName("timeheldweapontwo") val timeHeldWeaponTwo: Int,
    @SerialName("xp") val xp: Int,
    @SerialName("level") val level: Int,
    @SerialName("xp_percentage") val xpPercentage: Double
)