package com.android_b.egg

import com.dede.basic.provider.AndroidVersion

import com.dede.basic.provider.BaseEasterEgg
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
object AndroidBaseEasterEgg {

    @Provides
    @IntoSet
    @Singleton
    fun provideFroyoEgg(): BaseEasterEgg {
        return BaseEasterEgg(
            iconRes = R.drawable.b_android_froyo,
            nameRes = R.string.b_nickname_android_froyo,
            nicknameRes = R.string.b_nickname_android_froyo,
            version = AndroidVersion.FROYO,
            actionClass = PlatLogoActivity.Froyo::class.java,
        )
    }

    @Provides
    @IntoSet
    @Singleton
    fun provideEclairEgg(): BaseEasterEgg {
        return BaseEasterEgg(
            iconRes = R.drawable.b_android_eclair,
            nameRes = R.string.b_nickname_android_eclair,
            nicknameRes = R.string.b_nickname_android_eclair,
            version = AndroidVersion.ECLAIR, additionalVersions = listOf(AndroidVersion.ECLAIR_0_1, AndroidVersion.ECLAIR_MR1),
            actionClass = PlatLogoActivity.Eclair::class.java,
        )
    }

    @Provides
    @IntoSet
    @Singleton
    fun provideDonutEgg(): BaseEasterEgg {
        return BaseEasterEgg(
            iconRes = R.drawable.b_android_donut,
            nameRes = R.string.b_nickname_android_donut,
            nicknameRes = R.string.b_nickname_android_donut,
            version = AndroidVersion.DONUT,
            actionClass = PlatLogoActivity.Donut::class.java,
        )
    }

    @Provides
    @IntoSet
    @Singleton
    fun provideCupcakeEgg(): BaseEasterEgg {
        return BaseEasterEgg(
            iconRes = R.drawable.b_android_cupcake,
            nameRes = R.string.b_nickname_android_cupcake,
            nicknameRes = R.string.b_nickname_android_cupcake,
            version = AndroidVersion.CUPCAKE,
            actionClass = PlatLogoActivity.Cupcake::class.java,
        )
    }

    @Provides
    @IntoSet
    @Singleton
    fun providePetitFourEgg(): BaseEasterEgg {
        return BaseEasterEgg(
            iconRes = R.drawable.b_android_classic,
            nameRes = R.string.b_nickname_android_petit_four,
            nicknameRes = R.string.b_nickname_android_petit_four,
            version = AndroidVersion.BASE_1_1,
            actionClass = PlatLogoActivity.PetitFour::class.java,
        )
    }

    @Provides
    @IntoSet
    @Singleton
    fun provideBaseEgg(): BaseEasterEgg {
        return BaseEasterEgg(
            iconRes = R.drawable.b_android_classic,
            nameRes = R.string.b_nickname_android_base,
            nicknameRes = R.string.b_nickname_android_base,
            version = AndroidVersion.BASE,
            actionClass = PlatLogoActivity.Base::class.java,
        )
    }

    @Provides
    @IntoSet
    @Singleton
    fun provideTimelineEvents(): List<TimelineEvent> {
        return listOf(
            timelineEvent(
                AndroidVersion.FROYO,
                "F.\nReleased publicly as Android 2.2 in May 2010."
            ),
            timelineEvent(
                AndroidVersion.ECLAIR_MR1,
                "E MR1.\nReleased publicly as Android 2.1 in January 2010."
            ),
            timelineEvent(
                AndroidVersion.ECLAIR_0_1,
                "E incremental update.\nReleased publicly as Android 2.0.1 in December 2009."
            ),
            timelineEvent(
                AndroidVersion.ECLAIR,
                "E.\nReleased publicly as Android 2.0 in October 2009."
            ),
            timelineEvent(
                AndroidVersion.DONUT,
                "D.\nReleased publicly as Android 1.6 in September 2009."
            ),
            timelineEvent(
                AndroidVersion.CUPCAKE,
                "C.\nReleased publicly as Android 1.5 in April 2009."
            ),
            timelineEvent(
                AndroidVersion.BASE_1_1,
                "First Android update.\nReleased publicly as Android 1.1 in February 2009."
            ),
            timelineEvent(
                AndroidVersion.BASE,
                "The original, first, version of Android. Yay!\nReleased publicly as Android 1.0 in September 2008."
            ).apply {
                androidLogo = R.drawable.b_android_logo_2007_2014
            }
        )
    }
}
