@file:JvmName("Utils")

package com.dede.android_eggs.util

import android.os.Build
import android.os.Bundle
import java.util.Locale
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.max


fun isXiaomi(): Boolean {
    return Build.MANUFACTURER.lowercase(Locale.ENGLISH) == "xiaomi"
}

fun compareStringVersion(version1: String, version2: String): Int {
    if (version1 == version2) return 0

    val versionSplit = Regex("[.\\-_]")
    val versionArr1 = version1.split(versionSplit)
    val versionArr2 = version2.split(versionSplit)
    val len = max(versionArr1.size, versionArr2.size)
    var ver1: Int
    var ver2: Int
    var result = 0
    for (i in 0..<len) {
        ver1 = versionArr1.getOrNull(i)?.toIntOrNull() ?: 0
        ver2 = versionArr2.getOrNull(i)?.toIntOrNull() ?: 0
        result = ver1.compareTo(ver2)
        if (result != 0) break
    }
    return result
}

@OptIn(ExperimentalContracts::class)
inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    if (condition) block()
    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <T, I> T.applyNotNull(input: I?, block: T.(input: I) -> Unit): T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    if (input != null) block(input)
    return this
}

@Suppress("DEPRECATION")
fun Bundle?.isEquals(other: Any?): Boolean {
    if (this === other) return true
    if (this == null || other == null) return false

    if (other !is Bundle) return false

    if (this.size() != other.size()) return false

    val keySet = this.keySet()
    if (!keySet.equals(other.keySet())) return false

    for (key in keySet) {
        val any = this[key]
        if (any is Bundle) {
            return any.isEquals(other[key])
        }
        if (any != other[key]) {
            return false
        }
    }

    return true
}