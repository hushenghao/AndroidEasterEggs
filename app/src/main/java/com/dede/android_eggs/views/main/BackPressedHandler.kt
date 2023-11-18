package com.dede.android_eggs.views.main

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.window.BackEvent
import androidx.activity.BackEventCompat
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.withStyledAttributes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.drawables.ScaleType
import com.dede.android_eggs.ui.drawables.ScaleTypeDrawable
import com.dede.android_eggs.util.applyIf
import com.dede.android_eggs.util.resolveColor
import com.dede.basic.dpf
import com.dede.blurhash_android.BlurHashDrawable
import com.google.android.material.resources.MaterialAttributes
import kotlin.math.abs
import kotlin.math.max
import com.google.android.material.R as M3R

class BackPressedHandler(private val host: ComponentActivity) :
    OnBackPressedCallback(enabled = true), DefaultLifecycleObserver {

    companion object {
        private const val ANIMATION_DELAY = 200L
        private const val ANIMATION_DURATION = 200L
    }

    private val displayMetrics = Resources.getSystem().displayMetrics

    // https://developer.android.google.cn/design/ui/mobile/guides/patterns/predictive-back?hl=zh-cn
    private val maxXShift: Float = displayMetrics.widthPixels / 20 - 8 * displayMetrics.density
    private var maskableShapeSize: Float = 0f
    private var scaledTouchSlop: Int = 0

    private val androidContent: ViewGroup by lazy { host.findViewById(android.R.id.content) }
    private val contentView: View by lazy { androidContent.getChildAt(0) }

    private val backPressedDrawable: Drawable by lazy {
        val blurHashDrawable = BlurHashDrawable(
            host, R.string.hash_back_pressed_bg,
            (displayMetrics.widthPixels * 0.1f).toInt(),
            (displayMetrics.heightPixels * 0.1f).toInt()
        )
        ScaleTypeDrawable(blurHashDrawable, ScaleType.CENTER_CROP)
    }

    private var isStopped = false
    private var touchX = 0f
    private var touchY = 0f
    private var isProgressed = false

    @SuppressLint("RestrictedApi", "UnsafeOptInUsageError")
    fun register() {
        host.onBackPressedDispatcher.addCallback(host, this)
        host.lifecycle.addObserver(this)

        scaledTouchSlop = ViewConfiguration.get(host).scaledTouchSlop

        val value = MaterialAttributes.resolveTypedValueOrThrow(
            host, M3R.attr.shapeAppearanceCornerExtraLarge,
            "undefined shapeAppearanceCornerExtraLarge"
        )
        val attrs = intArrayOf(M3R.attr.cornerSize)
        host.withStyledAttributes(value.resourceId, attrs) {
            maskableShapeSize = getDimension(0, 32.dpf)
        }
        androidContent.background = null
        contentView.background = ColorDrawable(host.resolveColor(M3R.attr.colorSurface))
    }

    override fun onStart(owner: LifecycleOwner) {
        if (!isStopped) return
        // onRestart
        restoreContentState(true)
    }

    override fun onStop(owner: LifecycleOwner) {
        isStopped = true
    }


    private fun restoreContentState(delay: Boolean) {
        contentView.animate()
            .scaleX(1f)
            .scaleY(1f)
            .translationX(0f)
            .translationY(0f)
            .setDuration(ANIMATION_DURATION)
            .applyIf(delay) {
                startDelay = ANIMATION_DELAY
            }
            .start()
        with(ObjectAnimator.ofFloat(maskableShapeSize, 0f)) {
            addUpdateListener {
                setCornerRadius(it.animatedValue as Float)
            }
            doOnEnd {
                androidContent.background = null
            }
            applyIf(delay) {
                startDelay = ANIMATION_DELAY
            }
            duration = ANIMATION_DURATION
            start()
        }
        isProgressed = false
        isStopped = false
    }


    override fun handleOnBackStarted(backEvent: BackEventCompat) {
        touchX = backEvent.touchX
        touchY = backEvent.touchY
    }

    override fun handleOnBackProgressed(backEvent: BackEventCompat) {
        if (!isProgressed && max(
                abs(touchX - backEvent.touchX),
                abs(touchY - backEvent.touchY)
            ) >= scaledTouchSlop
        ) {
            with(ObjectAnimator.ofFloat(0f, maskableShapeSize)) {
                addUpdateListener {
                    setCornerRadius(it.animatedValue as Float)
                }
                doOnStart {
                    androidContent.background = backPressedDrawable
                }
                duration = ANIMATION_DURATION
                start()
            }
            isProgressed = true
        }

        when (backEvent.swipeEdge) {
            BackEvent.EDGE_LEFT ->
                contentView.translationX = backEvent.progress * maxXShift

            BackEvent.EDGE_RIGHT ->
                contentView.translationX = -(backEvent.progress * maxXShift)
        }
        contentView.scaleX = 1F - (0.1F * backEvent.progress)
        contentView.scaleY = 1F - (0.1F * backEvent.progress)
    }

    override fun handleOnBackCancelled() {
        restoreContentState(false)
    }

    override fun handleOnBackPressed() {
        isEnabled = false
        @Suppress("DEPRECATION")
        host.onBackPressed()
        isEnabled = true
    }

    private lateinit var outlineProvider: RoundOutline

    private class RoundOutline(var radius: Float) : ViewOutlineProvider() {

        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, radius)
        }
    }

    private fun setCornerRadius(cornerSizes: Float) {
        if (!::outlineProvider.isInitialized) {
            outlineProvider = RoundOutline(cornerSizes)
            contentView.outlineProvider = outlineProvider
            contentView.clipToOutline = true
        }
        outlineProvider.radius = cornerSizes
        contentView.invalidateOutline()
    }

}