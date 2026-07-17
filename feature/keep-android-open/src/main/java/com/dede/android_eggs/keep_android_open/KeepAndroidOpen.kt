package com.dede.android_eggs.keep_android_open

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dede.android_eggs.views.settings.compose.basic.rememberPrefBoolState
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

// https://keepandroidopen.org/faq
private val LOCKED_TIME_MILLIS = Instant.parse("2027-01-01T00:00:00Z").toEpochMilliseconds()

// https://github.com/keepandroidopen/keepandroidopen.github.io/blob/main/public/banner.js
private val SUPPORTED_LOCALES = setOf(
    "en", "fr", "es", "ca", "it", "pt-BR", "de", "da", "fi", "nl",
    "pl", "cs", "sk", "sq", "el", "ru", "uk", "hu", "bg", "be",
    "tr", "kk", "he", "ar", "fa", "vi", "th", "id", "tl", "bn",
    "hi", "zh-CN", "zh-TW", "ja", "ko", "eu",
)

private fun resolveWebsiteLocale(context: android.content.Context): String {
    val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales.get(0)?.toLanguageTag()
    } else {
        @Suppress("DEPRECATION")
        context.resources.configuration.locale?.toLanguageTag()
    } ?: java.util.Locale.getDefault().toLanguageTag()

    // Exact match
    if (tag in SUPPORTED_LOCALES) return tag

    // Case-insensitive match
    val lower = tag.lowercase()
    SUPPORTED_LOCALES.firstOrNull { it.lowercase() == lower }?.let { return it }

    // Base language fallback (e.g. "de-CH" -> find "de")
    val base = lower.split("-").first()
    SUPPORTED_LOCALES.firstOrNull { it.lowercase() == base }?.let { return it }

    // Regional variant fallback (e.g. "pt" -> "pt-BR")
    SUPPORTED_LOCALES.firstOrNull { it.lowercase().split("-").first() == base }?.let { return it }

    return "en"
}

private fun getLockedDownDesc(millis: Long, context: android.content.Context): String {
    val seconds = millis / 1000
    val days = seconds / (24 * 3600)
    val hours = (seconds % (24 * 3600)) / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60
    val message = context.getString(R.string.keep_android_open_message)
    val countdown = context.getString(
        R.string.keep_android_open_countdown,
        days, hours, minutes, remainingSeconds
    )
    return "$message\n$countdown"
}

private const val KEY = "key_keep_android_open_dismissed"

@Preview
@Composable
fun KeepAndroidOpen() {
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
        val uriHandler = LocalUriHandler.current
        val websiteUrl = remember {
            val websiteLocale = resolveWebsiteLocale(context)
            if (websiteLocale == "en") {
                "https://keepandroidopen.org/"
            } else {
                "https://keepandroidopen.org/$websiteLocale/"
            }
        }
        Card(
            onClick = { uriHandler.openUri(websiteUrl) },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFB71C1C),
                contentColor = Color.White,
            ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFD32F2F), Color(0xFFB71C1C))
                        )
                    )
                    .drawBehind {
                        drawRect(
                            color = Color(0xFF801313),
                            topLeft = Offset(0f, size.height - 4.dp.toPx()),
                            size = Size(size.width, 4.dp.toPx()),
                        )
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                val countdownMillis = rememberCountdown(LOCKED_TIME_MILLIS)
                Text(
                    text = getLockedDownDesc(countdownMillis, context).uppercase(),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        lineHeight = 16.sp,
                        shadow = Shadow(
                            color = Color(0xFF5E0D0D),
                            offset = Offset.Zero,
                            blurRadius = 10f,
                        ),
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun rememberCountdown(targetMillis: Long, intervalMillis: Long = 1000L): Long {
    var remainingMillis by remember(targetMillis) {
        mutableLongStateOf(maxOf(0L, targetMillis - System.currentTimeMillis()))
    }
    LaunchedEffect(targetMillis, intervalMillis) {
        while (remainingMillis > 0L) {
            val msToNextSecond = remainingMillis % intervalMillis
            val wait = if (msToNextSecond == 0L) {
                min(intervalMillis, remainingMillis)
            } else {
                msToNextSecond
            }

            delay(wait.milliseconds)
            remainingMillis -= wait
        }
    }
    return remainingMillis
}
