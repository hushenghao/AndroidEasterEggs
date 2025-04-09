package com.dede.android_eggs.cat_editor

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * CatEditorRecords is a class that manages the history of color changes in a cat editor.
 */
internal class CatEditorRecords(recordIndexState: MutableIntState, private val maxSize: Int) {

    companion object {

        private const val TAG = "CatEditorRecords"

        fun speed(speed: Long): Record {
            return SpeedRecord(speed)
        }

        fun colors(colors: List<Color>, speed: Long): Record {
            return ColorsRecord(ArrayList(colors), speed)
        }

        @Composable
        fun rememberCatEditorRecords(maxSize: Int = 50): CatEditorRecords {
            val state = remember { mutableIntStateOf(0) }
            return remember { CatEditorRecords(state, maxSize) }
        }

        fun restoreRecord(
            record: Record?,
            controller: CatEditorController,
            speedState: MutableLongState
        ) {
            record?.restore(controller, speedState)
        }
    }

    internal abstract class Record(val speed: Long) {
        abstract fun restore(controller: CatEditorController, speedState: MutableLongState)
    }

    private class ColorsRecord(private val colors: List<Color>, speed: Long) : Record(speed) {
        override fun restore(controller: CatEditorController, speedState: MutableLongState) {
            speedState.longValue = speed
            controller.updateColors(colors)
        }

        override fun toString(): String {
            val colorsStr = colors.joinToString(
                prefix = "[",
                separator = ",",
                postfix = "]"
            ) { Utilities.getHexColor(it, true) }
            return "ColorRecord(colors=$colorsStr, speed=$speed)"
        }
    }

    private open class SpeedRecord(speed: Long) : Record(speed) {
        override fun restore(controller: CatEditorController, speedState: MutableLongState) {
            speedState.longValue = speed
            controller.updateColors(speed)
        }

        override fun toString(): String {
            return "SpeedRecord(speed=$speed)"
        }
    }

    private val records = ArrayDeque<Record>()

    private var recordIndex by recordIndexState

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
        return getRecord(--recordIndex)
    }

    fun goNext(): Record? {
        return getRecord(++recordIndex)
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
