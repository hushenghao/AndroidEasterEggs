package com.android_u.egg

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
object AndroidUEasterEgg : EasterEggProvider {

    @Provides
    @IntoSet
    @Singleton
    override fun provideEasterEgg(): BaseEasterEgg {
        return object : EasterEgg(
            iconRes = R.drawable.u_android14_patch_adaptive,
            nameRes = R.string.u_egg_name,
            nicknameRes = R.string.u_android_nickname,
            apiLevel = Build.VERSION_CODES.UPSIDE_DOWN_CAKE
        ) {
            override fun provideEasterEgg(): Class<out Activity> {
                return PlatLogoActivity::class.java
            }

            override fun provideSnapshotProvider(): SnapshotProvider {
                return SnapshotProvider()
            }

            override fun getReleaseDate(): Date {
                val calendar = Calendar.getInstance(TimeZone.getDefault())
                calendar.set(Calendar.YEAR, 2023)
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
                "2023", "September",
                Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
                "Upside Down Cake."
            )
        )
    }
}