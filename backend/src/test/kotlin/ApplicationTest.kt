package com.brawlpulse.api

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Ignore("Integration test requiring a running DB and full config — not suitable for unit test run")
    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}
