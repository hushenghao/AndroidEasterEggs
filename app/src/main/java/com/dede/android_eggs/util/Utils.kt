@file:JvmName("Utils")

package com.dede.android_eggs.util

import android.os.Build
import android.os.Bundle
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


fun isXiaomi(): Boolean {
    return Build.MANUFACTURER.lowercase(Locale.ENGLISH) == "xiaomi"
}

@OptIn(ExperimentalContracts::class)
inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    if (condition) block()
    return this
}

@OptIn(ExperimentalContracts::class)
inline fun <T, I> T.applyNotNull(input: I?, block: T.(input: I) -> Unit): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    if (input != null) block(input)
    return this
}

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