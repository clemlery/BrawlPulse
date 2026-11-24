package com.brawlpulse.api.infrastructure.brawlhalla

import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsGlobal
import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsRanked
import com.brawlpulse.api.infrastructure.brawlhalla.models.SearchPlayerResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.HttpRequest
import io.ktor.client.request.get
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*

class BrawlhallaDao(engine: HttpClientEngine = CIO.create()) {

    private val client = HttpClient(engine) {
        // Without this, non-2xx responses skip HttpResponseValidator and fail later in body().
        expectSuccess = true
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
                // Ktor's ResponseException embeds the full request URL, api_key query
                // parameter included, in its message. It is deliberately NOT chained as
                // the cause so the key never ends up in logs or stack traces.
                val message = describeFailure(request, exceptionResponse.status, exceptionResponseText)
                when (exceptionResponse.status) {
                    HttpStatusCode.Unauthorized -> throw IllegalStateException(message)
                    HttpStatusCode.Forbidden -> throw InvalidApiKeyException(message)
                    HttpStatusCode.NotFound -> {
                        if (exceptionResponseText.startsWith("Not Found")) throw NotFoundException(message)
                        else throw BadRequestException(message)
                    }
                    HttpStatusCode.TooManyRequests -> throw RateLimitExceededException(message)
                    HttpStatusCode.ServiceUnavailable -> throw ServerErrorException(message)
                    else -> throw UncoveredException(message)
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

    /** Describes a failed call without its query string, which carries the API key. */
    private fun describeFailure(request: HttpRequest, status: HttpStatusCode, body: String): String =
        "Brawlhalla API ${request.method.value} ${request.url.host}${request.url.encodedPath} failed with $status: $body"
}
