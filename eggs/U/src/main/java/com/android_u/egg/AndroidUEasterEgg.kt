package com.android_u.egg

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
class AndroidUEasterEgg : EasterEggProvider {

    @Provides
    @IntoMap
    @IntKey(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
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
        }
    }
}