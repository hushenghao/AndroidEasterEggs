package com.dede.android_eggs.ui.views

import android.view.animation.Interpolator
import kotlin.math.exp

// android.widget.Scroller.ViscousFluidInterpolator
class ViscousFluidInterpolator : Interpolator {

    override fun getInterpolation(input: Float): Float {
        val interpolated = VISCOUS_FLUID_NORMALIZE * viscousFluid(input)
        return if (interpolated > 0) {
            interpolated + VISCOUS_FLUID_OFFSET
        } else interpolated
    }

    companion object {

        fun getInstance(): ViscousFluidInterpolator {
            return ViscousFluidInterpolator()
        }

        /**
         * Controls the viscous fluid effect (how much of it).
         */
        private const val VISCOUS_FLUID_SCALE = 8.0f

        // must be set to 1.0 (used in viscousFluid())
        private val VISCOUS_FLUID_NORMALIZE = 1.0f / viscousFluid(1.0f)

        // account for very small floating-point error
        private var VISCOUS_FLUID_OFFSET = 1.0f - VISCOUS_FLUID_NORMALIZE * viscousFluid(1.0f)

        private fun viscousFluid(x: Float): Float {
            var x = x
            x *= VISCOUS_FLUID_SCALE
            if (x < 1.0f) {
                x -= 1.0f - exp(-x.toDouble()).toFloat()
            } else {
                val start = 0.36787945f // 1/e == exp(-1)
                x = 1.0f - exp((1.0f - x).toDouble()).toFloat()
                x = start + x * (1.0f - start)
            }
            return x
        }
    }
}