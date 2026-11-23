package com.dede.android_eggs.egg_stats

import androidx.room3.Room
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.sqlite.execSQL
import com.dede.basic.globalContext
import com.dede.basic.provider.AndroidVersion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Local play history of every Easter egg.
 *
 * Every write is keyed by the egg's stable string id [EggPlayEvent.eggKey], the same
 * identity the app uses for egg equality and list keys. See [EggPlayEvent] before
 * changing that identity.
 *
 * The database is process-wide and opened lazily, so recording a launch never
 * blocks the click that started it.
 */
object EggStatsRepository {

    private val db by lazy {
        Room.databaseBuilder<EggStatsDatabase>(globalContext, "egg_stats.db")
            .setDriver(AndroidSQLiteDriver())
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun observeSummaries(): Flow<List<EggStatsSummary>> {
        return db.eggPlayEventDao().observeSummaries()
    }

    fun observeHistory(eggKey: String): Flow<List<EggPlayEvent>> {
        return db.eggPlayEventDao().observeHistory(eggKey)
    }

    fun observeRecentEvents(): Flow<List<EggPlayEvent>> {
        return db.eggPlayEventDao().observeRecentEvents()
    }

    fun recordPlayAsync(eggKey: String, source: EggLaunchSource) {
        scope.launch {
            runCatching {
                db.eggPlayEventDao().insert(
                    EggPlayEvent(
                        eggKey = eggKey,
                        playedAt = System.currentTimeMillis(),
                        source = source,
                    )
                )
            }
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            db.eggPlayEventDao().clear()
        }
    }
}

/**
 * v1 stored the egg identity as the full api level range pair
 * (`api_level_first` / `api_level_last`). v2 replaces it with the stable string id
 * [EggPlayEvent.eggKey]. `api_level_first` is always the egg's primary
 * [AndroidVersion.fullApiLevel], so it alone determines the new key; unmapped rows
 * fall back to `legacy_<first>_<last>` so a beta install upgrades without losing data.
 */
private val MIGRATION_1_2 = object : Migration(1, 2) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "ALTER TABLE `egg_play_events` ADD COLUMN `egg_key` TEXT NOT NULL DEFAULT ''"
        )
        val cases = AndroidVersion.values().joinToString(separator = " ") {
            "WHEN `api_level_first` = ${it.fullApiLevel} THEN '${it.id}'"
        }
        connection.execSQL(
            "UPDATE `egg_play_events` SET `egg_key` = CASE $cases " +
                "ELSE 'legacy_' || `api_level_first` || '_' || `api_level_last` END"
        )
        connection.execSQL(
            "CREATE TABLE `egg_play_events_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`egg_key` TEXT NOT NULL, `played_at` INTEGER NOT NULL, `source` TEXT NOT NULL)"
        )
        connection.execSQL(
            "INSERT INTO `egg_play_events_new` (`id`, `egg_key`, `played_at`, `source`) " +
                "SELECT `id`, `egg_key`, `played_at`, `source` FROM `egg_play_events`"
        )
        connection.execSQL("DROP TABLE `egg_play_events`")
        connection.execSQL("ALTER TABLE `egg_play_events_new` RENAME TO `egg_play_events`")
        connection.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_egg_play_events_egg_key_played_at` " +
                "ON `egg_play_events` (`egg_key`, `played_at`)"
        )
        connection.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_egg_play_events_played_at` " +
                "ON `egg_play_events` (`played_at`)"
        )
    }
}