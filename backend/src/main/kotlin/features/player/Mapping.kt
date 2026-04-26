package com.brawlpulse.api.features.player

fun daoToModel(dao: PlayerDAO) = Player(
    dao.id,
    dao.steamId,
    dao.brawlhallaId,
    dao.currentName,
    dao.addedAt
)