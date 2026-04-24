package com.brawlpulse.api.features.player

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone
import java.time.OffsetDateTime
import java.time.ZoneOffset

object PlayerTable : Table("players_tracked") {
    val id = integer("id").autoIncrement()
    val steamId = long("steam_id").uniqueIndex()
    val brawlhallaId = integer("brawlhalla_id").uniqueIndex()
    val currentName = text("current_name")
    val addedAt = timestampWithTimeZone("added_at")
        .clientDefault { OffsetDateTime.now(ZoneOffset.UTC) }

    override val primaryKey = PrimaryKey(id)
}