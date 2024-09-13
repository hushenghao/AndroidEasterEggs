package com.android_next.egg

import android.content.Context
import android.content.res.Configuration
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.dede.basic.requireDrawable
import com.dede.basic.utils.DynamicObjectUtils
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.max
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
                    text = AndroidNextEasterEgg.getTimelineMessage(context),
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
                val customTabsBrowser =
                    DynamicObjectUtils.asDynamicObject("com.dede.android_eggs.util.CustomTabsBrowser")
                        .getProperty("INSTANCE")
                        .getValue()
                if (customTabsBrowser != null) {
                    DynamicObjectUtils.asDynamicObject(customTabsBrowser)
                        .invokeMethod(
                            "launchUrl",
                            arrayOf(Context::class.java, Int::class.java),
                            arrayOf(context, R.string.url_android_releases)
                        )
                }
                // CustomTabsBrowser.launchUrl(context, R.string.url_android_releases)
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

@Composable
private fun AndroidReleaseTimeline() {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)// [0, 11]
    // Month    Progress    Calender.MONTH
    // Feb          0           1
    // ...
    // Jul          5           6
    // Aug          -           7
    val offsetXArr = intArrayOf(20, 111, 202, 294, 386, 478, 584)
    val nextReleaseYear = AndroidNextEasterEgg.RELEASE_YEAR
    val offsetXIndex =
        if (year < nextReleaseYear || (year == nextReleaseYear && month < Calendar.FEBRUARY)) {
            // No preview
            -1
        } else if (year == nextReleaseYear && month in Calendar.FEBRUARY..Calendar.JULY) {
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
            if (isSystemNightMode(LocalContext.current)) {
                // Increase the overall brightness and more blue brightness
                matrix.setToScale(1.3f, 1.5f, 2f, 1f)
            }
            Image(
                painter = painterResource(id = R.drawable.timeline_bg),
                contentDescription = null,
                colorFilter = ColorFilter.colorMatrix(matrix)
            )
        }
    }
}

private fun isSystemNightMode(context: Context): Boolean {
    return (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
}
