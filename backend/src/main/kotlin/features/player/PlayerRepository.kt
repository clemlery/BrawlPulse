package com.brawlpulse.api.features.player

interface PlayerRepository {

    suspend fun addPlayer(steamId : String)

    suspend fun deletePlayer(steamId : String)

    suspend fun getPlayer(steamId : String) : Player?

}