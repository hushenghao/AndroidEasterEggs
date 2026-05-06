package com.dede.android_eggs.views.settings.compose.groups

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocalPolice
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.navigation.LocalNavigator
import com.dede.android_eggs.ui.composes.icons.outlined.Beta
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.android_eggs.views.settings.compose.basic.Option
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock
import com.dede.android_eggs.views.settings.compose.options.GithubOption
import com.dede.android_eggs.views.settings.compose.options.TestCrashOption
import com.dede.android_eggs.views.settings.compose.options.VersionOption
import com.dede.android_eggs.resources.R as StringsR

@Preview
@Composable
fun AboutGroup() {
    val navigator = LocalNavigator.current
    val uriHandler = LocalUriHandler.current
    val betaUrl = stringResource(R.string.url_beta)
    val privacyUrl = stringResource(R.string.url_privacy)
    val wikiUrl = stringResource(R.string.url_wiki)
    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.Info,
        title = stringResource(StringsR.string.label_about)
    ) {
        VersionOption(shape = OptionShapes.firstShape())
        Option(
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Outlined.Beta,
                contentDescription = stringResource(StringsR.string.label_beta),
            ),
            title = stringResource(StringsR.string.label_beta),
            trailingContent = imageVectorIconBlock(imageVector = Icons.Rounded.Download),
            onClick = {
                uriHandler.openUri(betaUrl)
            }
        )

        GithubOption()
        Option(
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.Policy,
                contentDescription = stringResource(StringsR.string.label_privacy_policy),
            ),
            title = stringResource(StringsR.string.label_privacy_policy),
            onClick = {
                uriHandler.openUri(privacyUrl)
            }
        )

        Option(
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                contentDescription = stringResource(StringsR.string.label_wiki),
            ),
            title = stringResource(StringsR.string.label_wiki),
            onClick = {
                uriHandler.openUri(wikiUrl)
            },
        )

        Option(
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.LocalPolice,
                contentDescription = stringResource(StringsR.string.label_open_source_license),
            ),
            title = stringResource(StringsR.string.label_open_source_license),
            onClick = {
                navigator.navigate(EasterEggsDestination.LibrariesInfo)
            },
            shape = OptionShapes.lastShape()
        )

        if (BuildConfig.DEBUG) {
            TestCrashOption()
        }
    }
}
