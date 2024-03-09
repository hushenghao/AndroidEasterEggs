package com.dede.android_eggs.views.settings.compose

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.rounded.Balance
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.createChooser

@Preview
@Composable
fun AboutGroup() {
    val context = LocalContext.current

    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.Info,
        title = stringResource(R.string.label_about)
    ) {
        Option(
            shape = OptionShapes.firstShape(),
            leadingIcon = imageVectorIconBlock(imageVector = Icons.Outlined.NewReleases),
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
                Box(
                    modifier = Modifier.size(24.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_pgyer_logo),
                        contentDescription = stringResource(R.string.label_beta),
                        modifier = Modifier.requiredSize(33.dp)
                    )
                }
            },
            title = stringResource(R.string.label_beta),
            desc = stringResource(R.string.url_beta),
            onClick = {
                CustomTabsBrowser.launchUrl(context, R.string.url_beta)
            }
        )

        Option(
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.Share,
                contentDescription = stringResource(id = R.string.label_share)
            ),
            title = stringResource(R.string.label_share),
            onClick = {
                val target = Intent(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_TEXT, context.getString(R.string.url_share))
                    .setType("text/plain")
                val intent = context.createChooser(target)
                context.startActivity(intent)
            }
        )
        Option(
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.StarRate,
                contentDescription = stringResource(R.string.label_star),
            ),
            title = stringResource(R.string.label_star),
            onClick = {
                CustomTabsBrowser.launchUrlByBrowser(
                    context,
                    context.getString(R.string.url_market_detail).toUri()
                )
            }
        )

        Option(
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.Policy,
                contentDescription = stringResource(R.string.label_privacy_policy),
            ),
            title = stringResource(R.string.label_privacy_policy),
            desc = stringResource(R.string.url_privacy),
            onClick = {
                CustomTabsBrowser.launchUrl(context, R.string.url_privacy)
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
                CustomTabsBrowser.launchUrl(context, R.string.url_github)
            }
        )
        Option(
            shape = OptionShapes.lastShape(),
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.Balance,
                contentDescription = stringResource(R.string.label_license),
            ),
            title = stringResource(R.string.label_license),
            desc = "Apache-2.0 license",
            onClick = {
                CustomTabsBrowser.launchUrl(context, R.string.url_license)
            }
        )
    }
}