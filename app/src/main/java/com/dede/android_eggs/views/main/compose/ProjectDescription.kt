@file:OptIn(ExperimentalLayoutApi::class)

package com.dede.android_eggs.views.main.compose

import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.createChooser
import com.dede.android_eggs.views.settings.component.ComponentManagerFragment
import com.dede.android_eggs.views.timeline.AndroidTimelineFragment
import com.dede.basic.requireDrawable
import kotlin.math.roundToInt


@Composable
private fun ChipItem(
    @StringRes textRes: Int,
    separator: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = stringResource(textRes),
            style = typography.bodyMedium,
            color = colorScheme.secondary,
            modifier = Modifier.clickable(onClick = onClick)
        )
        if (separator) {
            Text(
                text = stringResource(id = R.string.char_separator),
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        }
    }
}

@Composable
private fun ChipItem2(
    @StringRes textRes: Int,
    onClick: () -> Unit
) {
    Text(
        text = stringResource(textRes),
        style = typography.titleSmall,
        color = colorScheme.secondary,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable(onClick = onClick)
    )
}

@Preview(showBackground = true)
@Composable
fun ProjectDescription() {
    val context = LocalContext.current
    val fm = LocalFragmentManager.current

    fun openCustomTab(@StringRes uri: Int) {
        CustomTabsBrowser.launchUrl(context, context.getString(uri).toUri())
    }

    fun openBrowser(uri: String) {
        CustomTabsBrowser.launchUrlByBrowser(context, uri.toUri())
    }

    val px = with(LocalDensity.current) {
        50.dp.toPx().roundToInt()
    }
    val bitmap = remember(context.theme) {
        context.requireDrawable(R.mipmap.ic_launcher_round)
            .toBitmap(px, px).asImageBitmap()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .then(Modifier.padding(bottom = 30.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(50.dp),
                bitmap = bitmap,
                contentDescription = null
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = stringResource(
                        R.string.label_version,
                        BuildConfig.VERSION_NAME,
                        BuildConfig.VERSION_CODE
                    ),
                    style = typography.titleSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = BuildConfig.GIT_HASH,
                    style = typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        val commitId =
                            context.getString(R.string.url_github_commit, BuildConfig.GIT_HASH)
                        CustomTabsBrowser.launchUrl(context, commitId.toUri())
                    }
                )
            }
        }
        Text(
            text = stringResource(R.string.label_project_desc),
            modifier = Modifier.padding(top = 20.dp),
            style = typography.bodyMedium
        )
        FlowRow(
            modifier = Modifier.padding(top = 20.dp)
        ) {
            ChipItem(R.string.label_github) {
                openCustomTab(R.string.url_github)
            }
            ChipItem(R.string.label_translation) {
                openCustomTab(R.string.url_translation)
            }
            ChipItem(R.string.label_timeline) {
                AndroidTimelineFragment.show(fm ?: return@ChipItem)
            }
            ChipItem(R.string.label_component_manager) {
                ComponentManagerFragment.show(fm ?: return@ChipItem)
            }
            ChipItem(R.string.label_star) {
                val uri = context.getString(R.string.url_market_detail, context.packageName)
                openBrowser(uri)
            }
            ChipItem(R.string.label_donate) {
                openCustomTab(R.string.url_sponsor)
            }
            ChipItem(R.string.label_share) {
                val target = Intent(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_TEXT, context.getString(R.string.url_share))
                    .setType("text/plain")
                val intent = context.createChooser(target)
                context.startActivity(intent)
            }
            ChipItem(R.string.label_beta, false) {
                openCustomTab(R.string.url_beta)
            }
        }
        Wavy(res = R.drawable.ic_wavy_line_1, true, colorScheme.secondaryContainer)
        FlowRow {
            Text(
                text = stringResource(R.string.app_name),
                style = typography.titleSmall,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .then(Modifier.padding(end = 8.dp))
            )
            ChipItem2(R.string.label_privacy_policy) {
                openCustomTab(R.string.url_privacy)
            }
            ChipItem2(R.string.label_license) {
                openCustomTab(R.string.url_license)
            }
            ChipItem2(R.string.label_email) {
                openBrowser(context.getString(R.string.url_mail))
            }
        }
    }
}