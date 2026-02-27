package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.views.settings.compose.basic.SettingPref
import com.dede.android_eggs.views.settings.compose.basic.rememberPrefBoolState
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.time.Instant

// https://developer.android.com/developer-verification
private val LOCKED_TIME_MILLIS = Instant.parse("2026-09-01T00:00:00Z").toEpochMilliseconds()

private fun getLockedDownDesc(millis: Long): String {
    val seconds = millis / 1000
    val days = seconds / (24 * 3600)
    val hours = (seconds % (24 * 3600)) / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60
    return "Android will become a locked-down platform\nin ${days}d ${hours}h ${minutes}m ${remainingSeconds}s"
}

private const val KEY = "key_keep_android_open_dismissed"

@Composable
fun KeepAndroidOpenPref() {
    var isDismissed by rememberPrefBoolState(KEY, false)
    if (isDismissed) {
        return
    }
    SwipeToDismissBox(
        state = rememberSwipeToDismissBoxState(),
        backgroundContent = {
        },
        onDismiss = {
            isDismissed = true
        },
    ) {
        val context = LocalContext.current
        val infiniteTransition =
            rememberInfiniteTransition(label = "KeepAndroidOpenInfiniteTransition")
        val containerColor by infiniteTransition.animateColor(
            initialValue = Color(0xFF_CC2929), targetValue = Color(0xFF_AE1A1A),
            animationSpec = infiniteRepeatable(
                tween(2000, easing = LinearEasing),
                RepeatMode.Reverse
            )
        )
        SettingPref(
            colors = CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = Color.White,
            ),
            desc = {
                val countdownMillis = rememberCountdown(LOCKED_TIME_MILLIS)
                val shadowOffset = with(LocalDensity.current) {
                    Offset(0.5.dp.toPx(), 2.dp.toPx())
                }
                Text(
                    text = getLockedDownDesc(countdownMillis).uppercase(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        shadow = Shadow(color = Color(0xFF_941919), offset = shadowOffset)
                    ),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
            },
            onClick = {
                CustomTabsBrowser.launchUrl(context, "https://keepandroidopen.org/")
            }
        )
    }
}

@Composable
private fun rememberCountdown(targetMillis: Long, intervalMillis: Long = 1000L): Long {
    var remainingMillis by remember(targetMillis) {
        mutableLongStateOf(maxOf(0L, targetMillis - System.currentTimeMillis()))
    }
    LaunchedEffect(targetMillis, intervalMillis) {
        while (remainingMillis > 0L) {
            // Align next update to the nearest second boundary to keep 1s alignment
            val msToNextSecond = remainingMillis % intervalMillis
            val wait = if (msToNextSecond == 0L) {
                min(intervalMillis, remainingMillis)
            } else {
                // wait the remainder to reach the next second boundary
                msToNextSecond
            }

            delay(wait)
            remainingMillis -= wait
        }
    }
    return remainingMillis
}
