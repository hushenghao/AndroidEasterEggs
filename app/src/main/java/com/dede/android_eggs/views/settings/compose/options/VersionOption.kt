@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.dede.android_eggs.views.settings.compose.options

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.rounded.Upgrade
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.flavor.FlavorFeatures
import com.dede.android_eggs.flavor.LatestRelease
import com.dede.android_eggs.util.AGPUtils
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.compareStringVersion
import com.dede.android_eggs.views.main.compose.isAgreedPrivacyPolicy
import com.dede.android_eggs.views.settings.compose.basic.Option
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock
import com.dede.basic.toast
import kotlinx.coroutines.launch
import com.dede.android_eggs.resources.R as StringR


@Composable
fun VersionOption() {
    val context = LocalContext.current

    var newRelease: LatestRelease? by remember { mutableStateOf(null) }
    Option(
        shape = OptionShapes.firstShape(),
        leadingIcon = imageVectorIconBlock(imageVector = Icons.Outlined.NewReleases),
        title = stringResource(
            R.string.label_version,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        ),
        desc = AGPUtils.getVcsRevision(7),
        trailingContent = {
            if (isAgreedPrivacyPolicy(context)) {
                val activity = LocalActivity.current
                val coroutineScope = rememberCoroutineScope()
                FilledTonalIconButton(
                    shapes = IconButtonShapes(MaterialShapes.SoftBurst.toShape()),
                    onClick = onClick@{
                        if (activity == null) {
                            return@onClick
                        }
                        coroutineScope.launch {
                            val latestRelease = FlavorFeatures.get().checkUpdate(activity)
                            if (latestRelease != null) {
                                if (compareStringVersion(
                                        latestRelease.versionName,
                                        BuildConfig.VERSION_NAME
                                    ) > 0
                                ) {
                                    newRelease = latestRelease
                                } else {
                                    context.toast(StringR.string.toast_no_update_found)
                                }
                            }
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Rounded.Upgrade, contentDescription = null)
                }
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.NavigateNext,
                    contentDescription = null
                )
            }
        },
        onClick = {
            val revision = AGPUtils.getVcsRevision()
            val uri = if (revision == null) {
                context.getString(R.string.url_github)
            } else {
                context.getString(R.string.url_github_commit, revision)
            }
            CustomTabsBrowser.launchUrl(context, uri)
        }
    )

    if (newRelease != null) {
        val release = newRelease!!
        AlertDialog(
            onDismissRequest = { newRelease = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.NewReleases,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(release.versionName)
                }
            },
            text = {
                Text(release.changelog)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        CustomTabsBrowser.launchUrl(context, release.downloadUrl)
                    }
                ) {
                    Text(stringResource(StringR.string.label_update))
                }
            },
            dismissButton = {
                TextButton(onClick = { newRelease = null }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }
}
