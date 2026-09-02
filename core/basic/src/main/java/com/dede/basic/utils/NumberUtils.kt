package com.dede.basic.utils

import kotlin.math.max
import kotlin.math.min

object NumberUtils {

    @JvmStatic
    fun rangeOf(value: Float, min: Float, max: Float): Float {
        if (value.isNaN()) {
            return min
        }
        if (value.isInfinite()) {
            return max
        }
        return max(min, min(value, max))
    }
}
