package com.dede.android_eggs.views.settings.compose.options

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.composes.icons.Github
import com.dede.android_eggs.views.settings.compose.basic.Option
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock
import com.dede.android_eggs.resources.R as StringsR

@Composable
fun GithubOption(shape: Shape = OptionShapes.defaultShape) {
    val uriHandler = LocalUriHandler.current
    val githubUrl = stringResource(R.string.url_github)
    Option(
        leadingIcon = imageVectorIconBlock(
            imageVector = Icons.Github,
            contentDescription = stringResource(StringsR.string.label_github)
        ),
        shape = shape,
        title = stringResource(StringsR.string.label_github),
        desc = githubUrl,
        trailingContent = imageVectorIconBlock(imageVector = Icons.Rounded.Star),
        onClick = {
            uriHandler.openUri(githubUrl)
        }
    )
}
