package com.android_b.egg

import android.os.Build
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEggGroup
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
object AndroidBaseEasterEgg : EasterEggProvider {

    @Provides
    @IntoSet
    @Singleton
    override fun provideEasterEgg(): BaseEasterEgg {
        return EasterEggGroup(
            BaseEasterEgg(
                iconRes = R.drawable.b_android_froyo,
                nameRes = R.string.b_nickname_android_froyo,
                nicknameRes = R.string.b_nickname_android_froyo,
                fullApiLevel = Build.VERSION_CODES_FULL.FROYO,
            ),
            BaseEasterEgg(
                iconRes = R.drawable.b_android_eclair,
                nameRes = R.string.b_nickname_android_eclair,
                nicknameRes = R.string.b_nickname_android_eclair,
                fullApiLevelRange = Build.VERSION_CODES_FULL.ECLAIR..Build.VERSION_CODES_FULL.ECLAIR_MR1,
            ),
            BaseEasterEgg(
                iconRes = R.drawable.b_android_donut,
                nameRes = R.string.b_nickname_android_donut,
                nicknameRes = R.string.b_nickname_android_donut,
                fullApiLevel = Build.VERSION_CODES_FULL.DONUT,
            ),
            BaseEasterEgg(
                iconRes = R.drawable.b_android_cupcake,
                nameRes = R.string.b_nickname_android_cupcake,
                nicknameRes = R.string.b_nickname_android_cupcake,
                fullApiLevel = Build.VERSION_CODES_FULL.CUPCAKE,
            ),
            BaseEasterEgg(
                iconRes = R.drawable.b_android_classic,
                nameRes = R.string.b_nickname_android_petit_four,
                nicknameRes = R.string.b_nickname_android_petit_four,
                fullApiLevel = Build.VERSION_CODES_FULL.BASE_1_1,
            ),
            BaseEasterEgg(
                iconRes = R.drawable.b_android_classic,
                nameRes = R.string.b_nickname_android_base,
                nicknameRes = R.string.b_nickname_android_base,
                fullApiLevel = Build.VERSION_CODES_FULL.BASE,
            ),
        )
    }

    @Provides
    @IntoSet
    @Singleton
    override fun provideTimelineEvents(): List<TimelineEvent> {
        return listOf(
            timelineEvent(
                Build.VERSION_CODES_FULL.FROYO,
                "F.\nReleased publicly as Android 2.2 in May 2010."
            ),
            timelineEvent(
                Build.VERSION_CODES_FULL.ECLAIR_MR1,
                "E MR1.\nReleased publicly as Android 2.1 in January 2010."
            ),
            timelineEvent(
                Build.VERSION_CODES_FULL.ECLAIR_0_1,
                "E incremental update.\nReleased publicly as Android 2.0.1 in December 2009."
            ),
            timelineEvent(
                Build.VERSION_CODES_FULL.ECLAIR,
                "E.\nReleased publicly as Android 2.0 in October 2009."
            ),
            timelineEvent(
                Build.VERSION_CODES_FULL.DONUT,
                "D.\nReleased publicly as Android 1.6 in September 2009."
            ),
            timelineEvent(
                Build.VERSION_CODES_FULL.CUPCAKE,
                "C.\nReleased publicly as Android 1.5 in April 2009."
            ),
            timelineEvent(
                Build.VERSION_CODES_FULL.BASE_1_1,
                "First Android update.\nReleased publicly as Android 1.1 in February 2009."
            ),
            timelineEvent(
                Build.VERSION_CODES_FULL.BASE,
                "The original, first, version of Android. Yay!\nReleased publicly as Android 1.0 in September 2008."
            ).apply {
                androidLogo = R.drawable.b_android_logo_2007_2014
            }
        )
    }
}
