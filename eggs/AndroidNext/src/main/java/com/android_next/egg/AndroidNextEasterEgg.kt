package com.android_next.egg

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dede.android_eggs.navigation.EasterEggsDestination.AndroidNextTimelineDialog
import com.dede.android_eggs.navigation.Navigator
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEgg.VERSION_CODES_FULL.toFullApiLevel
import com.dede.basic.provider.EasterEggProvider
import com.dede.basic.provider.SnapshotProvider
import com.dede.basic.provider.TimelineEvent
import com.dede.basic.requireDrawable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import java.util.Calendar
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AndroidNextEasterEgg : EasterEggProvider {

    internal const val RELEASE_YEAR = 2026
    internal const val RELEASE_MONTH = Calendar.AUGUST

    private const val NEXT_API = Build.VERSION_CODES.CUR_DEVELOPMENT// android next

     private const val TIMELINE_EVENT = "Wow, Android Next."

    @StringRes
    private val NICKNAME_RES: Int = R.string.nickname_android_next

    @DrawableRes
    private val LOGO_RES: Int = R.drawable.ic_droid_logo

    @DrawableRes
    private val PLATLOGO_RES: Int = R.drawable.android_17_platlogo

    @Provides
    @IntoSet
    @Singleton
    override fun provideEasterEgg(): BaseEasterEgg {
        return object : EasterEgg(
            iconRes = LOGO_RES,
            nameRes = NICKNAME_RES,
            nicknameRes = NICKNAME_RES,
            fullApiLevel = NEXT_API.toFullApiLevel(),
        ) {
            override fun onEasterEggAction(context: Context): Boolean {
                Navigator.findNavigator()?.navigate(AndroidNextTimelineDialog)
                return true
            }

            override fun provideSnapshotProvider(): SnapshotProvider {
                return object : SnapshotProvider() {
                    override fun create(context: Context): View {
                        return ImageView(context).apply {
                            setImageDrawable(context.requireDrawable(PLATLOGO_RES))
                        }
                    }

                    override val includeBackground: Boolean = false
                }
            }

        }
    }

    @Provides
    @IntoSet
    @Singleton
    override fun provideTimelineEvents(): List<TimelineEvent> {
        return listOf(
            TimelineEvent(
                year = RELEASE_YEAR,
                month = RELEASE_MONTH,
                fullApiLevel = NEXT_API.toFullApiLevel(),
                event = TIMELINE_EVENT,
            )
        )
    }
}
