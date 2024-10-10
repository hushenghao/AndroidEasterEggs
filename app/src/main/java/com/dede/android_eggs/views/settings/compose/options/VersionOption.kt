package com.dede.android_eggs.views.settings.compose.options

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.util.AGPUtils
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.views.settings.compose.basic.Option
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock


@Composable
fun VersionOption() {
    val context = LocalContext.current
    Option(
        shape = OptionShapes.firstShape(),
        leadingIcon = imageVectorIconBlock(imageVector = Icons.Outlined.NewReleases),
        title = stringResource(
            R.string.label_version,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        ),
        desc = AGPUtils.getVcsRevision(7),
        onClick = {
            CustomTabsBrowser.launchUrl(context, R.string.url_beta)
        }
    )
}
