package com.android_next.egg

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.basic.requireDrawable
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.floor
import kotlin.math.min

@Module
@InstallIn(SingletonComponent::class)
object AndroidNextTimelineDialog : EasterEggsDestination, EasterEggsDestination.Provider {
    override val type: EasterEggsDestination.Type = EasterEggsDestination.Type.Dialog

    override val route: NavKey = EasterEggsDestination.AndroidNextTimelineDialog

    @Composable
    override fun Content(properties: EasterEggsDestination.DestinationProps) {
        AndroidNextTimelineDialog(onDismiss = properties.onBack)
    }

    @Provides
    @IntoSet
    override fun provider(): EasterEggsDestination = this
}

const val ACTION_SHOE_ANDROID_NEXT_DIALOG = "action_show_android_next_dialog"

@Composable
fun AndroidNextTimelineDialog(
    @DrawableRes logoRes: Int = R.drawable.ic_droid_logo,
    @StringRes titleRes: Int = R.string.nickname_android_next,
    onDismiss: () -> Unit = {},
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
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
                AndroidReleaseTimeline()
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                CustomTabsBrowser.launchUrl(context, R.string.url_android_releases)
            }) {
                Text(text = stringResource(id = R.string.label_timeline_releases))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
    )
}


@Composable
@Preview(showBackground = true)
private fun AndroidReleaseTimeline() {
    val nowDate = Calendar.getInstance().setDateZero()
    val releaseDate = remember { getReleaseDate() }

    val offsetXIndex = remember(nowDate, releaseDate) {
        val diffMonth = getDateDiffMonth(start = nowDate, end = releaseDate)
        if (diffMonth <= 0) {
            6 // Final release
        } else if (diffMonth > MONTH_CYCLE) {
            // No preview
            -1
        } else {
            // Preview
            MONTH_CYCLE - diffMonth - 1
        }
    }
    val isFinalRelease = offsetXIndex == (MONTH_CYCLE - 1)

    val scrollState = rememberScrollState()
    if (offsetXIndex != -1) {
        LaunchedEffect(offsetXIndex, isFinalRelease) {
            val value = if (isFinalRelease) {
                scrollState.maxValue
            } else {
                scrollState.maxValue / MONTH_CYCLE * offsetXIndex
            }
            launch {
                scrollState.animateScrollTo(value)
            }
        }
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        val context = LocalContext.current
        val configuration = LocalConfiguration.current

        val isNightMode = remember(configuration) { isSystemNightMode(configuration) }
        val timelineMonths = remember(configuration) { getReleaseCycleMonths(context) }
        if (timelineMonths.size != monthExtras.size) {
            throw IllegalArgumentException("Timeline months cycle != %d".format(MONTH_CYCLE))
        }
        val textMeasurer = rememberTextMeasurer(cacheSize = timelineMonths.size + labelExtras.size)
        Image(
            painter = painterResource(id = R.drawable.timeline_bg),
            contentDescription = null,
            modifier = Modifier
                .horizontalScroll(scrollState)
                .height(160.dp)
                .aspectRatio(789f / 180)
                .drawWithCache {
                    onDrawWithContent {
                        for (extra in labelExtras) {
                            val offsetX = size.width * extra.offsetXPercent
                            val rangeX = size.width * extra.rangeXPercent
                            val offsetY = size.height * extra.offsetYPercent
                            @SuppressLint("LocalContextGetResourceValueCall")
                            val textLayout = textMeasurer.measure(
                                text = context.getString(extra.labelRes),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif,
                                ),
                                constraints = Constraints(
                                    maxWidth = floor(rangeX - offsetX).toInt(),
                                )
                            )
                            drawText(
                                textLayoutResult = textLayout,
                                color = extra.color,
                                topLeft = Offset(x = offsetX, y = offsetY),
                            )
                        }

                        for ((index, month) in timelineMonths.withIndex()) {
                            val isLastMonth = index == timelineMonths.size - 1
                            val isSelected = index == offsetXIndex

                            val extra = monthExtras[index]
                            val colors = if (isNightMode) extra.nightColors else extra.colors

                            val textLayout = textMeasurer.measure(
                                text = month,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected || isLastMonth) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = FontFamily.SansSerif,
                                ),
                            )

                            val offsetX = size.width * extra.offsetXPercent
                            val offsetY = size.height * extra.offsetYPercent

                            if (isSelected) {
                                val rectSize = Size(
                                    width = textLayout.size.width + textLayout.size.height * 1.3f,
                                    height = textLayout.size.height * 1.6f
                                )
                                val radius = min(rectSize.height, rectSize.width) / 2f
                                drawRoundRect(
                                    color = colors.shapeColor,
                                    topLeft = Offset(
                                        x = offsetX - rectSize.width / 2f,
                                        y = offsetY - rectSize.height / 2f
                                    ),
                                    size = rectSize,
                                    cornerRadius = CornerRadius(radius, radius)
                                )
                            }

                            drawText(
                                textLayoutResult = textLayout,
                                color = if (isSelected) colors.selectedTextColor else colors.textColor,
                                topLeft = Offset(
                                    // text align center
                                    x = offsetX - textLayout.size.width / 2f,
                                    y = offsetY - textLayout.size.height / 2f,
                                ),
                            )
                        }

                        drawContent()
                    }
                }
        )
    }
}

private fun getTimelineMessage(context: Context): String {
    val nowDate = Calendar.getInstance().setDateZero()
    val releaseDate = getReleaseDate()
    return if (nowDate.after(releaseDate)) {
        context.getString(R.string.summary_android_release_pushed)
    } else {
        context.getString(R.string.summary_android_waiting)
    }
}

private fun isSystemNightMode(configuration: Configuration): Boolean {
    return (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}
