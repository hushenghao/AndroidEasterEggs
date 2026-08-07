package com.dede.android_eggs.cat_editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room3.ColumnInfo
import androidx.room3.ColumnTypeConverter
import androidx.room3.ColumnTypeConverters
import androidx.room3.Dao
import androidx.room3.Database
import androidx.room3.Delete
import androidx.room3.Entity
import androidx.room3.Ignore
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.sqlite.execSQL
import com.dede.android_eggs.cat_editor.Cat.Companion.createCat
import com.dede.basic.globalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A data store for remembering cats.
 */
object CatRememberDataStore {

    private val db by lazy {
        Room.databaseBuilder<CatRememberDatabase>(globalContext, "cat_remember.db")
            .addMigrations(MIGRATION_1_2)
            .setDriver(AndroidSQLiteDriver())
            .build()
    }

    suspend fun remember(seed: Long, colors: List<Color>? = null) {
        withContext(Dispatchers.IO) {
            val cat = createCat(seed, colors)
            db.catDao().remember(cat)
        }
    }

    suspend fun forgetById(id: Long) {
        withContext(Dispatchers.IO) {
            db.catDao().forgetById(id)
        }
    }

    suspend fun forget(seed: Long, colors: List<Color>? = null) {
        withContext(Dispatchers.IO) {
            val cat = createCat(seed, colors)
            db.catDao().forget(cat)
        }
    }

    suspend fun isFavorite(seed: Long, colors: List<Color>): Boolean {
        return withContext(Dispatchers.IO) {
            db.catDao().isFavorite(seed, colors)
        }
    }

    suspend fun getAllCats(): List<Cat> {
        return withContext(Dispatchers.IO) {
            db.catDao().getAllCats()
        }
    }
}

@Database(
    version = 2,
    entities = [Cat::class],
    exportSchema = true,
)
@ColumnTypeConverters(value = [CatColorsConverter::class])
abstract class CatRememberDatabase : RoomDatabase() {
    abstract fun catDao(): CatDao
}

/**
 * v4.3.0 wrote an extra `isMirrorMode` column (identity hash 9638cec1...).
 * Recreate the table without it so both schema variants converge.
 */
private val MIGRATION_1_2 = object : Migration(1, 2) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `remember_cats_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `seed` INTEGER NOT NULL, `colors` TEXT NOT NULL)"
        )
        connection.execSQL(
            "INSERT INTO `remember_cats_new` (`id`, `seed`, `colors`) SELECT `id`, `seed`, `colors` FROM `remember_cats`"
        )
        connection.execSQL("DROP TABLE `remember_cats`")
        connection.execSQL("ALTER TABLE `remember_cats_new` RENAME TO `remember_cats`")
    }
}

@Entity(tableName = "remember_cats")
data class Cat(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "seed") val seed: Long,
    @ColumnInfo(name = "colors") val colors: List<Color>,
) {
    @Ignore
    var isMirrorMode: Boolean = false
        private set

    companion object {
        fun createCat(seed: Long, colors: List<Color>?, isMirrorMode: Boolean = false): Cat {
            return Cat(0, seed, colors ?: listOf(*CatPartColors.colors(seed))).apply {
                this.isMirrorMode = isMirrorMode
            }
        }
    }
}

@Dao
interface CatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun remember(cat: Cat)

    @Query("DELETE FROM remember_cats WHERE id = :id")
    suspend fun forgetById(id: Long)

    @Delete
    suspend fun forget(cat: Cat)

    @Query("SELECT EXISTS(SELECT * FROM remember_cats WHERE seed = :seed AND colors = :colors LIMIT 1)")
    suspend fun isFavorite(seed: Long, colors: List<Color>?): Boolean

    @Query("SELECT * FROM remember_cats ORDER BY id DESC")
    suspend fun getAllCats(): List<Cat>
}

@ColumnTypeConverters
class CatColorsConverter {
    @ColumnTypeConverter
    fun colorsToString(value: List<Color>?): String? {
        return value?.joinToString(separator = ",") { it.toArgb().toString() }
    }

    @ColumnTypeConverter
    fun stringToColors(string: String?): List<Color>? {
        return string?.split(",")?.map { Color(it.toInt()) }
    }
}
