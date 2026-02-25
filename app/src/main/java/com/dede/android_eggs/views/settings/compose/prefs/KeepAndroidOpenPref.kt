package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.views.settings.compose.basic.SettingPref
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefIcon
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

@Composable
fun KeepAndroidOpenPref() {
    val context = LocalContext.current
    val countdownMillis = rememberCountdown(LOCKED_TIME_MILLIS)
    SettingPref(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        title = "Keep Android Open",
        desc = getLockedDownDesc(countdownMillis).uppercase(),
        leadingIcon = {
            SettingPrefIcon(
                modifier = Modifier.padding(start = 12.dp),
                icon = Icons.Rounded.LockOpen,
            )
        },
        trailingContent = {
            Icon(
                modifier = Modifier.padding(end = 12.dp),
                imageVector = Icons.AutoMirrored.Rounded.NavigateNext,
                contentDescription = null,
            )
        },
        onClick = {
            CustomTabsBrowser.launchUrl(context, "https://keepandroidopen.org/")
        }
    )
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
