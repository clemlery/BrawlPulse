package com.brawlpulse.api.infrastructure.brawlhalla

class Constants {
    companion object {
        const val BASE_URL : String = "https://api.brawlhalla.com/"
        const val SEARCH_PLAYER_URL = "https://api.brawlhalla.com/search"
        val GLOBAL_STATS_URL : (Int) -> String = { brawlhallaId -> "https://api.brawlhalla.com/player/${brawlhallaId}/stats" }
        val RANKED_STATS_URL : (Int) -> String = { brawlhallaId -> "https://api.brawlhalla.com/player/${brawlhallaId}/ranked" }
    }
}