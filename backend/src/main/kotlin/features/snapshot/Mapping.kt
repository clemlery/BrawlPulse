package com.brawlpulse.api.features.snapshot

import com.brawlpulse.api.features.player.Player
import com.brawlpulse.api.features.player.PlayerDAO

fun daoToModel(dao: DailySnapshotsDAO) = DailySnapshot(
    dao.id.value,
    dao.playerId,
    dao.snapshotDate,
    dao.wins,
    dao.games,
    dao.rating,
    dao.peakRating,
    dao.legendsRaw,
    dao.createdAt
)