package com.brawlpulse.api.infrastructure.brawlhalla

import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsGlobal
import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsRanked
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
                    HttpStatusCode.Unauthorized -> throw IllegalStateException(exceptionResponseText, clientException)
                    HttpStatusCode.Forbidden -> throw InvalidApiKeyException(exceptionResponseText, clientException)
                    HttpStatusCode.NotFound -> {
                        if (exceptionResponseText.startsWith("Not Found")) throw NotFoundException(exceptionResponseText, clientException)
                        else throw BadRequestException(exceptionResponseText, clientException)
                    }
                    HttpStatusCode.TooManyRequests -> throw RateLimitExceededException(exceptionResponseText, clientException)
                    HttpStatusCode.ServiceUnavailable -> throw ServerErrorException(exceptionResponseText, clientException)
                    else -> throw UncoveredException(exceptionResponseText, clientException)
                }
            }
        }
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun searchPlayer(
        steamId : Long,
        apiKey : String
    ) : SearchPlayerResponse {
        val response = client.get(Constants.SEARCH_PLAYER_URL) {
            url {
                parameters.append("steamid", steamId.toString())
                parameters.append("api_key", apiKey)
            }
        }

        return response.body()
    }

    suspend fun getPlayerGlobalStats(
        brawlhallaId: Int,
        apiKey : String
    ) : PlayerStatsGlobal {
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
    ) : PlayerStatsRanked{
        val response = client.get(Constants.RANKED_STATS_URL(brawlhallaId)) {
            url {
                parameters.append("api_key", apiKey)
            }
        }

        return response.body()
    }
}