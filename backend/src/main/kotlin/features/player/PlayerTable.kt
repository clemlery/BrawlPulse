package com.brawlpulse.api.features.player

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone


object PlayerTable : IntIdTable("players_tracked") {
    val steamId = long("steam_id").uniqueIndex()
    val brawlhallaId = integer("brawlhalla_id").uniqueIndex()
    val currentName = text("current_name")
    val addedAt = timestampWithTimeZone("added_at")
}