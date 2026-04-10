package com.dede.android_eggs.views.main.util

import android.graphics.Matrix
import android.graphics.Rect
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FloatValueHolder
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.dede.android_eggs.util.OrientationAngleSensor
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class EasterEggLogoSensorMatrixConvert @Inject constructor() :
    OrientationAngleSensor.OnOrientationAnglesUpdate {

    companion object {
        private const val DEGREES_THRESHOLD = 0.6f
        private const val OFFSET_RATIO = 0.2f
        private const val MAX_EFFECTIVE_DEGREES = 45f
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

        internal fun updateDegrees(cXDegrees: Float, cYDegrees: Float) {
            // Swap coordinate systems
            val dx = cYDegrees / 90f * width * -1f
            val dy = cXDegrees / 90f * height
            matrix.setTranslate(dx, dy)
            onUpdateMatrix(matrix)
        }

        abstract fun onUpdateMatrix(matrix: Matrix)
    }

    private class SpringAngleAnimator(
        private val onUpdate: (xDegrees: Float, yDegrees: Float) -> Unit,
    ) : DynamicAnimation.OnAnimationUpdateListener {

        companion object {
            private const val SPRING_STIFFNESS = 420f
            private const val SPRING_DAMPING_RATIO = 0.85f
            private const val MIN_VISIBLE_CHANGE = 0.08f
        }

        private val xHolder = FloatValueHolder(0f)
        private val yHolder = FloatValueHolder(0f)
        private val xAnimation = createSpringAnimation(xHolder)
        private val yAnimation = createSpringAnimation(yHolder)

        private fun createSpringAnimation(holder: FloatValueHolder): SpringAnimation {
            return SpringAnimation(holder).apply {
                setSpring(
                    SpringForce().setStiffness(SPRING_STIFFNESS)
                        .setDampingRatio(SPRING_DAMPING_RATIO)
                )
                setMinimumVisibleChange(MIN_VISIBLE_CHANGE)
                addUpdateListener(this@SpringAngleAnimator)
            }
        }

        private var targetXDegrees: Float = 0f
        private var targetYDegrees: Float = 0f

        private fun angleDistance(old: Float, new: Float): Float {
            return abs(new - old)
        }

        fun snapTo(xDegrees: Float, yDegrees: Float) {
            targetXDegrees = xDegrees
            targetYDegrees = yDegrees
            xAnimation.cancel()
            yAnimation.cancel()
            xHolder.value = xDegrees
            yHolder.value = yDegrees
            onUpdate(xDegrees, yDegrees)
        }

        fun targetDistance(xDegrees: Float, yDegrees: Float): Float {
            return max(
                angleDistance(targetXDegrees, xDegrees),
                angleDistance(targetYDegrees, yDegrees)
            )
        }

        fun animateTo(xDegrees: Float, yDegrees: Float) {
            targetXDegrees = xDegrees
            targetYDegrees = yDegrees
            xAnimation.animateToFinalPosition(xDegrees)
            yAnimation.animateToFinalPosition(yDegrees)
        }

        override fun onAnimationUpdate(
            animation: DynamicAnimation<out DynamicAnimation<*>>,
            value: Float,
            velocity: Float
        ) {
            onUpdate(xHolder.value, yHolder.value)
        }
    }

    private var isInitialized = false
    private val springAnimator = SpringAngleAnimator(onUpdate = ::dispatchDegrees)

    private fun Float.toNormalizedDegrees(): Float {
        val degrees = Math.toDegrees(toDouble())
        return atan2(sin(Math.toRadians(degrees)), cos(Math.toRadians(degrees)))
            .let { Math.toDegrees(it).toFloat() }
    }

    private fun Float.toUiDegrees(): Float {
        return toNormalizedDegrees().coerceIn(-MAX_EFFECTIVE_DEGREES, MAX_EFFECTIVE_DEGREES)
    }

    private fun dispatchDegrees(xDegrees: Float, yDegrees: Float) {
        for (listener in list) {
            listener.updateDegrees(xDegrees, yDegrees)
        }
    }

    override fun updateOrientationAngles(zAngle: Float, xAngle: Float, yAngle: Float) {
        val xDegrees = xAngle.toUiDegrees()// 俯仰角
        val yDegrees = yAngle.toUiDegrees()// 侧倾角
        if (!isInitialized) {
            isInitialized = true
            springAnimator.snapTo(xDegrees, yDegrees)
            return
        }

        if (springAnimator.targetDistance(xDegrees, yDegrees) < DEGREES_THRESHOLD) {
            return
        }

        springAnimator.animateTo(xDegrees, yDegrees)
    }
}
