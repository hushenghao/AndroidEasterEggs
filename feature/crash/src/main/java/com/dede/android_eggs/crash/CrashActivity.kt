package com.dede.android_eggs.crash

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.SentimentDissatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.util.AGPUtils
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.views.theme.AppTheme
import com.dede.basic.Utils
import com.dede.basic.copy
import kotlin.system.exitProcess

/**
 * App crash report
 */
class CrashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.enableEdgeToEdge(this)
        super.onCreate(savedInstanceState)

        val tr: Throwable? = GlobalExceptionHandler.getUncaughtException(intent)
        if (tr == null) {
            finish()
            return
        }

        setContent {
            AppTheme {
                Surface {
                    CrashScreen(tr)
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun CrashScreen(tr: Throwable = IllegalStateException("test")) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val titleSpan = remember(tr) {
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("[Bug] App Crash: %s".format(tr.toString()))
            }
        }
    }
    val (versionName, versionCode) = remember {
        Utils.getAppVersionPair(context)
    }
    val bodySpan = remember(tr) {
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                val devicesInfo =
                    "Device: %s (%s - %s), SDK: %s (%d), App: %s (%d), VcsRevision: %s\n\n".format(
                        Build.MODEL, Build.BRAND, Build.DEVICE,
                        Build.VERSION.RELEASE, Build.VERSION.SDK_INT,
                        versionName, versionCode,
                        AGPUtils.getVcsRevision(7)
                    )
                append(devicesInfo)
            }

            val stackTraceString = try {
                Log.getStackTraceString(tr)
            } catch (ignore: Throwable) {
                tr.toString()
            }
            append(stackTraceString)
        }
    }

    fun copyCrashPlainText() {
        context.copy("%s\n\n%s".format(titleSpan.text, bodySpan.text))
    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.SentimentDissatisfied,
                contentDescription = null,
                modifier = Modifier
                    .statusBarsPadding()
                    .size(42.dp),
                tint = colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                style = typography.titleMedium
            )
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                onClick = {
                    expanded = !expanded
                },
                shape = shapes.extraLarge,
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.9f)
                    .navigationBarsPadding()
                    .animateContentSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (expanded) bodySpan else titleSpan,
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        style = typography.bodySmall,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (!expanded) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 12.dp)
                .statusBarsPadding(),
            onClick = {
                exitProcess(0)
            },
        ) {
            Icon(
                imageVector = Icons.Rounded.PowerSettingsNew,
                contentDescription = null
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .navigationBarsPadding()
                .fillMaxWidth(fraction = 0.8f),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            FloatingActionButton(
                onClick = {
                    val intent = Utils.getLaunchIntent(context)
                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        context.startActivity(intent)
                    }
                },
                shape = FloatingActionButtonDefaults.largeShape
            ) {
                Icon(
                    imageVector = Icons.Rounded.RestartAlt,
                    contentDescription = null
                )
            }
            FloatingActionButton(
                onClick = {
                    copyCrashPlainText()
                },
                shape = FloatingActionButtonDefaults.largeShape
            ) {
                Icon(
                    imageVector = Icons.Rounded.ContentCopy,
                    contentDescription = null
                )
            }
            FloatingActionButton(
                onClick = {
                    val uri = Uri.parse(context.getString(R.string.url_github_issues))
                        .buildUpon()
                        .appendPath("new")
                        .appendQueryParameter("title", titleSpan.text)
                        .appendQueryParameter("body", "```\n%s\n```".format(bodySpan.text))
                        .build()
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    } catch (_: ActivityNotFoundException) {
                    }
                    copyCrashPlainText()
                },
                shape = FloatingActionButtonDefaults.largeShape
            ) {
                Icon(
                    imageVector = Icons.Rounded.BugReport,
                    contentDescription = null
                )
            }
        }
    }
}
