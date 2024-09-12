package com.dede.android_eggs.inject

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dede.android_eggs.R
import com.dede.android_eggs.views.main.compose.androidReleaseDialogVisible
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
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

//@Module
//@InstallIn(SingletonComponent::class)
object AndroidNextReleaseEasterEgg : EasterEggProvider {

    const val RELEASE_YEAR = 2024
    const val RELEASE_MONTH = Calendar.SEPTEMBER

    const val NEXT_API = 35// android v
    const val NEXT_API_VERSION_NAME = "15"// android v

    private const val TIMELINE_EVENT = "Vanilla Ice Cream."

    @StringRes
    val NICKNAME_RES = R.string.app_name

    @DrawableRes
    val LOGO_RES = R.mipmap.ic_launcher

    @DrawableRes
    val PLATLOGO_RES = R.mipmap.ic_launcher

    fun getTimelineMessage(context: Context): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        return if (year > RELEASE_YEAR) {
            context.getString(R.string.summary_android_release_pushed)
        } else {
            context.getString(R.string.summary_android_waiting)
        }
    }

//    @Provides
//    @IntoSet
//    @Singleton
    override fun provideEasterEgg(): BaseEasterEgg {
        return object : EasterEgg(
            iconRes = LOGO_RES,
            nameRes = NICKNAME_RES,
            nicknameRes = NICKNAME_RES,
            apiLevel = NEXT_API,
        ) {
            override fun onEasterEggAction(context: Context): Boolean {
                androidReleaseDialogVisible = true
                return true
            }

            override fun provideSnapshotProvider(): SnapshotProvider {
                return object : SnapshotProvider() {
                    override fun create(context: Context): View {
                        return ImageView(context).apply {
                            setImageDrawable(context.requireDrawable(PLATLOGO_RES))
                        }
                    }
                }
            }

        }
    }

//    @Provides
//    @IntoSet
//    @Singleton
    override fun provideTimelineEvents(): List<TimelineEvent> {
        return listOf(
            TimelineEvent(
                year = RELEASE_YEAR,
                month = RELEASE_MONTH,
                apiLevel = NEXT_API,
                event = TIMELINE_EVENT
            )
        )
    }
}
