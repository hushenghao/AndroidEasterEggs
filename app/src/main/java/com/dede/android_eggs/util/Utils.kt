@file:JvmName("Utils")

package com.dede.android_eggs.util

import android.os.Build
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