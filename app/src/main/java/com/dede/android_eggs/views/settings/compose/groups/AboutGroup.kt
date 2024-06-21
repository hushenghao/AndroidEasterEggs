package com.dede.android_eggs.views.settings.compose.groups

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.android_eggs.views.settings.compose.basic.Option
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock
import com.dede.android_eggs.views.settings.compose.options.GithubOption
import com.dede.android_eggs.views.settings.compose.options.VersionOption

@Preview
@Composable
fun AboutGroup() {
    val context = LocalContext.current

    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.Info,
        title = stringResource(R.string.label_about)
    ) {
        VersionOption()
        Option(
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.ic_pgyer_logo),
                    contentDescription = stringResource(R.string.label_beta),
                    modifier = Modifier
                        .size(24.dp)
                        .requiredSize(33.dp)
                )
            },
            title = stringResource(R.string.label_beta),
            desc = stringResource(R.string.url_beta),
            trailingContent = imageVectorIconBlock(imageVector = Icons.Rounded.Download),
            onClick = {
                CustomTabsBrowser.launchUrl(context, R.string.url_beta)
            }
        )

        GithubOption()
        Option(
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.Policy,
                contentDescription = stringResource(R.string.label_privacy_policy),
            ),
            title = stringResource(R.string.label_privacy_policy),
            onClick = {
                CustomTabsBrowser.launchUrl(context, R.string.url_privacy)
            }
        )
    }
}
