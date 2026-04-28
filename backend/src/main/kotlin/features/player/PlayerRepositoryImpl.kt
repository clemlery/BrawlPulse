package com.brawlpulse.api.features.player

import com.brawlpulse.api.features.snapshot.daoToModel
import com.brawlpulse.api.plugins.dbQuery
import java.time.OffsetDateTime

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
            addedAt = OffsetDateTime.now()
        })
    }

    override suspend fun deletePlayer(steamId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getPlayer(steamId: Long): Player? = dbQuery{
        PlayerDAO
            .find { (PlayerTable.steamId eq steamId) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

}