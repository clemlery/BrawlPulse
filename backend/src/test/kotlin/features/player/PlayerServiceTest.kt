package com.brawlpulse.api.features.player

import com.brawlpulse.api.features.player.exceptions.BrawlhallaApiUnavailableException
import com.brawlpulse.api.features.player.exceptions.SteamIdNotLinkedException
import com.brawlpulse.api.features.player.result.AddPlayerResult
import com.brawlpulse.api.features.snapshot.DailySnapshotsService
import com.brawlpulse.api.infrastructure.brawlhalla.BrawlhallaDao
import com.brawlpulse.api.infrastructure.brawlhalla.NotFoundException
import com.brawlpulse.api.infrastructure.brawlhalla.models.Clan
import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsGlobal
import com.brawlpulse.api.infrastructure.brawlhalla.models.PlayerStatsRanked
import com.brawlpulse.api.infrastructure.brawlhalla.models.RotatingRanked
import com.brawlpulse.api.infrastructure.brawlhalla.models.SearchPlayerResponse
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import java.time.OffsetDateTime
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class PlayerServiceTest {

    private val mockBhClient = mockk<BrawlhallaDao>()
    private val mockPlayerRepository = mockk<PlayerRepository>()
    private val mockDailySnapshotsService = mockk<DailySnapshotsService>()

    private val playerService = PlayerService(mockBhClient, mockPlayerRepository, mockDailySnapshotsService)

    private val testApiKey = "test-api-key"
    private val testSteamId = 76561198123456789L
    private val testBrawlhallaId = 123456

    private val testPlayer = Player(
        id = 1,
        steamId = testSteamId,
        brawlhallaId = testBrawlhallaId,
        currentName = "TestPlayer",
        addedAt = OffsetDateTime.now()
    )

    private val testSearchResponse = SearchPlayerResponse(
        brawlhallaId = testBrawlhallaId,
        name = "TestPlayer"
    )

    private val testGlobalStats = PlayerStatsGlobal(
        brawlhallaId = testBrawlhallaId,
        name = "TestPlayer",
        xp = 10000,
        level = 50,
        xpPercentage = 0.5,
        games = 500,
        wins = 300,
        damageBomb = "1000",
        damageMine = "500",
        damageSpikeball = "200",
        damageSidekick = "100",
        hitSnowball = 10,
        koBomb = 50,
        koMine = 20,
        koSpikeball = 5,
        koSidekick = 3,
        koSnowball = 1,
        legends = emptyList(),
        clan = Clan(clanName = "", clanId = 0, clanXp = "0", clanLifetimeXp = 0, personalXp = 0)
    )

    private val testRankedStats = PlayerStatsRanked(
        name = "TestPlayer",
        brawlhallaId = testBrawlhallaId,
        rating = 1500,
        peakRating = 1600,
        tier = "Gold",
        wins = 100,
        games = 200,
        region = "EU",
        globalRank = 0,
        regionRank = 0,
        legends = emptyList(),
        twosStats = emptyList(),
        rotatingRanked = RotatingRanked(
            name = "TestPlayer",
            brawlhallaId = testBrawlhallaId.toLong(),
            rating = 1400,
            peakRating = 1500,
            tier = "Silver",
            wins = 50,
            games = 100,
            region = "EU"
        )
    )

    @Before
    fun setUp() {
        clearMocks(mockBhClient, mockPlayerRepository, mockDailySnapshotsService)
    }

    @Test
    fun `addPlayer creates initial snapshot when player is new`() = runTest {
        coEvery { mockPlayerRepository.getPlayer(testSteamId) } returns null
        coEvery { mockBhClient.searchPlayer(testSteamId, testApiKey) } returns testSearchResponse
        coEvery { mockBhClient.getPlayerGlobalStats(testBrawlhallaId, testApiKey) } returns testGlobalStats
        coEvery { mockBhClient.getPlayerRankedStats(testBrawlhallaId, testApiKey) } returns testRankedStats
        coEvery { mockPlayerRepository.addPlayer(testSteamId, testBrawlhallaId, "TestPlayer") } returns testPlayer
        coJustRun { mockDailySnapshotsService.addDailySnapshot(testPlayer.id, testGlobalStats, testRankedStats) }

        val result = playerService.addPlayer(testSteamId, testApiKey)

        assertIs<AddPlayerResult.Created>(result)
        coVerify(exactly = 1) { mockDailySnapshotsService.addDailySnapshot(testPlayer.id, testGlobalStats, testRankedStats) }
    }

    @Test
    fun `addPlayer does not create snapshot when player already tracked`() = runTest {
        coEvery { mockPlayerRepository.getPlayer(testSteamId) } returns testPlayer

        val result = playerService.addPlayer(testSteamId, testApiKey)

        assertIs<AddPlayerResult.AlreadyTracked>(result)
        coVerify(exactly = 0) { mockDailySnapshotsService.addDailySnapshot(any(), any(), any()) }
    }

    @Test
    fun `addPlayer throws SteamIdNotLinkedException when player not found in Brawlhalla`() = runTest {
        coEvery { mockPlayerRepository.getPlayer(testSteamId) } returns null
        coEvery { mockBhClient.searchPlayer(testSteamId, testApiKey) } throws NotFoundException("Not found")

        assertFailsWith<SteamIdNotLinkedException> {
            playerService.addPlayer(testSteamId, testApiKey)
        }
        coVerify(exactly = 0) { mockDailySnapshotsService.addDailySnapshot(any(), any(), any()) }
    }

    @Test
    fun `addPlayer throws BrawlhallaApiUnavailableException when API is unavailable`() = runTest {
        coEvery { mockPlayerRepository.getPlayer(testSteamId) } returns null
        coEvery { mockBhClient.searchPlayer(testSteamId, testApiKey) } throws
            com.brawlpulse.api.infrastructure.brawlhalla.RateLimitExceededException("Rate limit")

        assertFailsWith<BrawlhallaApiUnavailableException> {
            playerService.addPlayer(testSteamId, testApiKey)
        }
    }
}
