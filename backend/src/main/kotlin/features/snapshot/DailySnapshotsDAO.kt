package com.brawlpulse.api.features.snapshot

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DailySnapshotsDAO(id : EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DailySnapshotsDAO>(DailySnapshotsTable)

    var playerId by DailySnapshotsTable.playerId
    var snapshotDate by DailySnapshotsTable.snapshotDate
    var wins by DailySnapshotsTable.wins
    var games by DailySnapshotsTable.games
    var rating by DailySnapshotsTable.rating
    var peakRating by DailySnapshotsTable.peakRating
    var legendsRaw by DailySnapshotsTable.legendsRaw
    var createdAt by DailySnapshotsTable.createdAt
}