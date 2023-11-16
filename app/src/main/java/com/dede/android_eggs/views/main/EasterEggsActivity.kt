package com.dede.android_eggs.views.main

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Matrix
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.IntSize
import com.dede.android_eggs.R
import com.dede.android_eggs.util.EdgeUtils
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.OrientationAngleSensor
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.views.main.compose.AndroidSnapshotView
import com.dede.android_eggs.views.main.compose.EasterEggItem
import com.dede.android_eggs.views.main.compose.LocalEasterEggLogoSensor
import com.dede.android_eggs.views.main.compose.LocalFragmentManager
import com.dede.android_eggs.views.main.compose.LocalHost
import com.dede.android_eggs.views.main.compose.MainTitleBar
import com.dede.android_eggs.views.main.compose.ProjectDescription
import com.dede.android_eggs.views.main.compose.Wavy
import com.dede.android_eggs.views.main.compose.Welcome
import com.dede.android_eggs.views.settings.prefs.IconVisualEffectsPref
import com.dede.android_eggs.views.theme.AppTheme
import com.dede.basic.provider.BaseEasterEgg
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max

@AndroidEntryPoint
class EasterEggsActivity : AppCompatActivity(), OrientationAngleSensor.OnOrientationAnglesUpdate {

    @Inject
    lateinit var easterEggs: List<@JvmSuppressWildcards BaseEasterEgg>

    @Inject
    @ActivityScoped
    lateinit var schemeHandler: SchemeHandler

    private var orientationAngleSensor: OrientationAngleSensor? = null
    private val sensor = Sensor()

    class Sensor {

        private val list = ArrayList<Update>()

        fun updateOrientationAngles(zAngle: Float, xAngle: Float, yAngle: Float) {
            for (update in list) {
                update.updateOrientationAngles(zAngle, xAngle, yAngle)
            }
        }

        fun register(update: Update) {
            list.add(update)
        }

        fun unregister(update: Update) {
            list.remove(update)
        }

        abstract class Update(private val size: IntSize) :
            OrientationAngleSensor.OnOrientationAnglesUpdate {

            abstract fun onUpdate(matrix: Matrix)

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

                val xDegrees = xAngle.toRoundDegrees()// 俯仰角
                val yDegrees = yAngle.toRoundDegrees()// 侧倾角
                if (max(abs(lastXDegrees - xDegrees), abs(lastYDegrees - yDegrees)) < 5f) return

                val width = size.width / 4f
                val height = size.height / 4f

                animator?.cancel()
                val saveXDegrees = lastXDegrees
                val saveYDegrees = lastYDegrees
                animator = ValueAnimator.ofFloat(0f, 1f)
                    .setDuration(100)
                    .apply {
                        interpolator = this@Update.interpolator
                        addUpdateListener {
                            val fraction = it.animatedFraction
                            val cXDegrees = calculateAnimDegrees(saveXDegrees, xDegrees, fraction)
                            val cYDegrees = calculateAnimDegrees(saveYDegrees, yDegrees, fraction)
                            val dx = cYDegrees / 90f * width * -1f
                            val dy = cXDegrees / 90f * height
                            matrix.setTranslate(dx, dy)
                            onUpdate(matrix)
                        }
                        start()
                    }
                lastYDegrees = yDegrees
                lastXDegrees = xDegrees
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.tryApplyOLEDTheme(this)
        EdgeUtils.applyEdge(window)
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(
                LocalFragmentManager provides supportFragmentManager,
                LocalHost provides this,
                LocalEasterEggLogoSensor provides sensor,
            ) {
                AppTheme {
                    Scaffold(
                        topBar = {
                            MainTitleBar()
                        }
                    ) { contentPadding ->
                        Welcome()

                        LazyColumn(contentPadding = contentPadding) {
                            item {
                                AndroidSnapshotView()
                                Wavy(res = R.drawable.ic_wavy_line)
                                for (easterEgg in easterEggs) {
                                    EasterEggItem(easterEgg)
                                }
                                Wavy(res = R.drawable.ic_wavy_line)
                                ProjectDescription()
                            }
                        }
                    }
                }
            }
        }

        BackPressedHandler(this).register()

        handleOrientationAngleSensor(IconVisualEffectsPref.isEnable(this))
        LocalEvent.receiver(this).register(IconVisualEffectsPref.ACTION_CHANGED) {
            val enable = it.getBooleanExtra(IconVisualEffectsPref.EXTRA_VALUE, false)
            handleOrientationAngleSensor(enable)
        }

        schemeHandler.handleIntent(intent)
    }


    private fun handleOrientationAngleSensor(enable: Boolean) {
        val orientationAngleSensor = this.orientationAngleSensor
        if (enable && orientationAngleSensor == null) {
            this.orientationAngleSensor = OrientationAngleSensor(
                this, this, this
            )
        } else if (!enable && orientationAngleSensor != null) {
            updateOrientationAngles(0f, 0f, 0f)
            orientationAngleSensor.destroy()
            this.orientationAngleSensor = null
        }
    }

    override fun updateOrientationAngles(zAngle: Float, xAngle: Float, yAngle: Float) {
        sensor.updateOrientationAngles(zAngle, xAngle, yAngle)
    }

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        schemeHandler.handleIntent(intent)
    }
}
