package com.brawlpulse.api.infrastructure.brawlhalla

import com.brawlpulse.api.common.exceptions.*
import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsGlobalResponse
import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsRankedResponse
import com.brawlpulse.api.infrastructure.brawlhalla.models.SearchPlayerResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*

class BrawlhallaDao {

    private val client = HttpClient(CIO) {
        install(HttpRequestRetry) {
            maxRetries = 3
            retryIf { request, response ->
                (response.status == HttpStatusCode.TooManyRequests)
            }
            retryOnServerErrors(maxRetries = 3)
            exponentialDelay()
        }
        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, request ->
                val clientException = exception as? ResponseException ?: return@handleResponseExceptionWithRequest
                val exceptionResponse = clientException.response
                val exceptionResponseText = exceptionResponse.bodyAsText()
                when (exceptionResponse.status) {
                    HttpStatusCode.Unauthorized -> throw IllegalStateException(exceptionResponseText)
                    HttpStatusCode.Forbidden -> throw InvalidApiKeyException(exceptionResponseText)
                    HttpStatusCode.NotFound -> {
                        if (exceptionResponseText.startsWith("Not Found")) throw NotFoundException(exceptionResponseText)
                        else throw BadRequestException(exceptionResponseText)
                    }
                    HttpStatusCode.TooManyRequests -> throw RateLimitExceededException(exceptionResponseText)
                    HttpStatusCode.ServiceUnavailable -> throw ServerErrorException(exceptionResponseText)
                    else -> throw UncoveredException(exceptionResponseText)
                }
            }
        }
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