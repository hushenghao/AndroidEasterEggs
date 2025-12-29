package com.dede.android_eggs.views.main.util

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Matrix
import android.graphics.Rect
import android.view.animation.LinearInterpolator
import com.dede.android_eggs.util.OrientationAngleSensor
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max

class EasterEggLogoSensorMatrixConvert @Inject constructor() :
    OrientationAngleSensor.OnOrientationAnglesUpdate {

        companion object {
            private const val DEGREES_THRESHOLD = 3f
            private const val OFFSET_RATIO = 0.18f
        }

    private val list = ArrayList<Listener>()

    fun register(listener: Listener) {
        list.add(listener)
    }

    fun unregister(listener: Listener) {
        list.remove(listener)
    }

    abstract class Listener(bounds: Rect) {

        private val width = bounds.width() * OFFSET_RATIO
        private val height = bounds.height() * OFFSET_RATIO

        private val matrix = Matrix()

        fun updateDegrees(cXDegrees: Float, cYDegrees: Float) {
            // Swap coordinate systems
            val dx = cYDegrees / 90f * width * -1f
            val dy = cXDegrees / 90f * height
            matrix.setTranslate(dx, dy)
            onUpdateMatrix(matrix)
        }

        abstract fun onUpdateMatrix(matrix: Matrix)
    }

    private var lastXDegrees: Float = 0f
    private var lastYDegrees: Float = 0f
    private var animator: Animator? = null
    private val interpolator = LinearInterpolator()

    private fun Float.toRoundDegrees(): Float {
        return ((Math.toDegrees(toDouble())) % 90f).toFloat()
    }

    private fun calculateAnimDegrees(old: Float, new: Float, fraction: Float): Float {
        return old + (new - old) * fraction
    }

    override fun updateOrientationAngles(zAngle: Float, xAngle: Float, yAngle: Float) {
        val xDegrees = xAngle.toRoundDegrees()// 俯仰角
        val yDegrees = yAngle.toRoundDegrees()// 侧倾角
        if (max(abs(lastXDegrees - xDegrees), abs(lastYDegrees - yDegrees)) < DEGREES_THRESHOLD) return

        animator?.cancel()
        val saveXDegrees = lastXDegrees
        val saveYDegrees = lastYDegrees
        animator = ValueAnimator.ofFloat(0f, 1f)
            .setDuration(80)
            .apply {
                interpolator = this@EasterEggLogoSensorMatrixConvert.interpolator
                addUpdateListener {
                    val fraction = it.animatedFraction
                    val cXDegrees = calculateAnimDegrees(saveXDegrees, xDegrees, fraction)
                    val cYDegrees = calculateAnimDegrees(saveYDegrees, yDegrees, fraction)

                    for (listener in list) {
                        listener.updateDegrees(cXDegrees, cYDegrees)
                    }
                }
                start()
            }
        lastYDegrees = yDegrees
        lastXDegrees = xDegrees
    }
}