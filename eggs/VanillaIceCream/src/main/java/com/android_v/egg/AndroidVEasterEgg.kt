package com.android_v.egg

import android.content.ComponentName
import android.content.Context
import android.os.Build
import com.android_v.egg.landroid.DreamUniverse
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.ComponentProvider
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggProvider
import com.dede.basic.provider.TimelineEvent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import java.util.Calendar
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AndroidVEasterEgg : EasterEggProvider, ComponentProvider {

    @Provides
    @IntoSet
    @Singleton
    override fun provideEasterEgg(): BaseEasterEgg {
        return object : EasterEgg(
            iconRes = R.drawable.v_android15_patch_adaptive,
            nameRes = R.string.v_egg_name,
            nicknameRes = R.string.v_android_nickname,
            apiLevel = Build.VERSION_CODES.VANILLA_ICE_CREAM,
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
            TimelineEvent(
                2024, Calendar.SEPTEMBER,
                Build.VERSION_CODES.VANILLA_ICE_CREAM,
                "Vanilla Ice Cream."
            )
        )
    }

    @Provides
    @IntoSet
    @Singleton
    override fun provideComponent(): ComponentProvider.Component {
        return object : ComponentProvider.Component(
            R.drawable.v_android15_patch_adaptive,
            R.string.v_egg_name,
            R.string.v_android_nickname,
            Build.VERSION_CODES.VANILLA_ICE_CREAM
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
