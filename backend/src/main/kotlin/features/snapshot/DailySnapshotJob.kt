package com.brawlpulse.api.features.snapshot

import com.brawlpulse.api.features.player.PlayerRepository
import com.brawlpulse.api.infrastructure.brawlhalla.BrawlhallaDao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.ZoneOffset
import java.time.ZonedDateTime

class DailySnapshotJob(
    private val playerRepository: PlayerRepository,
    private val dailySnapshotsRepository: DailySnapshotsRepository,
    private val bhClient: BrawlhallaDao,
    private val apiKey: String
) {
    private val logger = LoggerFactory.getLogger(DailySnapshotJob::class.java)

    suspend fun runDailySnapshot() {
        val players = playerRepository.getAllPlayers()
        logger.info("Daily snapshot job started – {} tracked players", players.size)

        var succeeded = 0
        var failed = 0

        for ((index, player) in players.withIndex()) {
            if (index > 0) delay(250)

            try {
                val globalStats = bhClient.getPlayerGlobalStats(player.brawlhallaId, apiKey)
                val rankedStats = bhClient.getPlayerRankedStats(player.brawlhallaId, apiKey)
                dailySnapshotsRepository.addDailySnapshot(player.id, globalStats, rankedStats)
                logger.info(
                    "Snapshot saved – player='{}' brawlhallaId={}",
                    player.currentName, player.brawlhallaId
                )
                succeeded++
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logger.error(
                    "Snapshot failed – player='{}' brawlhallaId={}: {}",
                    player.currentName, player.brawlhallaId, e.message, e
                )
                failed++
            }
        }

        logger.info("Daily snapshot job finished – succeeded={} failed={}", succeeded, failed)
    }

    suspend fun scheduleDaily() {
        while (true) {
            val now = ZonedDateTime.now(ZoneOffset.UTC)
            var next = now.toLocalDate().atTime(2, 0).atZone(ZoneOffset.UTC)
            if (!next.isAfter(now)) next = next.plusDays(1)

            val delayMs = Duration.between(now, next).toMillis()
            logger.info("Next daily snapshot scheduled at {} (in {}s)", next, delayMs / 1000)
            delay(delayMs)

            try {
                runDailySnapshot()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logger.error("Daily snapshot job crashed unexpectedly: {}", e.message, e)
            }
        }
    }
}
