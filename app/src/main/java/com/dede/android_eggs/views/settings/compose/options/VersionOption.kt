@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.dede.android_eggs.views.settings.compose.options

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.flavor.FlavorFeatures
import com.dede.android_eggs.flavor.LatestVersion
import com.dede.android_eggs.util.AGPUtils
import com.dede.android_eggs.util.compareStringVersion
import com.dede.android_eggs.views.main.compose.isAgreedPrivacyPolicy
import com.dede.android_eggs.views.settings.compose.basic.Option
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock
import com.dede.android_eggs.views.settings.compose.prefs.IconShapePrefUtil
import com.dede.basic.Utils
import com.dede.basic.toast
import kotlinx.coroutines.launch
import com.dede.android_eggs.resources.R as StringR


@Composable
fun VersionOption(shape: Shape = OptionShapes.defaultShape) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val (versionName, versionCode) = remember(context) { Utils.getAppVersionPair(context) }
    var newVersion: LatestVersion? by remember { mutableStateOf(null) }
    val resources = LocalResources.current
    Option(
        shape = shape,
        leadingIcon = imageVectorIconBlock(imageVector = Icons.Outlined.NewReleases),
        title = stringResource(R.string.label_version, versionName, versionCode),
        desc = AGPUtils.getVcsRevision(7),
        trailingContent = {
            if (isAgreedPrivacyPolicy(context)) {
                UpgradeIconButton(
                    newVersion = newVersion,
                    onNewVersionChange = { newVersion = it },
                )
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
                resources.getString(R.string.url_github)
            } else {
                resources.getString(R.string.url_github_commit, revision)
            }
            uriHandler.openUri(uri)
        }
    )
    if (newVersion != null) {
        UpgradeDialog(version = newVersion!!, onDismiss = { newVersion = null })
    }
}

@Composable
private fun UpgradeIconButton(
    newVersion: LatestVersion?,
    onNewVersionChange: (LatestVersion?) -> Unit,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val coroutineScope = rememberCoroutineScope()

    var scaleAnimatable by remember { mutableStateOf(newVersion == null) }
    val scaleAnim = remember { Animatable(1f) }
    LaunchedEffect(scaleAnimatable) {
        while (scaleAnimatable) {
            scaleAnim.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(durationMillis = 800)
            )
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
        }
        scaleAnim.animateTo(1f, tween(durationMillis = 300))
    }
    FilledTonalIconButton(
        modifier = Modifier
            .offset(x = 4.dp)// fix padding end of Option
            .graphicsLayer {
                val s = scaleAnim.value
                scaleY = s
                scaleX = s
            },
        shape = IconShapePrefUtil.getIconShape(),
        onClick = onClick@{
            if (activity == null) {
                scaleAnimatable = false
                return@onClick
            }
            coroutineScope.launch {
                val latestVersion = FlavorFeatures.get().checkUpdate(activity).getOrNull()
                if (latestVersion != null) {
                    if (compareStringVersion(
                            latestVersion.versionName,
                            Utils.getAppVersionPair(context).first
                        ) > 0
                    ) {
                        onNewVersionChange(latestVersion)
                        scaleAnimatable = false
                    } else {
                        context.toast(StringR.string.toast_no_update_found)
                        scaleAnimatable = false
                    }
                } else {
                    scaleAnimatable = false
                }
            }
        }
    ) {
        Icon(imageVector = Icons.Rounded.Upgrade, contentDescription = null)
    }
}

@Composable
private fun UpgradeDialog(version: LatestVersion, onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current
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
                        val id = match.groupValues.getOrNull(1) ?: ""
                        val url = stringResource(R.string.url_github_issues_id, id)
                        addLink(
                            LinkAnnotation.Url(url = url, linkInteractionListener = {
                                uriHandler.openUri(url)
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
                    uriHandler.openUri(version.downloadUrl)
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
