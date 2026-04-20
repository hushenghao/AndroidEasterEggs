package com.android_cinnamon_bun.egg

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.dede.basic.provider.BaseEasterEgg
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

internal class SP : SnapshotProvider() {
    override val includeBackground: Boolean
        get() = true

    override fun create(context: Context): View {
        val dp = context.resources.displayMetrics.density
        val random = Random()
        val starfield = PlatLogoActivity.Starfield(random, dp * 2.0f)
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
object AndroidCinnamonBunEasterEgg : EasterEggProvider {
    @Provides
    @IntoSet
    @Singleton
    override fun provideEasterEgg(): BaseEasterEgg {
        return object : EasterEgg(
            iconRes = R.drawable.cinnamon_bun_android17_patch_adaptive,
            nameRes = R.string.cinnamon_bun_egg_name,
            nicknameRes = R.string.cinnamon_bun_egg_name,
            apiLevel = EasterEgg.VERSION_CODES.CINNAMON_BUN,
            actionClass = PlatLogoActivity::class.java,
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
                apiLevel = EasterEgg.VERSION_CODES.CINNAMON_BUN,
                event = "Hello, Android CinnamonBun.\nAndroid 17",
            )
        )
    }
}