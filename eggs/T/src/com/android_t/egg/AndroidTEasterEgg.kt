package com.android_t.egg

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.android_t.egg.neko.NekoControlsService
import com.android_t.egg.widget.PaintChipsActivity
import com.android_t.egg.widget.PaintChipsWidget
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.ComponentProvider
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AndroidTEasterEgg : EasterEggProvider, ComponentProvider {

    @Provides
    @IntoSet
    @Singleton
    override fun provideEasterEgg(): BaseEasterEgg {
        return object : EasterEgg(
            iconRes = R.drawable.t_android_logo,
            nameRes = R.string.t_egg_name,
            nicknameRes = R.string.t_android_nickname,
            apiLevel = Build.VERSION_CODES.TIRAMISU
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
    override fun provideComponent(): ComponentProvider.Component {
        return object : ComponentProvider.Component(
            nameRes = R.string.t_egg_name,
            nicknameRes = R.string.t_android_nickname,
            apiLevel = Build.VERSION_CODES.TIRAMISU
        ) {
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