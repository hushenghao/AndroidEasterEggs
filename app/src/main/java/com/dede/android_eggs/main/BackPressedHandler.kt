package com.dede.android_eggs.main

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewConfiguration
import android.window.BackEvent
import androidx.activity.BackEventCompat
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherAccessor
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.withStyledAttributes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.drawables.ScaleType
import com.dede.android_eggs.ui.drawables.ScaleTypeDrawable
import com.dede.android_eggs.util.applyIf
import com.dede.basic.dpf
import com.dede.blurhash_android.BlurHashDrawable
import com.google.android.material.carousel.MaskableFrameLayout
import com.google.android.material.resources.MaterialAttributes
import com.google.android.material.shape.ShapeAppearanceModel
import kotlin.math.abs
import kotlin.math.max
import com.google.android.material.R as M3R

class BackPressedHandler(private val host: AppCompatActivity) :
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

    private val contentView: MaskableFrameLayout by lazy { host.findViewById(R.id.fl_mask) }
    private val androidContent: View by lazy { host.findViewById(android.R.id.content) }

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

    @SuppressLint("RestrictedApi")
    fun register() {
        OnBackPressedDispatcherAccessor.fixApi34(host)
        host.onBackPressedDispatcher.addCallback(this)
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
                contentView.shapeAppearanceModel =
                    createShapeAppearanceModel(it.animatedValue as Float)
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
                    contentView.shapeAppearanceModel =
                        createShapeAppearanceModel(it.animatedValue as Float)
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

    private fun createShapeAppearanceModel(cornerSizes: Float): ShapeAppearanceModel {
        return ShapeAppearanceModel.builder().setAllCornerSizes(cornerSizes).build()
    }

}