package com.brawlpulse.api.infrastructure.brawlhalla

import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsGlobalResponse
import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsRankedResponse
import com.brawlpulse.api.infrastructure.brawlhalla.models.SearchPlayerResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

class BrawlhallaDao {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun searchPlayer(
        steamId : String,
        apiKey : String
    ) : SearchPlayerResponse {
        val response = client.get(Constants.SEARCH_PLAYER_URL) {
            url {
                parameters.append("steamid", steamId)
                parameters.append("api_key", apiKey)
            }
        }

        return response.body()
    }

    suspend fun getPlayerGlobalStats(
        brawlhallaId: Int,
        apiKey : String
    ) : PlayerStatsGlobalResponse {
        val response = client.get(Constants.GLOBAL_STATS_URL(brawlhallaId)) {
            url {
                parameters.append("api_key", apiKey)
            }
        }

        return response.body()
    }

    suspend fun getPlayerRankedStats(
        brawlhallaId : Int,
        apiKey : String
    ) : PlayerStatsRankedResponse{
        val response = client.get(Constants.RANKED_STATS_URL(brawlhallaId)) {
            url {
                parameters.append("api_key", apiKey)
            }
        }

        return response.body()
    }
}