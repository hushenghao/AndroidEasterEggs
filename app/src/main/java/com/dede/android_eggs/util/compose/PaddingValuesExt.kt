@file:Suppress("NOTHING_TO_INLINE")

package com.dede.android_eggs.util.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import javax.annotation.concurrent.Immutable

inline operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    return OperatorPaddingValues(this, other, OperatorPaddingValues.Plus)
}

inline operator fun PaddingValues.minus(other: PaddingValues): PaddingValues {
    return OperatorPaddingValues(this, other, OperatorPaddingValues.Minus)
}

private typealias Operator = Dp.(other: Dp) -> Dp

@Immutable
class OperatorPaddingValues(
    private val that: PaddingValues,
    private val other: PaddingValues,
    private val operator: Operator,
) : PaddingValues {

    companion object {
        val Plus: Operator = Dp::plus
        val Minus: Operator = Dp::minus
    }

    override fun calculateBottomPadding(): Dp =
        operator(that.calculateBottomPadding(), other.calculateBottomPadding())


    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        operator(
            that.calculateLeftPadding(layoutDirection),
            other.calculateLeftPadding(layoutDirection)
        )

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        operator(
            that.calculateRightPadding(layoutDirection),
            other.calculateRightPadding(layoutDirection)
        )

    override fun calculateTopPadding(): Dp =
        operator(that.calculateTopPadding(), other.calculateTopPadding())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OperatorPaddingValues) return false

        if (that != other.that) return false
        if (other != other.other) return false
        if (operator != other.operator) return false

        return true
    }

    override fun hashCode(): Int {
        var result = that.hashCode()
        result = 31 * result + other.hashCode()
        result = 31 * result + operator.hashCode()
        return result
    }

    override fun toString(): String {
        return "OperatorPaddingValues(that=$that, other=$other, operator=$operator)"
    }

}
