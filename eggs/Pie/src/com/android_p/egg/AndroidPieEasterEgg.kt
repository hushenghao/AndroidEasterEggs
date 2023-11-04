package com.android_p.egg

import android.app.Activity
import android.os.Build
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntKey
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
class AndroidPieEasterEgg : EasterEggProvider {

    @Provides
    @IntoMap
    @IntKey(Build.VERSION_CODES.P)
    override fun provideEasterEgg(): BaseEasterEgg {
        return object : EasterEgg(
            iconRes = R.drawable.p_icon,
            nameRes = R.string.p_app_name,
            nicknameRes = R.string.p_android_nickname,
            apiLevel = Build.VERSION_CODES.P
        ) {
            override fun provideEasterEgg(): Class<out Activity>? {
                return PlatLogoActivity::class.java
            }

            override fun provideSnapshotProvider(): SnapshotProvider {
                return SnapshotProvider()
            }
        }
    }
}