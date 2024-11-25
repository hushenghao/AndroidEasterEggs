package com.android_next.egg

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.basic.requireDrawable
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.min

internal var androidNextDialogVisible by mutableStateOf(false)

@Composable
fun AndroidNextTimelineDialog(
    @DrawableRes logoRes: Int = R.drawable.ic_droid_logo,
    @StringRes titleRes: Int = R.string.nickname_android_next
) {
    if (!androidNextDialogVisible) {
        return
    }
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = {
            androidNextDialogVisible = false
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
                AndroidReleaseTimeline()
            }
        },
        confirmButton = {
            TextButton(onClick = {
                androidNextDialogVisible = false
                CustomTabsBrowser.launchUrl(context, R.string.url_android_releases)
            }) {
                Text(text = stringResource(id = R.string.label_timeline_releases))
            }
        },
        dismissButton = {
            TextButton(onClick = { androidNextDialogVisible = false }) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
    )
}

private val offsetXPercentArr = floatArrayOf(
    49 / 789f,
    153 / 789f,
    257 / 789f,
    361 / 789f,
    465 / 789f,
    569 / 789f,
    717 / 789f
)

private const val offsetYPercent = 135 / 180f

@Composable
private fun AndroidReleaseTimeline() {
    val nowDate = Calendar.getInstance().setDateZero()
    val releaseDate = remember { getReleaseCalendar() }

    val offsetXIndex = remember(nowDate, releaseDate) {
        val diffMonth = getDateDiffMonth(start = nowDate, end = releaseDate)
        if (diffMonth > MONTH_CYCLE) {
            // No preview
            -1
        } else if (diffMonth < MONTH_CYCLE) {
            // Preview
            MONTH_CYCLE - diffMonth - 1
        } else {
            // Final release
            6
        }
    }
    val isFinalRelease = offsetXIndex == 6

    val scrollState = rememberScrollState()
    if (offsetXIndex != -1) {
        LaunchedEffect(offsetXIndex, isFinalRelease) {
            val value = if (isFinalRelease) {
                scrollState.maxValue
            } else {
                scrollState.maxValue / offsetXPercentArr.size * offsetXIndex
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
            val context = LocalContext.current
            val matrix = ColorMatrix()
            if (isSystemNightMode(context)) {
                // Increase the overall brightness and more blue brightness
                matrix.setToScale(1.3f, 1.5f, 2f, 1f)
            }

            val timelineMonths = remember { getReleaseCycleMonths(context) }
            val textMeasurer = rememberTextMeasurer(cacheSize = timelineMonths.size)
            Image(
                painter = painterResource(id = R.drawable.timeline_bg),
                contentDescription = null,
                colorFilter = ColorFilter.colorMatrix(matrix),
                modifier = Modifier.drawWithCache {
                    onDrawWithContent {
                        for ((index, month) in timelineMonths.withIndex()) {
                            val isLastMonth = index == timelineMonths.size - 1

                            val textLayout = textMeasurer.measure(
                                text = month,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = if (isLastMonth) FontWeight.Bold else FontWeight.Medium
                                ),
                            )

                            val offsetX = size.width * offsetXPercentArr[index]
                            val offsetY = size.height * offsetYPercent

                            if (index == offsetXIndex) {
                                val rectSize = Size(
                                    width = textLayout.size.width + textLayout.size.height * 1.3f,
                                    height = textLayout.size.height * 1.6f
                                )
                                val radius = min(rectSize.height, rectSize.width) / 2f
                                drawRoundRect(
                                    color = if (isLastMonth)
                                        Color(0xFF3DDC84)
                                    else
                                        Color(0xFFF86734),
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
                                color = if (isLastMonth)
                                    Color(0xFF188038)
                                else
                                    Color.Black,
                                topLeft = Offset(
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
}

private fun getTimelineMessage(context: Context): String {
    val nowDate = Calendar.getInstance().setDateZero()
    val releaseDate = getReleaseCalendar()
    return if (nowDate.after(releaseDate)) {
        context.getString(R.string.summary_android_release_pushed)
    } else {
        context.getString(R.string.summary_android_waiting)
    }
}

private fun isSystemNightMode(context: Context): Boolean {
    return (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
}
