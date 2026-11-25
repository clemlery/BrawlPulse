package com.brawlpulse.api.plugins

import com.brawlpulse.api.features.player.PlayerService
import com.brawlpulse.api.features.player.playerRoutes
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.mockk
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class StatusPagesTest {

    private val playerService = mockk<PlayerService>()

    private fun ApplicationTestBuilder.setup() {
        application {
            configureSerialization()
            configureStatusPages()
            routing {
                playerRoutes(playerService, "test-api-key")
                get("/boom") { error("secret internal detail") }
            }
        }
    }

    private suspend fun HttpResponse.errorField(): String =
        Json.parseToJsonElement(bodyAsText()).jsonObject["error"]!!.jsonPrimitive.content

    private suspend fun ApplicationTestBuilder.postPlayers(body: String) =
        client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }

    @Test
    fun `malformed JSON body returns 400 with a generic message`() = testApplication {
        setup()

        val response = postPlayers("""{"steamId": "oops"""")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Bad request", response.errorField())
    }

    @Test
    fun `wrong field type returns 400`() = testApplication {
        setup()

        val response = postPlayers("""{"steamId": "oops"}""")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertFalse(response.bodyAsText().contains("AddPlayerRequest"), "Internal class name leaked")
    }

    @Test
    fun `empty body returns 415 without leaking the target class`() = testApplication {
        setup()

        val response = postPlayers("")

        assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        assertEquals("Unsupported or empty request body", response.errorField())
    }

    @Test
    fun `non JSON content type returns 415 without leaking the target class`() = testApplication {
        setup()

        val response = client.post("/players") {
            contentType(ContentType.Text.Plain)
            setBody("steamId=1")
        }

        assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        assertEquals("Unsupported or empty request body", response.errorField())
    }

    @Test
    fun `unexpected exception returns 500 without its details`() = testApplication {
        setup()

        val response = client.get("/boom")

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals("Internal server error", response.errorField())
        assertFalse(response.bodyAsText().contains("secret internal detail"))
    }
}
