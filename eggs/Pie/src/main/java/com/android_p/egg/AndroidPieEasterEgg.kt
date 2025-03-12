package com.android_p.egg

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
object AndroidPieEasterEgg : EasterEggProvider {

    @Provides
    @IntoSet
    @Singleton
    override fun provideEasterEgg(): BaseEasterEgg {
        return object : EasterEgg(
            iconRes = R.drawable.p_icon,
            nameRes = R.string.p_app_name,
            nicknameRes = R.string.p_android_nickname,
            apiLevel = Build.VERSION_CODES.P,
            actionClass = PlatLogoActivity::class.java
        ) {
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
                Build.VERSION_CODES.P,
                "P.\nReleased publicly as Android 9 in August 2018."
            )
        )
    }
}