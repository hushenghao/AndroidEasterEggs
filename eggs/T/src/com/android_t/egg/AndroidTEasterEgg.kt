package com.android_t.egg

import android.app.Activity
import android.os.Build
import com.dede.basic.provider.BaseEasterEgg
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
class AndroidTEasterEgg : EasterEggProvider {

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
}