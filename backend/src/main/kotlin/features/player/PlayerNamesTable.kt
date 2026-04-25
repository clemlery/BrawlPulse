package com.brawlpulse.api.features.player

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone
import java.time.OffsetDateTime
import java.time.ZoneOffset

object PlayerNamesTable : Table("player_names") {
    val playerId = integer("player_id")
        .references(PlayerTable.id, onDelete = ReferenceOption.CASCADE)
    val name = text("name")
        .index()
    val lastSeenAt = timestampWithTimeZone("last_seen_at")

    override val primaryKey = PrimaryKey(playerId, name)
}