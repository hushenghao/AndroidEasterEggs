package com.dede.android_eggs.views.settings.compose

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Balance
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material.icons.rounded.GTranslate
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.basic.requireDrawable
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun AboutGroup() {
    val context = LocalContext.current

    fun launchUrl(@StringRes url: Int) {
        CustomTabsBrowser.launchUrl(context, context.getString(url).toUri())
    }

    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.Info,
        title = stringResource(R.string.label_about)
    ) {
        Option(
            leadingIcon = {
                val drawable =
                    rememberDrawablePainter(context.requireDrawable(R.mipmap.ic_launcher_round))
                Image(
                    painter = drawable,
                    contentDescription = stringResource(
                        R.string.label_version,
                        BuildConfig.VERSION_NAME,
                        BuildConfig.VERSION_CODE
                    ),
                )
            },
            title = stringResource(
                R.string.label_version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE
            ),
            desc = BuildConfig.GIT_HASH,
            onClick = {
                CustomTabsBrowser.launchUrl(
                    context,
                    context.getString(R.string.url_github_commit, BuildConfig.GIT_HASH).toUri()
                )
            }
        )

        Option(
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.ic_pgyer_logo),
                    contentDescription = stringResource(R.string.label_beta),
                    modifier = Modifier
                        .size(34.dp)
                )
            },
            title = stringResource(R.string.label_beta),
            desc = stringResource(R.string.url_beta),
            onClick = {
                launchUrl(R.string.url_beta)
            }
        )
        Option(
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.ic_github_logo),
                    contentDescription = stringResource(R.string.label_github),
                    modifier = Modifier.size(24.dp)
                )
            },
            title = stringResource(R.string.label_github),
            desc = stringResource(R.string.url_github),
            onClick = {
                launchUrl(R.string.url_github)
            }
        )
        Option(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.GTranslate,
                    contentDescription = stringResource(R.string.label_translation),
                )
            },
            title = stringResource(R.string.label_translation),
            desc = stringResource(R.string.url_translation),
            onClick = {
                launchUrl(R.string.url_translation)
            }
        )
        Option(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Balance,
                    contentDescription = stringResource(R.string.label_license),
                )
            },
            title = stringResource(R.string.label_license),
            desc = stringResource(R.string.url_license),
            onClick = {
                launchUrl(R.string.url_license)
            }
        )
        Option(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Policy,
                    contentDescription = stringResource(R.string.label_privacy_policy),
                )
            },
            title = stringResource(R.string.label_privacy_policy),
            desc = stringResource(R.string.url_privacy),
            onClick = {
                launchUrl(R.string.url_privacy)
            }
        )
        Option(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Feedback,
                    contentDescription = stringResource(R.string.label_email),
                )
            },
            title = stringResource(R.string.label_email),
            desc = stringResource(R.string.url_github_issues),
            onClick = {
                launchUrl(R.string.url_github_issues)
            }
        )
    }
}