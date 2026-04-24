package com.brawlpulse.api.features.player

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table.Dual.index
import org.jetbrains.exposed.sql.Table.Dual.integer
import org.jetbrains.exposed.sql.Table.Dual.long
import org.jetbrains.exposed.sql.Table.Dual.text

object PlayerTable : IntIdTable("players_tracked")
    val steam_id = long("steam_id").index(isUnique = true)
    val brawlhalla_id = integer("brawlhalla_id")
    val current_name = text("current_name")
    val all_names = 
}