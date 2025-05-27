package com.dede.android_eggs.cat_editor

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.os.BundleCompat
import com.dede.android_eggs.util.applyIf

/**
 * CatEditorRecords is a class that manages the history of color changes in a cat editor.
 */
internal class CatEditorRecords(private val maxSize: Int, recordIndex: Int = 0) {

    internal object SaverImpl : Saver<CatEditorRecords, Bundle> {

        private const val KEY_MAX_SIZE = "max_size"
        private const val KEY_RECORD_INDEX = "record_index"
        private const val KEY_RECORDS = "records"

        override fun restore(value: Bundle): CatEditorRecords {
            val maxSize = value.getInt(KEY_MAX_SIZE)
            val recordIndex = value.getInt(KEY_RECORD_INDEX)
            val records =
                BundleCompat.getParcelableArrayList(value, KEY_RECORDS, Record::class.java)
            return CatEditorRecords(maxSize, recordIndex).applyIf(records != null) {
                this.records.addAll(records!!)
            }
        }

        override fun SaverScope.save(value: CatEditorRecords): Bundle {
            return Bundle().apply {
                putInt(KEY_MAX_SIZE, value.maxSize)
                putInt(KEY_RECORD_INDEX, value.recordIndex)
                putParcelableArrayList(KEY_RECORDS, ArrayList(value.records))
            }
        }
    }

    companion object {

        private const val TAG = "CatEditorRecords"

        fun seed(seed: Long): Record {
            return SeedRecord(seed)
        }

        fun colors(colors: List<Color>, seed: Long): Record {
            return ColorsRecord(ArrayList(colors), seed)
        }

        @Composable
        fun rememberCatEditorRecords(maxSize: Int = 50, firstRecord: Record? = null): CatEditorRecords {
            return rememberSaveable(saver = SaverImpl) {
                val catEditorRecords = CatEditorRecords(maxSize)
                if (firstRecord != null && catEditorRecords.recordCount == 0) {
                    catEditorRecords.addRecord(firstRecord)
                }
                return@rememberSaveable catEditorRecords
            }
        }

        fun restoreRecord(
            record: Record?,
            controller: CatEditorController,
            seedState: MutableLongState
        ) {
            record?.restore(controller, seedState)
        }
    }

    internal abstract class Record(val seed: Long) : Parcelable {
        abstract fun restore(controller: CatEditorController, seedState: MutableLongState)
    }

    private class ColorsRecord(private val colors: List<Color>, seed: Long) : Record(seed) {
        override fun restore(controller: CatEditorController, seedState: MutableLongState) {
            seedState.longValue = seed
            controller.updateColors(colors)
        }

        override fun toString(): String {
            val colorsStr = colors.joinToString(
                prefix = "[",
                separator = ",",
                postfix = "]"
            ) { Utilities.getHexColor(it, true) }
            return "ColorRecord(colors=$colorsStr, seed=$seed)"
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeIntArray(colors.map(Color::toArgb).toIntArray())
            dest.writeLong(seed)
        }

        companion object CREATOR : Parcelable.Creator<ColorsRecord> {
            override fun createFromParcel(parcel: Parcel): ColorsRecord {
                val colors = IntArray(CatPartColors.COLOR_SIZE)
                parcel.readIntArray(colors)
                val seed = parcel.readLong()
                return ColorsRecord(colors.map(::Color).toList(), seed)
            }

            override fun newArray(size: Int): Array<ColorsRecord?> {
                return arrayOfNulls(size)
            }
        }
    }

    private open class SeedRecord(seed: Long) : Record(seed) {
        override fun restore(controller: CatEditorController, seedState: MutableLongState) {
            seedState.longValue = seed
            controller.updateColors(seed)
        }

        override fun toString(): String {
            return "SeedRecord(seed=$seed)"
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeLong(seed)
        }

        companion object CREATOR : Parcelable.Creator<SeedRecord> {
            override fun createFromParcel(parcel: Parcel): SeedRecord {
                val seed = parcel.readLong()
                return SeedRecord(seed)
            }

            override fun newArray(size: Int): Array<SeedRecord?> {
                return arrayOfNulls(size)
            }
        }
    }

    private val recordIndexState = mutableIntStateOf(recordIndex)

    private val records = ArrayDeque<Record>()

    private var recordIndex by recordIndexState

    val recordCount: Int get() = records.size

    fun canGoBack(): Boolean {
        return recordIndex > 0
    }

    fun canGoNext(): Boolean {
        return recordIndex < records.size - 1
    }

    private fun getRecord(index: Int): Record? {
        return records.getOrNull(index)
    }

    fun goBack(): Record? {
        return getRecord(--recordIndex).apply {
            Log.i(TAG, "goBack, index: $recordIndex, count: $recordCount")
        }
    }

    fun goNext(): Record? {
        return getRecord(++recordIndex).apply {
            Log.i(TAG, "goNext, index: $recordIndex, count: $recordCount")
        }
    }

    fun addRecord(record: Record) {
        Log.i(TAG, "addRecord: $record")

        val from = recordIndex
        while (from >= 0 && records.size - 1 > from) {
            records.removeLast()
        }
        records.addLast(record)

        while (records.size > maxSize) {
            records.removeFirst()
        }
        recordIndex = records.size - 1
    }

}
