package com.brawlpulse.api.features.stats

import com.brawlpulse.api.features.player.PlayerRepository
import com.brawlpulse.api.features.snapshot.DailySnapshotsRepository
import com.brawlpulse.api.features.stats.result.GetPlayerStatsResult
import java.math.RoundingMode
import java.time.LocalDate
import java.time.temporal.ChronoUnit

open class PlayerStatsService(
    private val playerRepository: PlayerRepository,
    private val dailySnapshotsRepository: DailySnapshotsRepository
) {

    open suspend fun getPlayerStats(steamId: Long, period: String): GetPlayerStatsResult {
        val player = playerRepository.getPlayer(steamId)
            ?: return GetPlayerStatsResult.PlayerNotFound

        val allSnapshots = dailySnapshotsRepository.getAllSnapshots(steamId)
        if (allSnapshots.isEmpty()) return GetPlayerStatsResult.NoSnapshots

        val cutoffDate: LocalDate = when (period) {
            "7d" -> LocalDate.now().minusDays(7)
            "4w" -> LocalDate.now().minusWeeks(4)
            "6m" -> LocalDate.now().minusMonths(6)
            "1y" -> LocalDate.now().minusYears(1)
            "all" -> LocalDate.MIN
            else -> return GetPlayerStatsResult.InvalidPeriod
        }

        val inPeriod = allSnapshots.filter { it.snapshotDate >= cutoffDate }
        val snapshots = if (inPeriod.isEmpty()) allSnapshots else inPeriod

        val oldest = snapshots.first()
        val newest = snapshots.last()

        val effectiveDays = ChronoUnit.DAYS.between(oldest.snapshotDate, newest.snapshotDate).toInt() + 1
        val winsDelta = newest.wins - oldest.wins
        val gamesDelta = newest.games - oldest.games
        val winrate = if (gamesDelta > 0) {
            (winsDelta.toDouble() / gamesDelta * 100)
                .toBigDecimal()
                .setScale(1, RoundingMode.HALF_UP)
                .toDouble()
        } else 0.0

        return GetPlayerStatsResult.Success(
            PlayerStats(
                name = player.currentName,
                period = period,
                effectiveDays = effectiveDays,
                winsDelta = winsDelta,
                gamesDelta = gamesDelta,
                winrate = winrate,
                ratingDelta = newest.rating - oldest.rating,
                currentRating = newest.rating,
                snapshotCount = snapshots.size
            )
        )
    }
}
