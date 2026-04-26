package com.brawlpulse.api.features.player

import com.brawlpulse.api.features.player.exceptions.BrawlhallaApiUnavailableException
import com.brawlpulse.api.features.player.exceptions.SteamIdNotLinkedException
import com.brawlpulse.api.features.player.result.AddPlayerResult
import com.brawlpulse.api.infrastructure.brawlhalla.BrawlhallaDao
import com.brawlpulse.api.infrastructure.brawlhalla.InvalidApiKeyException
import com.brawlpulse.api.infrastructure.brawlhalla.NotFoundException
import com.brawlpulse.api.infrastructure.brawlhalla.RateLimitExceededException
import com.brawlpulse.api.infrastructure.brawlhalla.ServerErrorException
import com.brawlpulse.api.infrastructure.brawlhalla.UncoveredException
import io.ktor.client.plugins.HttpRequestTimeoutException

class PlayerService(
    private val bhClient : BrawlhallaDao,
    private val playerRepository: PlayerRepository
) {

    suspend fun addPlayer(steamId : Long, apiKey: String) : AddPlayerResult {
        val searchPlayerResponse = try {
            bhClient.searchPlayer(steamId, apiKey)
        } catch (e: NotFoundException) {
            throw SteamIdNotLinkedException("SteamId : $steamId is not in brawlhalla's databases", e)
        } catch (e: RateLimitExceededException) {
            throw BrawlhallaApiUnavailableException("Rate limit exceeded", e)
        } catch (e: ServerErrorException) {
            throw BrawlhallaApiUnavailableException("Brawlhalla API server error", e)
        } catch (e: InvalidApiKeyException) {
            throw BrawlhallaApiUnavailableException("Invalid API key configuration", e)
        } catch (e: HttpRequestTimeoutException) {
            throw BrawlhallaApiUnavailableException("Brawlhalla API timeout", e)
        } catch (e: UncoveredException) {
            throw BrawlhallaApiUnavailableException("Unexpected Brawlhalla API error", e)
        }
        var player : Player? = playerRepository.getPlayer(steamId)
        return if (player == null) {
            player = playerRepository.addPlayer(
                steamId,
                searchPlayerResponse.brawlhallaId,
                searchPlayerResponse.name
            )
            AddPlayerResult.Created(player)
        } else {
            AddPlayerResult.AlreadyTracked(player)
        }
    }
}