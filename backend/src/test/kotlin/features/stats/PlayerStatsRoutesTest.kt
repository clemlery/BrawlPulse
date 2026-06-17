package com.brawlpulse.api.features.stats

import com.brawlpulse.api.features.stats.result.GetPlayerStatsResult
import com.brawlpulse.api.plugins.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.serialization.json.*
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

class PlayerStatsRoutesTest {

    private val mockPlayerStatsService = mockk<PlayerStatsService>()

    private val testStats = PlayerStats(
        name = "Zatox",
        period = "7d",
        effectiveDays = 7,
        winsDelta = 12,
        gamesDelta = 18,
        winrate = 66.7,
        ratingDelta = 45,
        currentRating = 1842,
        snapshotCount = 7
    )

    @Before
    fun setUp() {
        clearMocks(mockPlayerStatsService)
    }

    private fun ApplicationTestBuilder.setupRouting() {
        application {
            configureSerialization()
            routing {
                statsRoutes(mockPlayerStatsService)
            }
        }
    }

    @Test
    fun `GET players stats returns 200 with correct body`() = testApplication {
        setupRouting()
        coEvery { mockPlayerStatsService.getPlayerStats(12345L, "7d") } returns GetPlayerStatsResult.Success(testStats)

        val response = client.get("/players/12345/stats?period=7d")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals("Zatox", body["name"]!!.jsonPrimitive.content)
        assertEquals("7d", body["period"]!!.jsonPrimitive.content)
        assertEquals(7, body["effective_days"]!!.jsonPrimitive.int)
        assertEquals(12, body["wins_delta"]!!.jsonPrimitive.int)
        assertEquals(1842, body["current_rating"]!!.jsonPrimitive.int)
    }

    @Test
    fun `GET players stats defaults to 7d period when not specified`() = testApplication {
        setupRouting()
        coEvery { mockPlayerStatsService.getPlayerStats(12345L, "7d") } returns GetPlayerStatsResult.Success(testStats)

        val response = client.get("/players/12345/stats")

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET players stats returns 404 when player not tracked`() = testApplication {
        setupRouting()
        coEvery { mockPlayerStatsService.getPlayerStats(any(), any()) } returns GetPlayerStatsResult.PlayerNotFound

        val response = client.get("/players/99999/stats")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `GET players stats returns 204 when no snapshots exist`() = testApplication {
        setupRouting()
        coEvery { mockPlayerStatsService.getPlayerStats(any(), any()) } returns GetPlayerStatsResult.NoSnapshots

        val response = client.get("/players/12345/stats")

        assertEquals(HttpStatusCode.NoContent, response.status)
    }

    @Test
    fun `GET players stats returns 400 for invalid period`() = testApplication {
        setupRouting()
        coEvery { mockPlayerStatsService.getPlayerStats(any(), "bad") } returns GetPlayerStatsResult.InvalidPeriod

        val response = client.get("/players/12345/stats?period=bad")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `GET players stats returns 400 for invalid steamId`() = testApplication {
        setupRouting()

        val response = client.get("/players/not-a-number/stats")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
