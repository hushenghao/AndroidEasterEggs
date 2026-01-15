@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.dede.android_eggs.views.settings.compose.options

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.flavor.FlavorFeatures
import com.dede.android_eggs.flavor.LatestVersion
import com.dede.android_eggs.util.AGPUtils
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.compareStringVersion
import com.dede.android_eggs.views.main.compose.isAgreedPrivacyPolicy
import com.dede.android_eggs.views.settings.compose.basic.Option
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock
import com.dede.basic.Utils
import com.dede.basic.toast
import kotlinx.coroutines.launch
import com.dede.android_eggs.resources.R as StringR


@Composable
fun VersionOption() {
    val context = LocalContext.current
    val (versionName, versionCode) = remember(context) { Utils.getAppVersionPair(context) }
    Option(
        shape = OptionShapes.firstShape(),
        leadingIcon = imageVectorIconBlock(imageVector = Icons.Outlined.NewReleases),
        title = stringResource(R.string.label_version, versionName, versionCode),
        desc = AGPUtils.getVcsRevision(7),
        trailingContent = {
            if (isAgreedPrivacyPolicy(context)) {
                UpgradeIconButton()
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
}

@Composable
private fun UpgradeIconButton() {
    var newVersion: LatestVersion? by remember { mutableStateOf(null) }
    Box(
        modifier = Modifier.offset(x = 4.dp),// fix padding end of Option
        contentAlignment = Alignment.Center
    ) {
        val context = LocalContext.current
        val activity = LocalActivity.current
        val coroutineScope = rememberCoroutineScope()

        var iconButtonAnimatable by remember { mutableStateOf(true) }
        val infiniteTransition =
            rememberInfiniteTransition(label = "UpgradeIconButtonInfiniteTransition")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f, targetValue = 1.2f,
            animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse)
        )
        val degrees by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        FilledTonalIconButton(
            modifier = Modifier.graphicsLayer {
                if (iconButtonAnimatable) {
                    scaleY = scale
                    scaleX = scale
                    rotationZ = degrees
                }
            },
            shapes = IconButtonShapes(MaterialShapes.Cookie12Sided.toShape()),
            onClick = onClick@{
                if (activity == null) {
                    iconButtonAnimatable = false
                    return@onClick
                }
                coroutineScope.launch {
                    val latestVersion = FlavorFeatures.get().checkUpdate(activity)
                    if (latestVersion != null) {
                        if (compareStringVersion(
                                latestVersion.versionName,
                                Utils.getAppVersionPair(context).first
                            ) > 0
                        ) {
                            newVersion = latestVersion
                        } else {
                            context.toast(StringR.string.toast_no_update_found)
                            iconButtonAnimatable = false
                        }
                    } else {
                        iconButtonAnimatable = false
                    }
                }
            }
        ) {
            // no content
        }
        Icon(imageVector = Icons.Rounded.Upgrade, contentDescription = null)
    }

    if (newVersion != null) {
        UpgradeDialog(version = newVersion!!, onDismiss = { newVersion = null })
    }
}

@Composable
private fun UpgradeDialog(version: LatestVersion, onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
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
                Text(text = version.versionName)
            }
        },
        text = {
            val annotatedString = buildAnnotatedString {
                append(version.changelog)

                val regex = Regex("#(\\d+)")
                regex.findAll(version.changelog)
                    .forEach { match ->
                        // issue or pull request id
                        // github can automatically redirect to correct page
                        val id = match.groupValues.getOrNull(1)
                        val url = context.getString(R.string.url_github_issues_id, id)
                        addLink(
                            LinkAnnotation.Url(url = url, linkInteractionListener = {
                                CustomTabsBrowser.launchUrl(context, url)
                            }),
                            match.range.first, match.range.last + 1
                        )
                    }
            }
            Text(text = annotatedString)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    CustomTabsBrowser.launchUrl(context, version.downloadUrl)
                }
            ) {
                Text(text = stringResource(StringR.string.label_update))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
    )
}
