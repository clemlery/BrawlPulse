package com.brawlpulse.api.features.player

import com.brawlpulse.api.features.player.exceptions.BrawlhallaApiUnavailableException
import com.brawlpulse.api.features.player.exceptions.SteamIdNotLinkedException
import com.brawlpulse.api.features.player.result.AddPlayerResult
import com.brawlpulse.api.plugins.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import kotlinx.serialization.json.*
import org.junit.Before
import java.time.OffsetDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class PlayerRoutesTest {

    private val mockPlayerService = mockk<PlayerService>()
    private val testApiKey = "test-api-key"

    private val testPlayer = Player(
        id = 1,
        steamId = 76561198123456789L,
        brawlhallaId = 123456,
        currentName = "TestPlayer",
        addedAt = OffsetDateTime.now()
    )

    @Before
    fun setUp() {
        clearMocks(mockPlayerService)
    }

    private fun ApplicationTestBuilder.setupRouting() {
        application {
            configureSerialization()
            routing {
                playerRoutes(mockPlayerService, testApiKey)
            }
        }
    }

    @Test
    fun `POST players returns 201 Created when new player is added`() = testApplication {
        setupRouting()
        coEvery { mockPlayerService.addPlayer(testPlayer.steamId, testApiKey) } returns AddPlayerResult.Created(testPlayer)

        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody("""{"steamId": ${testPlayer.steamId}}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals(testPlayer.steamId, body["steamId"]!!.jsonPrimitive.long)
        assertEquals(testPlayer.brawlhallaId, body["brawlhallaId"]!!.jsonPrimitive.int)
        assertEquals(testPlayer.currentName, body["currentName"]!!.jsonPrimitive.content)
    }

    @Test
    fun `POST players returns 200 OK when player is already tracked`() = testApplication {
        setupRouting()
        coEvery { mockPlayerService.addPlayer(testPlayer.steamId, testApiKey) } returns AddPlayerResult.AlreadyTracked(testPlayer)

        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody("""{"steamId": ${testPlayer.steamId}}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals(testPlayer.steamId, body["steamId"]!!.jsonPrimitive.long)
    }

    @Test
    fun `POST players returns 404 when steamId is not linked to Brawlhalla`() = testApplication {
        setupRouting()
        coEvery { mockPlayerService.addPlayer(any(), testApiKey) } throws SteamIdNotLinkedException("SteamId not linked")

        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody("""{"steamId": 99999999999}""")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `POST players returns 503 when Brawlhalla API is unavailable`() = testApplication {
        setupRouting()
        coEvery { mockPlayerService.addPlayer(any(), testApiKey) } throws BrawlhallaApiUnavailableException("API unavailable")

        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody("""{"steamId": 12345678901}""")
        }

        assertEquals(HttpStatusCode.ServiceUnavailable, response.status)
    }

    @Test
    fun `POST players returns 400 when request body is missing steamId`() = testApplication {
        setupRouting()

        val response = client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
