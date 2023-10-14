package com.android_t.egg

import java.io.EOFException
import java.io.File
import java.io.InputStream

/**
 * Check if the OpenType font contains a [CORL](https://learn.microsoft.com/zh-cn/typography/opentype/spec/colr) table。
 *
 * * https://learn.microsoft.com/zh-cn/typography/opentype/spec/otff
 * * https://developer.apple.com/fonts/TrueType-Reference-Manual/RM06/Chap6.html
 * * https://www.jianshu.com/p/21ae2dc5c50a
 */
internal object COLRChecker {

    // OpenType fonts that contain TrueType outlines should use the value of 0x00010000 for the sfntVersion.
    private const val SFNT_TRUE_TYPE = 0x00010000

    // OpenType fonts containing CFF data (version 1 or 2) should use 0x4F54544F ('OTTO', when re-interpreted as a Tag) for sfntVersion.
    private const val SFNT_OPEN_TYPE = 0x74727565

    // https://learn.microsoft.com/zh-cn/typography/opentype/spec/colr
    private const val TABLE_TAG_COLR = "COLR"

    fun hasCOLR(file: File): Boolean {
        return file.inputStream().use { input ->
            val sfntVersion = input.readInt()// uint32
            if (sfntVersion != SFNT_TRUE_TYPE && sfntVersion != SFNT_OPEN_TYPE) {
                println("sfntVersion: 0x${sfntVersion.toString(16)}")
                return@use false
            }

            val numTables = input.readShort()// uint16
            if (numTables <= 0) {
                return@use false
            }

            // searchRange,entrySelector and rangeShift uint16 * 3
            input.skip(2 * 3)

            // Tables
            for (i in 0..<numTables) {
                val tableTag = input.readString(4)// uint32
                if (tableTag == TABLE_TAG_COLR) {
                    return true
                }
            }
            return@use false
        }
    }

    private fun InputStream.readString(len: Int): String {
        val array = ByteArray(len)
        read(array)
        return String(array)
    }

    private fun InputStream.readShort(): Short {
        val ch1 = read()
        val ch2 = read()
        if ((ch1 or ch2) < 0) {
            throw EOFException()
        }
        // big_endian
        return ((ch1 shl 8) + ch2).toShort()
    }

    private fun InputStream.readInt(): Int {
        val ch1 = read()
        val ch2 = read()
        val ch3 = read()
        val ch4 = read()
        if ((ch1 or ch2 or ch3 or ch4) < 0) {
            throw EOFException()
        }
        // big_endian
        return (ch1 shl 24) + (ch2 shl 16) + (ch3 shl 8) + ch4
    }
}
