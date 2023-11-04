package com.android_s.egg

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
class AndroidSEasterEgg : EasterEggProvider {

    @Provides
    @IntoMap
    @IntKey(Build.VERSION_CODES.S)
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
        }
    }
}