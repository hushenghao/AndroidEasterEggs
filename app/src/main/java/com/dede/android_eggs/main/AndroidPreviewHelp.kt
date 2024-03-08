@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.main

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ColorMatrixColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.ThemeUtils
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggProvider
import com.dede.basic.provider.SnapshotProvider
import com.dede.basic.requireDrawable
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

object AndroidPreviewHelp {

    @Module
    @InstallIn(SingletonComponent::class)
    class PreviewEasterEgg : EasterEggProvider {
        @Provides
        @IntoSet
        @Singleton
        override fun provideEasterEgg(): BaseEasterEgg {
            return object : EasterEgg(
                R.drawable.android_15_logo,
                R.string.nickname_android_vanilla_ice_cream,
                R.string.nickname_android_vanilla_ice_cream,
                35,
                true
            ) {
                override fun provideEasterEgg(): Class<out Activity>? {
                    return null
                }

                override fun easterEggAction(context: Context): Boolean {
                    dialogVisible = true
                    return true
                }

                override fun provideSnapshotProvider(): SnapshotProvider {
                    return object : SnapshotProvider() {
                        override fun create(context: Context): View {
                            return ImageView(context).apply {
                                setImageResource(R.drawable.android_15_platlogo)
                            }
                        }
                    }
                }

                override fun getReleaseDate(): Date {
                    val calendar = Calendar.getInstance(TimeZone.getDefault())
                    calendar.set(Calendar.YEAR, TIMELINE_YEAR)
                    calendar.set(Calendar.MONTH, Calendar.SEPTEMBER)
                    return calendar.time
                }
            }
        }

    }

    private const val TIMELINE_YEAR = 2024// android v
    const val API = 35// android v
    const val API_VERSION_NAME = "15"// android v

    private fun getTimelineMessage(context: Context): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        return if (year > TIMELINE_YEAR) {
            context.getString(R.string.summary_android_release_pushed)
        } else {
            context.getString(R.string.summary_android_waiting)
        }
    }

    private var dialogVisible by mutableStateOf(false)

    @Composable
    fun AndroidTimelineDialog(
        @DrawableRes logoRes: Int = R.drawable.android_15_platlogo,
        @StringRes titleRes: Int = R.string.nickname_android_vanilla_ice_cream
    ) {
        if (!dialogVisible) {
            return
        }
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = {
                dialogVisible = false
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = rememberDrawablePainter(context.requireDrawable(logoRes)),
                        contentDescription = stringResource(titleRes),
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = stringResource(id = titleRes),
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = getTimelineMessage(context),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(id = R.string.label_timeline_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    ReleaseTimeline()
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    dialogVisible = false
                    CustomTabsBrowser.launchUrl(context, R.string.url_android_releases)
                }) {
                    Text(text = stringResource(id = R.string.label_timeline_releases))
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogVisible = false }) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
            },
        )
    }

    @Composable
    private fun ReleaseTimeline() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)// [0, 11]
        // Month    Progress    Calender.MONTH
        // Feb          0           1
        // ...
        // Jul          5           6
        // Aug          -           7
        val offsetXArr = intArrayOf(20, 111, 202, 294, 386, 478, 584)
        val offsetXIndex =
            if (year < TIMELINE_YEAR || (year == TIMELINE_YEAR && month < Calendar.FEBRUARY)) {
                // No preview
                -1
            } else if (year == TIMELINE_YEAR && month in Calendar.FEBRUARY..Calendar.JULY) {
                // Preview
                month - 1
            } else {
                // Final release
                6
            }
        val isFinalRelease = offsetXIndex == 6
        val hasPreview = offsetXIndex != -1
        val offsetX = offsetXArr[min(offsetXArr.size - 1, max(offsetXIndex, 0))]

        val scrollState = rememberScrollState()
        if (hasPreview) {
            LaunchedEffect(offsetXIndex, isFinalRelease) {
                val value = if (isFinalRelease) {
                    scrollState.maxValue
                } else {
                    scrollState.maxValue / (offsetXArr.size) * (offsetXIndex)
                }
                launch {
                    scrollState.animateScrollTo(value)
                }
            }
        }
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Box(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .height(160.dp)
                    .aspectRatio(789f / 180)
            ) {
                if (hasPreview) {
                    Box(
                        modifier = Modifier
                            .padding(top = 103.dp, start = offsetX.dp)
                    ) {
                        val shape = RoundedCornerShape(
                            topStartPercent = 50, topEndPercent = 50,
                            bottomEndPercent = 50, bottomStartPercent = 50
                        )
                        if (isFinalRelease) {
                            // Final release
                            Box(
                                modifier = Modifier
                                    .width(106.dp)
                                    .height(34.dp)
                                    .background(Color(0xFF3DDC84), shape)
                            )
                        } else {
                            // Preview
                            Box(
                                modifier = Modifier
                                    .width(52.dp)
                                    .height(34.dp)
                                    .background(Color(0xFFF86734), shape)
                            )
                        }
                    }
                }
                val matrix = ColorMatrix()
                if (ThemeUtils.isSystemNightMode(LocalContext.current)) {
                    // Increase the overall brightness and more blue brightness
                    matrix.setToScale(1.3f, 1.5f, 2f, 1f)
                }
                Image(
                    painter = painterResource(id = R.drawable.timeline_bg),
                    contentDescription = null,
                    colorFilter = ColorMatrixColorFilter(matrix)
                )
            }
        }
    }
}