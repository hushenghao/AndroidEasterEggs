@file:Suppress("KDocUnresolvedReference")

package com.android_t.egg

import android.util.Log
import okio.BufferedSource
import okio.FileHandle
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import okio.buffer
import okio.use
import java.io.File
import java.io.IOException

/**
 * Check if the OpenType font contains a [CORL](https://learn.microsoft.com/zh-cn/typography/opentype/spec/colr) table。
 *
 * * [Microsoft, The OpenType Font File](https://learn.microsoft.com/zh-cn/typography/opentype/spec/otff)
 * * [Microsoft, TTC Header](https://learn.microsoft.com/zh-cn/typography/opentype/spec/otff#ttc-header)
 * * [Apple, TrueType Font Tables](https://developer.apple.com/fonts/TrueType-Reference-Manual/RM06/Chap6.html)
 * * https://www.jianshu.com/p/21ae2dc5c50a
 *
 * @see [android.graphics.fonts.FontFileUtil]
 */
internal object COLRv1 {

    // TTC Header `ttcf` bytes to int value
    // https://learn.microsoft.com/zh-cn/typography/opentype/spec/otff#ttc-header
    private const val TTC_TAG: Int = 0x74746366

    // OpenType fonts that contain TrueType outlines should use the value of 0x00010000 for the sfntVersion.
    private const val SFNT_VERSION_1: Int = 0x00010000

    // OpenType fonts containing CFF data (version 1 or 2) should use 0x4F54544F ('OTTO', when re-interpreted as a Tag) for sfntVersion.
    private const val SFNT_VERSION_OTTO: Int = 0x4F54544F

    // Table `COLR` bytes to int value
    // https://learn.microsoft.com/zh-cn/typography/opentype/spec/colr
    private const val COLR_TABLE_TAG: Int = 0x434F4C52

    // COLRv1
    // https://developer.android.google.cn/about/versions/13/features?hl=zh-cn#color-vector-fonts
    private const val COLR_VERSION_1: Short = 1

    @Throws(IOException::class)
    private fun analyzeCOLR(
        fileHandle: FileHandle,
        source: BufferedSource,
        position: Int,
    ): Boolean {
        fileHandle.reposition(source, position.toLong())

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
                source.skip(4)// checksum
                val offset = source.readInt()
                fileHandle.reposition(source, offset.toLong())
                val version = source.readShort()
                Log.i(TAG, "COLRv$version")
                return version == COLR_VERSION_1
            }
            // checksum,offset and length  unit32 * 3
            source.skip(4 * 3)
        }
        return false
    }

    fun analyzeCOLR(file: File): Boolean {
        val fontPath = file.toOkioPath()
        try {
            return analyzeCOLR(fontPath)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return false
    }

    @Throws(IOException::class)
    private fun analyzeCOLR(fontPath: Path): Boolean {
        // ByteOrder.BIG_ENDIAN
        return FileSystem.SYSTEM.openReadOnly(fontPath).use {
            val source = it.source().buffer()

            val magicNumber = source.readInt()
            if (magicNumber != TTC_TAG) {
                // TTF,OTF font
                return@use analyzeCOLR(it, source, 0)
            }

            // TTC,OTC font
            source.skip(2 * 2)// ttc version uint16 * 2
            val numFonts = source.readInt()// numFonts uint32
            val tableDirectoryOffsets = (0..<numFonts).map { source.readInt() }// uint32 * numFonts
            for (offset in tableDirectoryOffsets) {
                if (analyzeCOLR(it, source, offset)) {
                    return@use true
                }
            }
            return@use false
        }
    }

}
