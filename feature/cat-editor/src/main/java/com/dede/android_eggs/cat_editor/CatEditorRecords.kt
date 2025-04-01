package com.dede.android_eggs.cat_editor

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * CatEditorRecords is a class that manages the history of color changes in a cat editor.
 */
internal class CatEditorRecords(recordIndexState: MutableIntState) {

    companion object {

        private const val TAG = "CatEditorRecords"

        fun speed(speed: Long): Record {
            return SpeedRecord(speed)
        }

        fun color(color: Int, index: Int): Record {
            return ColorRecord(color, index)
        }

        @Composable
        fun rememberCatEditorRecords(): CatEditorRecords {
            val state = remember { mutableIntStateOf(0) }
            return remember { CatEditorRecords(state) }
        }
    }

    internal interface Record {
        fun restore(colors: SnapshotStateList<Int>)
    }

    internal data class ColorRecord(private val color: Int, private val index: Int) : Record {
        override fun restore(colors: SnapshotStateList<Int>) {
            colors[index] = color
        }
    }

    internal data class SpeedRecord(private val speed: Long) : Record {
        override fun restore(colors: SnapshotStateList<Int>) {
            val src = CatPartColors.colors(speed)
            src.forEachIndexed { index, color ->
                colors[index] = color
            }
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

    fun goBack(colors: SnapshotStateList<Int>) {
        getRecord(--recordIndex)?.restore(colors)
    }

    fun goNext(colors: SnapshotStateList<Int>) {
        getRecord(++recordIndex)?.restore(colors)
    }

    fun addRecord(record: Record) {
        if (records.contains(record)) {
            Log.i(TAG, "addRecord: already contains")
            return
        }
        Log.i(TAG, "addRecord: $record")

        val from = recordIndex
        while (from >= 0 && records.size - 1 > from) {
            records.removeLast()
        }
        records.addLast(record)
        recordIndex = records.size - 1
    }

}
