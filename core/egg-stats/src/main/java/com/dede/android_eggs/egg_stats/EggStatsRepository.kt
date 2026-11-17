package com.dede.android_eggs.egg_stats

import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.dede.basic.globalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Local play history of every Easter egg.
 *
 * Every write is keyed by the egg's full api level range, the same identity the app
 * uses for egg equality and list keys. See [EggPlayEvent] before changing that
 * identity (for example to a public api level or a single level).
 *
 * The database is process-wide and opened lazily, so recording a launch never
 * blocks the click that started it.
 */
object EggStatsRepository {

    private val db by lazy {
        Room.databaseBuilder<EggStatsDatabase>(globalContext, "egg_stats.db")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun observeSummaries(): Flow<List<EggStatsSummary>> {
        return db.eggPlayEventDao().observeSummaries()
    }

    fun observeHistory(apiLevelFirst: Int, apiLevelLast: Int): Flow<List<EggPlayEvent>> {
        return db.eggPlayEventDao().observeHistory(apiLevelFirst, apiLevelLast)
    }

    fun observeRecentEvents(): Flow<List<EggPlayEvent>> {
        return db.eggPlayEventDao().observeRecentEvents()
    }

    fun recordPlayAsync(apiLevelFirst: Int, apiLevelLast: Int, source: EggLaunchSource) {
        scope.launch {
            runCatching {
                db.eggPlayEventDao().insert(
                    EggPlayEvent(
                        apiLevelFirst = apiLevelFirst,
                        apiLevelLast = apiLevelLast,
                        playedAt = System.currentTimeMillis(),
                        source = source,
                    )
                )
            }
        }
    }

    fun recordPlayAsync(fullApiLevelRange: IntRange, source: EggLaunchSource) {
        recordPlayAsync(fullApiLevelRange.first, fullApiLevelRange.last, source)
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            db.eggPlayEventDao().clear()
        }
    }
}
