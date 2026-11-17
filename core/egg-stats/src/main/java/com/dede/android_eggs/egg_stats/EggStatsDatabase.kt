package com.dede.android_eggs.egg_stats

import androidx.room3.ColumnInfo
import androidx.room3.ColumnTypeConverter
import androidx.room3.ColumnTypeConverters
import androidx.room3.Dao
import androidx.room3.Database
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.Insert
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Database(
    version = 1,
    entities = [EggPlayEvent::class],
    exportSchema = true,
)
@ColumnTypeConverters(value = [EggLaunchSourceConverter::class])
abstract class EggStatsDatabase : RoomDatabase() {
    abstract fun eggPlayEventDao(): EggPlayEventDao
}

/**
 * One row per Easter egg launch. First opened time, last played time and play count
 * are aggregated from these events instead of being stored separately, so a
 * per-egg history view needs no extra bookkeeping.
 *
 * Identity: [apiLevelFirst] and [apiLevelLast] are the egg's full api level range,
 * the same identity used by `EasterEgg.equals/hashCode` and
 * `BaseEasterEgg.lazyItemKey()`. Keep both columns and treat the pair as the egg
 * id — the range is the identity, not a display detail:
 *
 * - A single `apiLevelFirst` is not enough: eggs can span a range (e.g. 2.3-2.3.3),
 *   and nothing guarantees that two ranges never share their first level.
 * - Do not key stats by public api level either: preview eggs such as T Beta share
 *   their public api level with the stable release and would be merged.
 * - Egg metadata is code-defined and localized, so there is deliberately no separate
 *   `eggs` table to join against.
 *
 * If an egg's range is ever changed, its history becomes orphaned under the old pair.
 * That is the point to migrate to a single stable `egg_key` column (optionally backed
 * by an overridable `EasterEgg.id`), not a reason to normalize the pair early.
 */
@Entity(
    tableName = "egg_play_events",
    indices = [
        // Per-egg history: the range pair is the identity, played_at orders the events.
        Index(value = ["api_level_first", "api_level_last", "played_at"]),
        // Global recent records, reserved for a future history screen.
        Index(value = ["played_at"]),
    ],
)
data class EggPlayEvent(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    /** Full api level range start; together with [apiLevelLast] it is the egg identity. */
    @ColumnInfo(name = "api_level_first") val apiLevelFirst: Int,
    /** Full api level range end; see [apiLevelFirst] and the class docs. */
    @ColumnInfo(name = "api_level_last") val apiLevelLast: Int,
    @ColumnInfo(name = "played_at") val playedAt: Long,
    @ColumnInfo(name = "source") val source: EggLaunchSource,
)

enum class EggLaunchSource {
    LIST,
    SEARCH,
    DEEP_LINK,
    WIDGET,
    SHORTCUT,
    APP_FUNCTION,
    CALLBACK,
    OTHER,
}

@ColumnTypeConverters
class EggLaunchSourceConverter {

    @ColumnTypeConverter
    fun sourceToString(value: EggLaunchSource): String {
        return value.name
    }

    @ColumnTypeConverter
    fun stringToSource(value: String): EggLaunchSource {
        return EggLaunchSource.entries.firstOrNull { it.name == value } ?: EggLaunchSource.OTHER
    }
}

/**
 * Aggregate of [EggPlayEvent] rows for one Easter egg, keyed by its full api level
 * range. See [EggPlayEvent] for why the range pair, not a public api level, is the id.
 */
data class EggStatsSummary(
    val apiLevelFirst: Int,
    val apiLevelLast: Int,
    val firstOpenedAt: Long,
    val lastPlayedAt: Long,
    val playCount: Int,
)

@Dao
interface EggPlayEventDao {

    @Insert
    suspend fun insert(event: EggPlayEvent)

    /**
     * Per-egg totals. Grouping by the full range pair keeps preview eggs such as
     * T Beta separate from their stable release.
     */
    @Query(
        """
        SELECT api_level_first AS apiLevelFirst,
               api_level_last AS apiLevelLast,
               MIN(played_at) AS firstOpenedAt,
               MAX(played_at) AS lastPlayedAt,
               COUNT(*) AS playCount
        FROM egg_play_events
        GROUP BY api_level_first, api_level_last
        """
    )
    fun observeSummaries(): Flow<List<EggStatsSummary>>

    /** Every play of one egg, newest first; the pair must match [EggPlayEvent]'s id. */
    @Query(
        """
        SELECT * FROM egg_play_events
        WHERE api_level_first = :apiLevelFirst AND api_level_last = :apiLevelLast
        ORDER BY played_at DESC
        """
    )
    fun observeHistory(apiLevelFirst: Int, apiLevelLast: Int): Flow<List<EggPlayEvent>>

    @Query("SELECT * FROM egg_play_events ORDER BY played_at DESC")
    fun observeRecentEvents(): Flow<List<EggPlayEvent>>

    @Query("DELETE FROM egg_play_events")
    suspend fun clear()
}
