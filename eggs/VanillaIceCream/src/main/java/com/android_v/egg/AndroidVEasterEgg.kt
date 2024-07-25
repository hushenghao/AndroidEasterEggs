package com.android_v.egg

import android.app.Activity
import android.os.Build
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggProvider
import com.dede.basic.provider.TimelineEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AndroidVEasterEgg : EasterEggProvider {

    @Provides
    @IntoSet
    @Singleton
    override fun provideEasterEgg(): BaseEasterEgg {
        return object : EasterEgg(
            iconRes = R.drawable.v_android15_patch_adaptive,
            nameRes = R.string.v_egg_name,
            nicknameRes = R.string.v_android_nickname,
            apiLevel = Build.VERSION_CODES.VANILLA_ICE_CREAM
        ) {
            override fun provideEasterEgg(): Class<out Activity> {
                return PlatLogoActivity::class.java
            }

            override fun provideSnapshotProvider(): SnapshotProvider {
                return SnapshotProvider()
            }

            override fun getReleaseDate(): Date {
                val calendar = Calendar.getInstance(TimeZone.getDefault())
                calendar.set(Calendar.YEAR, 2024)
                calendar.set(Calendar.MONTH, Calendar.SEPTEMBER)
                return calendar.time
            }
        }
    }

    @Provides
    @IntoSet
    @Singleton
    override fun provideTimelineEvents(): List<TimelineEvent> {
        return listOf(
            TimelineEvent(
                2024, Calendar.SEPTEMBER,
                Build.VERSION_CODES.VANILLA_ICE_CREAM,
                "Vanilla Ice Cream."
            )
        )
    }
}