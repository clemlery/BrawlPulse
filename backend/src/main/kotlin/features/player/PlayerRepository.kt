package com.brawlpulse.api.features.player

interface PlayerRepository {

    suspend fun addPlayer(steamId : Long)

    suspend fun deletePlayer(steamId : Long)

    suspend fun getPlayer(steamId : Long) : Player?

}