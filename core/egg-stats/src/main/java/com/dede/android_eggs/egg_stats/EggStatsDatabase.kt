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
    version = 2,
    entities = [EggPlayEvent::class],
    exportSchema = true,
)
@ColumnTypeConverters(value = [EggLaunchSourceConverter::class])
internal abstract class EggStatsDatabase : RoomDatabase() {
    abstract fun eggPlayEventDao(): EggPlayEventDao
}

/**
 * One row per Easter egg launch. First opened time, last played time and play count
 * are aggregated from these events instead of being stored separately, so a
 * per-egg history view needs no extra bookkeeping.
 *
 * Identity: [eggKey] is the egg's stable string id (see `EasterEgg.id` /
 * `AndroidVersion.id`), the same identity used by `EasterEgg.equals/hashCode` and
 * `BaseEasterEgg.lazyItemKey()`. It replaced the old full api level range pair, so a
 * future range change no longer orphans history and preview eggs keep their own key.
 */
@Entity(
    tableName = "egg_play_events",
    indices = [
        Index(value = ["egg_key", "played_at"]),
        Index(value = ["played_at"]),
    ],
)
data class EggPlayEvent(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    /** The egg's stable string id; the play-history identity. */
    @ColumnInfo(name = "egg_key") val eggKey: String,
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
internal class EggLaunchSourceConverter {

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
 * Aggregate of [EggPlayEvent] rows for one Easter egg, keyed by its stable [eggKey].
 */
data class EggStatsSummary(
    val eggKey: String,
    val firstOpenedAt: Long,
    val lastPlayedAt: Long,
    val playCount: Int,
)

@Dao
internal interface EggPlayEventDao {

    @Insert
    suspend fun insert(event: EggPlayEvent)

    /**
     * Per-egg totals, grouped by the stable egg key. A single key keeps preview eggs
     * such as T Beta separate from their stable release.
     */
    @Query(
        """
        SELECT egg_key AS eggKey,
               MIN(played_at) AS firstOpenedAt,
               MAX(played_at) AS lastPlayedAt,
               COUNT(*) AS playCount
        FROM egg_play_events
        GROUP BY egg_key
        """
    )
    fun observeSummaries(): Flow<List<EggStatsSummary>>

    /** Every play of one egg, newest first; reserved for a per-egg history view. */
    @Query(
        """
        SELECT * FROM egg_play_events
        WHERE egg_key = :eggKey
        ORDER BY played_at DESC
        """
    )
    fun observeHistory(eggKey: String): Flow<List<EggPlayEvent>>

    @Query("SELECT * FROM egg_play_events ORDER BY played_at DESC")
    fun observeRecentEvents(): Flow<List<EggPlayEvent>>

    @Query("DELETE FROM egg_play_events")
    suspend fun clear()
}