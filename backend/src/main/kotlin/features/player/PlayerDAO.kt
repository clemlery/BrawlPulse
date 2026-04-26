package com.brawlpulse.api.features.player

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PlayerDAO(id : EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PlayerDAO>(PlayerTable)

    var steamId by PlayerTable.steamId
    var brawlhallaId by PlayerTable.brawlhallaId
    var currentName by PlayerTable.currentName
    var addedAt by PlayerTable.addedAt
}