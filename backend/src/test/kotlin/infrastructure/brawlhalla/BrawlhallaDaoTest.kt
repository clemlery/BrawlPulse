package com.brawlpulse.api.infrastructure.brawlhalla

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BrawlhallaDaoTest {

    private val apiKey = "super-secret-api-key"

    private fun daoRespondingWith(status: HttpStatusCode, body: String) =
        BrawlhallaDao(MockEngine { respond(content = body, status = status) })

    private fun Throwable.fullStackTrace(): String =
        java.io.StringWriter().also { printStackTrace(java.io.PrintWriter(it)) }.toString()

    @Test
    fun `forbidden response does not leak the api key`() = runBlocking {
        val dao = daoRespondingWith(HttpStatusCode.Forbidden, "Invalid api key")

        val exception = assertFailsWith<InvalidApiKeyException> {
            dao.getPlayerGlobalStats(42, apiKey)
        }

        assertFalse(exception.fullStackTrace().contains(apiKey), "API key leaked in stack trace")
        assertTrue(exception.message!!.contains("/player/42/stats"))
        assertTrue(exception.message!!.contains("403"))
    }

    @Test
    fun `not found response does not leak the api key`() = runBlocking {
        val dao = daoRespondingWith(HttpStatusCode.NotFound, "Not Found")

        val exception = assertFailsWith<NotFoundException> {
            dao.searchPlayer(76561198000000000, apiKey)
        }

        assertFalse(exception.fullStackTrace().contains(apiKey), "API key leaked in stack trace")
    }

    @Test
    fun `server errors are retried before failing without leaking the api key`() = runBlocking {
        var calls = 0
        val dao = BrawlhallaDao(MockEngine {
            calls++
            respond(content = "Service Unavailable", status = HttpStatusCode.ServiceUnavailable)
        })

        val exception = assertFailsWith<ServerErrorException> {
            dao.getPlayerRankedStats(42, apiKey)
        }

        assertEquals(4, calls, "Expected 1 call + 3 retries")
        assertFalse(exception.fullStackTrace().contains(apiKey), "API key leaked in stack trace")
    }
}
