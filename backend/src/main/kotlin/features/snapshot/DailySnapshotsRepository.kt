package com.brawlpulse.api.features.snapshot

interface DailySnapshotsRepository {

    suspend fun addDailySnapshot() : DailySnapshot

    suspend fun deleteAllSnapshots(steamId : Long)

    suspend fun getAllSnapshots(steamId: Long) : List<DailySnapshot>
}