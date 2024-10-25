@file:JvmName("DimenUtils")
@file:JvmMultifileClass

package com.dede.basic

import android.util.TypedValue
import kotlin.math.roundToInt


val Number.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        globalContext.resources.displayMetrics
    ).roundToInt()

val Number.dpf: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        globalContext.resources.displayMetrics
    )
