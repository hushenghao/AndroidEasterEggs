package com.dede.android_eggs.main.holders

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.StateListDrawable
import android.util.StateSet
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.view.GravityCompat
import coil.dispose
import coil.load
import com.dede.android_eggs.databinding.ItemEasterEggLayoutBinding
import com.dede.android_eggs.main.EggActionHelp
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.main.entity.Egg.Companion.getIcon
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.adapter.VHType
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.ui.views.HorizontalSwipeLayout
import com.dede.android_eggs.util.OrientationAngleSensor
import com.dede.android_eggs.util.isRtl
import com.dede.android_eggs.util.resolveColorStateList
import com.dede.android_eggs.util.updateCompoundDrawablesRelative
import com.dede.basic.dp
import com.dede.basic.dpf
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import com.google.android.material.R as M3R

@VHType(viewType = Egg.VIEW_TYPE_EGG)
open class EggHolder(view: View) : VHolder<Egg>(view),
    OrientationAngleSensor.OnOrientationAnglesUpdate {

    val binding: ItemEasterEggLayoutBinding = ItemEasterEggLayoutBinding.bind(view)

    private val matrix = Matrix()
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
        val iconDrawable = binding.ivIcon.drawable as? AlterableAdaptiveIconDrawable ?: return
        if (!iconDrawable.isAdaptiveIconDrawable) return

        val xDegrees = xAngle.toRoundDegrees()// 俯仰角
        val yDegrees = yAngle.toRoundDegrees()// 侧倾角
        if (max(abs(lastXDegrees - xDegrees), abs(lastYDegrees - yDegrees)) < 5f) return

        val bounds = iconDrawable.bounds
        val width = bounds.width() / 4f
        val height = bounds.height() / 4f

        animator?.cancel()
        val saveXDegrees = lastXDegrees
        val saveYDegrees = lastYDegrees
        animator = ValueAnimator.ofFloat(0f, 1f)
            .setDuration(100)
            .apply {
                interpolator = this@EggHolder.interpolator
                addUpdateListener {
                    val fraction = it.animatedFraction
                    val cXDegrees = calculateAnimDegrees(saveXDegrees, xDegrees, fraction)
                    val cYDegrees = calculateAnimDegrees(saveYDegrees, yDegrees, fraction)
                    val dx = cYDegrees / 90f * width * -1f
                    val dy = cXDegrees / 90f * height
                    matrix.setTranslate(dx, dy)
                    iconDrawable.setForegroundMatrix(matrix)
                }
                start()
            }
        lastYDegrees = yDegrees
        lastXDegrees = xDegrees
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun onBindViewHolder(egg: Egg) {
        binding.tvTitle.setText(egg.eggNameRes)
        binding.tvSummary.setText(egg.androidRes)
        binding.cardView.setOnClickListener { EggActionHelp.launchEgg(context, egg) }
        binding.background.tvBgMessage.text = egg.versionCommentFormatter.format(context)
        val enableShortcut = EggActionHelp.isShortcutEnable(egg)
        binding.background.tvAddShortcut.isEnabled = enableShortcut
        binding.root.swipeGravity =
            if (enableShortcut) Gravity.FILL_HORIZONTAL else GravityCompat.END

        binding.ivIcon.dispose()
        binding.background.ivBgIcon.dispose()
        if (egg.supportAdaptiveIcon) {
            binding.ivIcon.setImageDrawable(egg.getIcon(context))
            binding.background.ivBgIcon.setImageDrawable(egg.getIcon(context))
        } else {
            binding.ivIcon.load(egg.iconRes)
            binding.background.ivBgIcon.load(egg.iconRes)
        }

        val drawable = StateListDrawable().apply {
            val color = context.resolveColorStateList(
                M3R.attr.textAppearanceLabelMedium, android.R.attr.textColor
            )
            addState(
                intArrayOf(android.R.attr.state_selected),
                FontIconsDrawable(context, Icons.Rounded.app_shortcut, color)
            )
            val icon = if (isRtl) Icons.Rounded.swipe_right else Icons.Rounded.swipe_left
            addState(StateSet.NOTHING, FontIconsDrawable(context, icon, color))
            setBounds(0, 0, 30.dp, 30.dp)
        }
        binding.background.tvAddShortcut.updateCompoundDrawablesRelative(end = drawable)
        binding.root.swipeListener = SwipeAddShortcut(
            onSwipedPositionChanged = { v, _, p ->
                val symbol = if (v.isRtl) 1 else -1
                binding.background.tvAddShortcut.translationX = 12.dpf * symbol * p
            },
            onSwipedStartHalfFeedback = {
                binding.background.tvAddShortcut.isSelected = true
                it.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            },
            onSwipedRelease = {
                binding.background.tvAddShortcut.isSelected = false
            },
            callback = {
                EggActionHelp.addShortcut(context, egg)
            })
    }

    private class SwipeAddShortcut(
        private val onSwipedPositionChanged: (view: View, left: Int, p: Float) -> Unit,
        private val onSwipedStartHalfFeedback: (view: View) -> Unit,
        private val onSwipedRelease: () -> Unit,
        private val callback: () -> Unit,
    ) : HorizontalSwipeLayout.OnSwipeListener {

        private var isFeedback: Boolean = false
        private var postInvokeCallback: Boolean = false

        override fun onSwipeCaptured(capturedChild: View) {
            isFeedback = false
        }

        override fun onSwipePositionChanged(changedView: View, left: Int, dx: Int) {
            val halfWidth = changedView.width / 2
            val rtl = isRtl
            val isSwipedStartHalf = if (!rtl) {
                left <= -halfWidth
            } else {
                left >= halfWidth
            }
            val p = if (!rtl) {
                -left * 1f / halfWidth
            } else {
                left * 1f / halfWidth
            }
            onSwipedPositionChanged.invoke(changedView, left, max(-1f, min(p, 1f)))
            postInvokeCallback = isSwipedStartHalf
            if (!isFeedback && isSwipedStartHalf) {
                onSwipedStartHalfFeedback.invoke(changedView)
                isFeedback = true
            }
        }

        override fun onSwipeReleased(releasedChild: View) {
            if (postInvokeCallback) {
                callback.invoke()
                postInvokeCallback = false
            }
            onSwipedRelease.invoke()
        }
    }

}