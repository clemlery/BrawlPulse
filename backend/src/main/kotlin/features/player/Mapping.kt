package com.brawlpulse.api.features.player

fun daoToModel(dao: PlayerDAO) = Player(
    dao.id.value,
    dao.steamId,
    dao.brawlhallaId,
    dao.currentName,
    dao.addedAt
)