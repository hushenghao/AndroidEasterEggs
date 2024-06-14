package com.android_q.egg

import android.app.Activity
import android.os.Build
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggProvider
import com.dede.basic.provider.TimelineEvent
import com.dede.basic.provider.TimelineEvent.Companion.timelineEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AndroidQEasterEgg : EasterEggProvider {

    @Provides
    @IntoSet
    @Singleton
    override fun provideEasterEgg(): BaseEasterEgg {
        return object : EasterEgg(
            iconRes = R.drawable.q_icon,
            nameRes = R.string.q_egg_name,
            nicknameRes = R.string.q_android_nickname,
            apiLevel = Build.VERSION_CODES.Q
        ) {
            override fun provideEasterEgg(): Class<out Activity> {
                return PlatLogoActivity::class.java
            }

            override fun provideSnapshotProvider(): SnapshotProvider {
                return SnapshotProvider()
            }
        }
    }

    @Provides
    @IntoSet
    @Singleton
    override fun provideTimelineEvents(): List<TimelineEvent> {
        return listOf(
            timelineEvent(
                Build.VERSION_CODES.Q,
                "Q.\nReleased publicly as Android 10 in September 2019."
            )
        )
    }
}