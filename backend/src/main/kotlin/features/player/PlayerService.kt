package com.brawlpulse.api.features.player

import com.brawlpulse.api.features.player.exceptions.BrawlhallaApiUnavailableException
import com.brawlpulse.api.features.player.exceptions.SteamIdNotLinkedException
import com.brawlpulse.api.features.player.result.AddPlayerResult
import com.brawlpulse.api.features.snapshot.DailySnapshotsRepository
import com.brawlpulse.api.features.snapshot.DailySnapshotsService
import com.brawlpulse.api.infrastructure.brawlhalla.BrawlhallaDao
import com.brawlpulse.api.infrastructure.brawlhalla.InvalidApiKeyException
import com.brawlpulse.api.infrastructure.brawlhalla.NotFoundException
import com.brawlpulse.api.infrastructure.brawlhalla.RateLimitExceededException
import com.brawlpulse.api.infrastructure.brawlhalla.ServerErrorException
import com.brawlpulse.api.infrastructure.brawlhalla.UncoveredException
import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsGlobal
import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsRanked
import com.brawlpulse.api.infrastructure.brawlhalla.models.SearchPlayerResponse
import io.ktor.client.plugins.HttpRequestTimeoutException

class PlayerService(
    private val bhClient : BrawlhallaDao,
    private val playerRepository: PlayerRepository,
    private val dailySnapshotService: DailySnapshotsService
) {

    suspend fun addPlayer(steamId : Long, apiKey: String) : AddPlayerResult {
        var player : Player? = playerRepository.getPlayer(steamId)
        var addPlayerResult : AddPlayerResult
        if (player == null) {
            var searchPlayerResponse : SearchPlayerResponse
            var playerStatsGlobal : PlayerStatsGlobal
            var playerStatsRanked : PlayerStatsRanked
            try {
                searchPlayerResponse = bhClient.searchPlayer(steamId, apiKey)
                playerStatsGlobal = bhClient.getPlayerGlobalStats(searchPlayerResponse.brawlhallaId, apiKey)
                playerStatsRanked = bhClient.getPlayerRankedStats(searchPlayerResponse.brawlhallaId, apiKey)
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
            player = playerRepository.addPlayer(
                steamId,
                searchPlayerResponse.brawlhallaId,
                playerStatsGlobal.name
            )
            dailySnapshotService.addDailySnapshot(player.id, playerStatsGlobal, playerStatsRanked)
            addPlayerResult = AddPlayerResult.Created(player)
        } else {
            addPlayerResult = AddPlayerResult.AlreadyTracked(player)
        }   
        return addPlayerResult
    }
}