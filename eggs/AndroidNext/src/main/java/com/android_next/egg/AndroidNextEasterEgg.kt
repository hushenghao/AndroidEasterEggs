package com.android_next.egg

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dede.android_eggs.util.LocalEvent
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggProvider
import com.dede.basic.provider.SnapshotProvider
import com.dede.basic.provider.TimelineEvent
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
    internal const val RELEASE_MONTH = Calendar.SEPTEMBER

    // private const val NEXT_API = Build.VERSION_CODES.CUR_DEVELOPMENT// android next
    private const val NEXT_API = 37// android 17

    // private const val TIMELINE_EVENT = "Wow, Android Next."
    private const val TIMELINE_EVENT = "Hello, Android CinnamonBun."

    @StringRes
    private val NICKNAME_RES: Int = R.string.nickname_android_next

    @DrawableRes
    private val LOGO_RES: Int = R.drawable.ic_droid_logo

    @DrawableRes
    private val PLATLOGO_RES: Int = R.drawable.img_droid_next

    @Provides
    @IntoSet
    @Singleton
    override fun provideEasterEgg(): BaseEasterEgg {
        return object : EasterEgg(
            iconRes = LOGO_RES,
            nameRes = NICKNAME_RES,
            nicknameRes = NICKNAME_RES,
            apiLevel = NEXT_API,
        ) {
            override fun onEasterEggAction(context: Context): Boolean {
                LocalEvent.poster().post(ACTION_SHOE_ANDROID_NEXT_DIALOG)
                return true
            }

            override fun provideSnapshotProvider(): SnapshotProvider {
                return object : SnapshotProvider() {
                    override fun create(context: Context): View {
                        return ImageView(context).apply {
                            setImageResource(PLATLOGO_RES)
                            scaleType = ImageView.ScaleType.CENTER_CROP
                        }
                    }

                    override val includeBackground: Boolean = true
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
                apiLevel = NEXT_API,
                event = TIMELINE_EVENT,
            )
        )
    }
}
