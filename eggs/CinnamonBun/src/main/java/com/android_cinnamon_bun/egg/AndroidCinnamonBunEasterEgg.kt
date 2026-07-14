package com.android_cinnamon_bun.egg

import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.android_cinnamon_bun.egg.PlatLogoActivity.Starfield
import com.android_cinnamon_bun.egg.landroid.DreamUniverse
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.ComponentProvider
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggProvider
import com.dede.basic.provider.SnapshotProvider
import com.dede.basic.provider.TimelineEvent
import com.dede.basic.provider.toRange
import com.dede.basic.requireDrawable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import java.util.Calendar
import java.util.Random
import javax.inject.Singleton

internal class SP : SnapshotProvider() {
    override val includeBackground: Boolean
        get() = true

    override fun create(context: Context): View {
        val dp = context.resources.displayMetrics.density
        val random = Random()
        val starfield = Starfield(random, dp * 2.0f)
        starfield.warp = 0.1f
        val layout = FrameLayout(context)
        layout.background = starfield

        val logo = ImageView(context)
        logo.setImageDrawable(context.requireDrawable(R.drawable.cinnamon_bun_platlogo))
        val lp: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        lp.gravity = Gravity.CENTER
        layout.addView(logo, lp)

        return layout
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AndroidCinnamonBunEasterEgg : EasterEggProvider, ComponentProvider {

    @Provides
    @IntoSet
    @Singleton
    override fun provideEasterEgg(): BaseEasterEgg {
        return object : EasterEgg(
            iconRes = R.drawable.cinnamon_bun_android17_patch_adaptive,
            nameRes = R.string.cinnamon_bun_egg_name,
            nicknameRes = R.string.cinnamon_bun_egg_name,
            apiLevelRange = Build.VERSION_CODES.CINNAMON_BUN.toRange(),
            actionClass = PlatLogoActivity::class.java,
            fullApiLevelRange = Build.VERSION_CODES_FULL.CINNAMON_BUN..Build.VERSION_CODES_FULL.CINNAMON_BUN_1
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
                year = 2026,
                month = Calendar.MAY,
                apiLevel = Build.VERSION_CODES.CINNAMON_BUN,
                event = "Cinnamon Bun."
            ),
            TimelineEvent(
                year = 2026,
                month = Calendar.AUGUST,
                apiLevel = Build.VERSION_CODES.CINNAMON_BUN,
                event = "Cinnamon Bun.\nAndroid 17.1",
                fullApiLevel = Build.VERSION_CODES_FULL.CINNAMON_BUN_1
            )
        )
    }

    @Provides
    @IntoSet
    @Singleton
    override fun provideComponent(): ComponentProvider.Component {
        return object : ComponentProvider.Component(
            R.drawable.cinnamon_bun_android17_patch_adaptive,
            R.string.cinnamon_bun_egg_name,
            R.string.cinnamon_bun_egg_name,
            Build.VERSION_CODES.CINNAMON_BUN
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
}
