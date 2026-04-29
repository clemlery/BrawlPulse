package com.brawlpulse.api.features.player

interface PlayerRepository {

    suspend fun addPlayer(
        newSteamId: Long,
        newBrawlhallaId: Int,
        newName: String
    ) : Player

    suspend fun deletePlayer(steamId : Long) : Boolean

    suspend fun getPlayer(steamId : Long) : Player?

}