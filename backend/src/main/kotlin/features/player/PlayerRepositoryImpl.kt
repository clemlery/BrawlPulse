package com.brawlpulse.api.features.player

import com.brawlpulse.api.plugins.dbQuery

class PlayerRepositoryImpl : PlayerRepository {

    override suspend fun addPlayer(
        newSteamId: Long,
        newBrawlhallaId: Int,
        newName: String
    ): Player = dbQuery {
        return@dbQuery daoToModel(PlayerDAO.new {
            steamId = newSteamId
            brawlhallaId = newBrawlhallaId
            currentName = newName
        })
    }

    override suspend fun deletePlayer(steamId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getPlayer(steamId: Long): Player? {
        TODO("Not yet implemented")
    }

}