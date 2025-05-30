package com.dede.android_eggs.cat_editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.dede.basic.globalContext

/**
 * A data store for remembering cats.
 */
object CatRememberDataStore {

    @Database(entities = [Cat::class], version = 1)
    @TypeConverters(value = [CatColorsConverter::class])
    abstract class CatRememberDatabase : RoomDatabase() {
        abstract fun catDan(): CatDao
    }

    @Dao
    interface CatDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun remember(cat: Cat)

        @Query("DELETE FROM remember_cats WHERE id = :id")
        fun forgetById(id: Long)

        @Delete
        fun forget(cat: Cat)

        @Query("SELECT EXISTS(SELECT * FROM remember_cats WHERE seed = :seed AND colors = :colors)")
        fun isFavorite(seed: Long, colors: List<Color>?): Boolean

        @Query("SELECT * FROM remember_cats ORDER BY id DESC")
        fun getAllCats(): List<Cat>
    }

    @Entity(tableName = "remember_cats")
    data class Cat(
        @PrimaryKey(autoGenerate = true) val id: Long,
        @ColumnInfo(name = "seed") val seed: Long,
        @ColumnInfo(name = "colors") val colors: List<Color>
    )

    @TypeConverters
    class CatColorsConverter {

        @TypeConverter
        fun colorsToString(value: List<Color>?): String? {
            return value?.joinToString(separator = ",") { it.toArgb().toString() }
        }

        @TypeConverter
        fun stringToColors(string: String?): List<Color>? {
            return string?.split(",")?.map { Color(it.toInt()) }
        }
    }

    private val db by lazy {
        Room.databaseBuilder<CatRememberDatabase>(globalContext, "cat_remember.db")
            .build()
    }

    private fun createCat(seed: Long, colors: List<Color>?): Cat {
        return Cat(0, seed, colors ?: listOf(*CatPartColors.colors(seed)))
    }

    fun remember(seed: Long, colors: List<Color>? = null) {
        val cat = createCat(seed, colors)
        db.catDan().remember(cat)
    }

    fun forgetById(id: Long) {
        db.catDan().forgetById(id)
    }

    fun forget(seed: Long, colors: List<Color>? = null) {
        val cat = createCat(seed, colors)
        db.catDan().forget(cat)
    }

    fun isFavorite(seed: Long, colors: List<Color>): Boolean {
        return db.catDan().isFavorite(seed, colors)
    }

    fun getAllCats(): List<Cat> {
        return db.catDan().getAllCats()
    }
}
