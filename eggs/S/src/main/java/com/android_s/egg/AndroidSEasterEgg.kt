package com.android_s.egg

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import com.android_s.egg.neko.NekoControlsService
import com.android_s.egg.widget.PaintChipsActivity
import com.android_s.egg.widget.PaintChipsWidget
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
import java.util.Date
import java.util.TimeZone
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AndroidSEasterEgg : EasterEggProvider, ComponentProvider {

    @Provides
    @IntoSet
    @Singleton
    override fun provideEasterEgg(): BaseEasterEgg {
        return object : EasterEgg(
            iconRes = R.drawable.s_android_logo,
            nameRes = R.string.s_egg_name,
            nicknameRes = R.string.s_android_nickname,
            apiLevel = Build.VERSION_CODES.S..Build.VERSION_CODES.S_V2
        ) {
            override fun provideEasterEgg(): Class<out Activity> {
                return PlatLogoActivity::class.java
            }

            override fun provideSnapshotProvider(): SnapshotProvider {
                return SnapshotProvider()
            }

            override fun getReleaseDate(): Date {
                val calendar = Calendar.getInstance(TimeZone.getDefault())
                calendar.set(Calendar.YEAR, 2021)
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
                2021, Calendar.DECEMBER,
                Build.VERSION_CODES.S_V2,
                "S V2.\nOnce more unto the breach, dear friends, once more."
            ),
            TimelineEvent(
                2021, Calendar.SEPTEMBER,
                Build.VERSION_CODES.S,
                "S."
            )
        )
    }

    @Provides
    @IntoSet
    @Singleton
    override fun provideComponent(): ComponentProvider.Component {
        return object : ComponentProvider.Component(
            iconRes = R.drawable.s_ic_fullcat_icon,
            nameRes = R.string.s_egg_name,
            nicknameRes = R.string.s_android_nickname,
            apiLevel = Build.VERSION_CODES.S..Build.VERSION_CODES.S_V2
        ) {
            @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
            override fun isSupported(): Boolean {
                return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
            }

            @RequiresApi(Build.VERSION_CODES.R)
            override fun isEnabled(context: Context): Boolean {
                val cn = ComponentName(context, NekoControlsService::class.java)
                return cn.isEnabled(context)
            }

            @RequiresApi(Build.VERSION_CODES.R)
            override fun setEnabled(context: Context, enable: Boolean) {
                val cns = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                    arrayOf(
                        ComponentName(context, NekoControlsService::class.java),
                        ComponentName(context, PaintChipsActivity::class.java),
                        ComponentName(context, PaintChipsWidget::class.java)
                    )
                } else {
                    arrayOf(
                        ComponentName(context, NekoControlsService::class.java)
                    )
                }
                for (cn in cns) {
                    cn.setEnable(context, enable)
                }
            }
        }
    }
}