package com.dede.android_eggs.views.main

import android.os.Build
import com.dede.android_eggs.R
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggGroup
import com.dede.basic.provider.EasterEggProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntKey
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
class AndroidBaseEasterEgg : EasterEggProvider {

    @Provides
    @IntoMap
    @IntKey(Build.VERSION_CODES.BASE)
    override fun provideEasterEgg(): BaseEasterEgg {
        return EasterEggGroup(
            0,
            EasterEgg(
                iconRes = R.drawable.ic_android_froyo,
                nameRes = R.string.nickname_android_froyo,
                nicknameRes = R.string.nickname_android_froyo,
                apiLevel = Build.VERSION_CODES.FROYO,
                supportAdaptiveIcon = false
            ),
            EasterEgg(
                iconRes = R.drawable.ic_android_eclair,
                nameRes = R.string.nickname_android_eclair,
                nicknameRes = R.string.nickname_android_eclair,
                apiLevel = Build.VERSION_CODES.ECLAIR..Build.VERSION_CODES.ECLAIR_MR1,
                supportAdaptiveIcon = false
            ),
            EasterEgg(
                iconRes = R.drawable.ic_android_donut,
                nameRes = R.string.nickname_android_donut,
                nicknameRes = R.string.nickname_android_donut,
                apiLevel = Build.VERSION_CODES.DONUT,
                supportAdaptiveIcon = false
            ),
            EasterEgg(
                iconRes = R.drawable.ic_android_cupcake,
                nameRes = R.string.nickname_android_cupcake,
                nicknameRes = R.string.nickname_android_cupcake,
                apiLevel = Build.VERSION_CODES.CUPCAKE,
                supportAdaptiveIcon = false
            ),
            EasterEgg(
                iconRes = R.drawable.ic_android_classic,
                nameRes = R.string.nickname_android_petit_four,
                nicknameRes = R.string.nickname_android_petit_four,
                apiLevel = Build.VERSION_CODES.BASE_1_1,
                supportAdaptiveIcon = false
            ),
            EasterEgg(
                iconRes = R.drawable.ic_android_classic,
                nameRes = R.string.nickname_android_base,
                nicknameRes = R.string.nickname_android_base,
                apiLevel = Build.VERSION_CODES.BASE,
                supportAdaptiveIcon = false
            ),
        )
    }
}