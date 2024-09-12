package com.android_b.egg

import android.os.Build
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
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
            EasterEgg(
                iconRes = R.drawable.b_android_froyo,
                nameRes = R.string.b_nickname_android_froyo,
                nicknameRes = R.string.b_nickname_android_froyo,
                apiLevel = Build.VERSION_CODES.FROYO,
            ),
            EasterEgg(
                iconRes = R.drawable.b_android_eclair,
                nameRes = R.string.b_nickname_android_eclair,
                nicknameRes = R.string.b_nickname_android_eclair,
                apiLevelRange = Build.VERSION_CODES.ECLAIR..Build.VERSION_CODES.ECLAIR_MR1,
            ),
            EasterEgg(
                iconRes = R.drawable.b_android_donut,
                nameRes = R.string.b_nickname_android_donut,
                nicknameRes = R.string.b_nickname_android_donut,
                apiLevel = Build.VERSION_CODES.DONUT,
            ),
            EasterEgg(
                iconRes = R.drawable.b_android_cupcake,
                nameRes = R.string.b_nickname_android_cupcake,
                nicknameRes = R.string.b_nickname_android_cupcake,
                apiLevel = Build.VERSION_CODES.CUPCAKE,
            ),
            EasterEgg(
                iconRes = R.drawable.b_android_classic,
                nameRes = R.string.b_nickname_android_petit_four,
                nicknameRes = R.string.b_nickname_android_petit_four,
                apiLevel = Build.VERSION_CODES.BASE_1_1,
            ),
            EasterEgg(
                iconRes = R.drawable.b_android_classic,
                nameRes = R.string.b_nickname_android_base,
                nicknameRes = R.string.b_nickname_android_base,
                apiLevel = Build.VERSION_CODES.BASE,
            ),
        )
    }

    @Provides
    @IntoSet
    @Singleton
    override fun provideTimelineEvents(): List<TimelineEvent> {
        return listOf(
            timelineEvent(
                Build.VERSION_CODES.FROYO,
                "F.\nReleased publicly as Android 2.2 in May 2010."
            ),
            timelineEvent(
                Build.VERSION_CODES.ECLAIR_MR1,
                "E MR1.\nReleased publicly as Android 2.1 in January 2010."
            ),
            timelineEvent(
                Build.VERSION_CODES.ECLAIR_0_1,
                "E incremental update.\nReleased publicly as Android 2.0.1 in December 2009."
            ),
            timelineEvent(
                Build.VERSION_CODES.ECLAIR,
                "E.\nReleased publicly as Android 2.0 in October 2009."
            ),
            timelineEvent(
                Build.VERSION_CODES.DONUT,
                "D.\nReleased publicly as Android 1.6 in September 2009."
            ),
            timelineEvent(
                Build.VERSION_CODES.CUPCAKE,
                "C.\nReleased publicly as Android 1.5 in April 2009."
            ),
            timelineEvent(
                Build.VERSION_CODES.BASE_1_1,
                "First Android update.\nReleased publicly as Android 1.1 in February 2009."
            ),
            timelineEvent(
                Build.VERSION_CODES.BASE,
                "The original, first, version of Android. Yay!\nReleased publicly as Android 1.0 in September 2008."
            )
        )
    }
}
