package com.android_t.egg

import android.util.Log
import okio.BufferedSource
import okio.FileHandle
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import okio.buffer
import okio.use
import java.io.File
import java.io.IOException

/**
 * Check if the OpenType font contains a [CORL](https://learn.microsoft.com/zh-cn/typography/opentype/spec/colr) table。
 *
 * * [Microsoft, The OpenType Font File](https://learn.microsoft.com/zh-cn/typography/opentype/spec/otff)
 * * [Apple, TrueType Font Tables](https://developer.apple.com/fonts/TrueType-Reference-Manual/RM06/Chap6.html)
 * * https://www.jianshu.com/p/21ae2dc5c50a
 * * [Microsoft, TTC Header](https://learn.microsoft.com/zh-cn/typography/opentype/spec/otff#ttc-header)
 */
internal object COLRChecker {

    // TTC Header `ttcf` bytes to int value
    // https://learn.microsoft.com/zh-cn/typography/opentype/spec/otff#ttc-header
    private const val TTC_TAG: Int = 0x74746366

    // OpenType fonts that contain TrueType outlines should use the value of 0x00010000 for the sfntVersion.
    private const val SFNT_VERSION_1: Int = 0x00010000

    // OpenType fonts containing CFF data (version 1 or 2) should use 0x4F54544F ('OTTO', when re-interpreted as a Tag) for sfntVersion.
    private const val SFNT_VERSION_OTTO: Int = 0x4F54544F

    // Table `COLR` bytes to int value
    // https://learn.microsoft.com/zh-cn/typography/opentype/spec/colr
    private const val COLR_TABLE_TAG = 0x434F4C52

    @Throws(IOException::class)
    private fun hasCOLR(fileHandle: FileHandle, source: BufferedSource, position: Long): Boolean {
        fileHandle.reposition(source, position)

        val sfntVersion = source.readInt()// uint32
        if (sfntVersion != SFNT_VERSION_1 && sfntVersion != SFNT_VERSION_OTTO) {
            Log.i(TAG, "Unknown sfntV: 0x${sfntVersion.toString(16)}")
            return false
        }

        val numTables = source.readShort()// uint16
        if (numTables <= 0) {
            return false
        }

        // searchRange,entrySelector and rangeShift uint16 * 3
        source.skip(2 * 3)

        // Tables
        for (i in 0..<numTables) {
            val tableTag = source.readInt()// uint32
            if (tableTag == COLR_TABLE_TAG) {
                return true
            }
            // checksum,offset and length  unit32 * 3
            source.skip(4 * 3)
        }
        return false
    }

    fun hasCOLR(file: File): Boolean {
        if (!file.exists()) return false
        // ByteOrder.BIG_ENDIAN
        return FileSystem.SYSTEM.openReadOnly(file.toOkioPath()).use {
            val source = it.source().buffer()

            val magicNumber = source.readInt()
            if (magicNumber != TTC_TAG) {
                // TTF,OTF font
                return@use hasCOLR(it, source, 0L)
            }

            // TTC,OTC font
            source.skip(2 * 2)// ttc version uint16 * 2
            val numFonts = source.readInt()// numFonts uint32
            var fontFileOffset: Int
            var hasCLOR: Boolean
            for (i in 0..<numFonts) {
                fontFileOffset = source.readInt()
                hasCLOR = hasCOLR(it, source, fontFileOffset.toLong())
                if (hasCLOR) {
                    return@use true
                }
            }
            return@use false
        }
    }

}
