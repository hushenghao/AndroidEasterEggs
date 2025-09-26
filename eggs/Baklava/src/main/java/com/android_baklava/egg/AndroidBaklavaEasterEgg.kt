package com.android_baklava.egg

import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.android_baklava.egg.PlatLogoActivity.Starfield
import com.android_baklava.egg.landroid.DreamUniverse
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.ComponentProvider
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggProvider
import com.dede.basic.provider.SnapshotProvider
import com.dede.basic.provider.TimelineEvent
import com.dede.basic.requireDrawable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import java.util.Calendar
import java.util.Random
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AndroidBaklavaEasterEgg : EasterEggProvider, ComponentProvider {

    @Provides
    @IntoSet
    @Singleton
    override fun provideEasterEgg(): BaseEasterEgg {
        return object : EasterEgg(
            iconRes = R.drawable.baklava_android16_patch_adaptive,
            nameRes = R.string.baklava_egg_name,
            nicknameRes = R.string.baklava_android_nickname,
            apiLevel = Build.VERSION_CODES.BAKLAVA,
            actionClass = PlatLogoActivity::class.java
        ) {
            override fun provideSnapshotProvider(): SnapshotProvider {
                return SP()
            }
        }
    }

    @Provides
    @IntoSet
    @Singleton
    override fun provideTimelineEvents(): List<TimelineEvent> {
        return listOf(
            TimelineEvent(
                2025, Calendar.MAY,
                Build.VERSION_CODES.BAKLAVA,
                "Baklava."
            ),
            TimelineEvent(
                2025, Calendar.DECEMBER,
                Build.VERSION_CODES.BAKLAVA,
                "Baklava.\nAndroid 16.1.",
                // Build.VERSION_CODES_FULL.BAKLAVA_1
                Build.VERSION_CODES.BAKLAVA * TimelineEvent.SDK_INT_MULTIPLIER + 1
            )
        )
    }

    @Provides
    @IntoSet
    @Singleton
    override fun provideComponent(): ComponentProvider.Component {
        return object : ComponentProvider.Component(
            R.drawable.baklava_android16_patch_adaptive,
            R.string.baklava_egg_name,
            R.string.baklava_android_nickname,
            Build.VERSION_CODES.BAKLAVA
        ) {
            override fun isSupported(): Boolean = true

            override fun isEnabled(context: Context): Boolean {
                val cn = ComponentName(context, DreamUniverse::class.java)
                return cn.isEnabled(context)
            }

            override fun setEnabled(context: Context, enable: Boolean) {
                val cn = ComponentName(context, DreamUniverse::class.java)
                cn.setEnable(context, enable)
            }
        }
    }

    internal class SP : SnapshotProvider() {
        override val includeBackground: Boolean
            get() = true

        override fun create(context: Context): View {
            val dp = context.resources.displayMetrics.density
            val random = Random()
            val starfield = Starfield(random, dp * 2.0f)
            starfield.setVelocity(
                (random.nextFloat() - 0.5f) * 200.0f,
                (random.nextFloat() - 0.5f) * 200.0f
            )
            val layout = FrameLayout(context)
            layout.background = starfield

            val logo = ImageView(context)
            logo.setImageDrawable(context.requireDrawable(R.drawable.baklava_platlogo))
            val lp: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            lp.gravity = Gravity.CENTER
            layout.addView(logo, lp)

            return layout
        }
    }
}
