package com.dede.android_eggs.main.holders

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.LinearInterpolator
import coil.dispose
import coil.load
import com.dede.android_eggs.databinding.ItemEasterEggLayoutBinding
import com.dede.android_eggs.main.EggActionHelp
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.settings.IconShapePref
import com.dede.android_eggs.ui.Icons
import com.dede.android_eggs.ui.adapter.VHType
import com.dede.android_eggs.ui.adapter.VHolder
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.ui.views.HorizontalSwipeLayout
import com.dede.android_eggs.util.isRtl
import com.dede.android_eggs.util.resolveColorStateList
import com.dede.android_eggs.util.updateCompoundDrawablesRelative
import kotlin.math.abs
import kotlin.math.max
import com.google.android.material.R as M3R

@VHType(viewType = Egg.VIEW_TYPE_EGG)
open class EggHolder(view: View) : VHolder<Egg>(view) {

    val binding: ItemEasterEggLayoutBinding = ItemEasterEggLayoutBinding.bind(view)
    val context: Context = itemView.context

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

    fun updateOrientationAngles(xAngle: Float, yAngle: Float) {
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
        binding.background.tvAddShortcut.isEnabled = EggActionHelp.supportShortcut(context, egg)

        binding.ivIcon.dispose()
        binding.background.ivBgIcon.dispose()
        if (egg.supportAdaptiveIcon) {
            val pathStr = IconShapePref.getMaskPath(context)
            binding.ivIcon.setImageDrawable(
                AlterableAdaptiveIconDrawable(context, egg.iconRes, pathStr)
            )
            binding.background.ivBgIcon.setImageDrawable(
                AlterableAdaptiveIconDrawable(context, egg.iconRes, pathStr)
            )
        } else {
            binding.ivIcon.load(egg.iconRes)
            binding.background.ivBgIcon.load(egg.iconRes)
        }

        val color = context.resolveColorStateList(
            M3R.attr.textAppearanceLabelMedium, android.R.attr.textColor
        )
        val drawable = FontIconsDrawable(context, Icons.Rounded.swipe_left_alt, 24f).apply {
            setColorStateList(color)
            isAutoMirrored = true
        }
        binding.background.tvAddShortcut.updateCompoundDrawablesRelative(end = drawable)
        binding.root.swipeListener = SwipeAddShortcut({
            it.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        }) {
            EggActionHelp.addShortcut(context, egg)
        }
    }

    private class SwipeAddShortcut(
        private val onSwipedStartHalfFeedback: (view: View) -> Unit,
        private val callback: () -> Unit,
    ) : HorizontalSwipeLayout.OnSwipeListener {

        private var isFeedback: Boolean = false
        private var postInvokeCallback: Boolean = false

        override fun onSwipeCaptured(capturedChild: View) {
            isFeedback = false
        }

        override fun onSwipePositionChanged(changedView: View, left: Int, dx: Int) {
            val halfWidth = changedView.width / 2
            val isSwipedStartHalf = if (!isRtl) {
                left <= -halfWidth
            } else {
                left >= halfWidth
            }
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
        }
    }

}