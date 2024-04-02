package com.dede.android_eggs.views.settings.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Hive
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.composes.icons.Crowdin
import com.dede.android_eggs.util.CustomTabsBrowser

@Composable
fun ContributeGroup() {
    val context = LocalContext.current
    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.Hive,
        title = stringResource(id = R.string.label_contribute)
    ) {
        Option(
            shape = OptionShapes.firstShape(),
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Crowdin,
                contentDescription = stringResource(id = R.string.label_contribute),
            ),
            title = stringResource(R.string.label_translation),
            desc = stringResource(R.string.url_translation),
            onClick = {
                CustomTabsBrowser.launchUrl(context, R.string.url_translation)
            }
        )
        Option(
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.Feedback,
                contentDescription = stringResource(R.string.label_feedback),
            ),
            title = stringResource(R.string.label_feedback),
            desc = stringResource(R.string.url_github_issues),
            onClick = {
                CustomTabsBrowser.launchUrl(context, R.string.url_github_issues)
            }
        )
        Option(
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.Forum,
                contentDescription = stringResource(R.string.label_discussions),
            ),
            title = stringResource(R.string.label_discussions),
            desc = stringResource(R.string.url_github_discussions),
            onClick = {
                CustomTabsBrowser.launchUrl(context, R.string.url_github_discussions)
            }
        )
        Option(
            shape = OptionShapes.lastShape(),
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.Coffee,
                contentDescription = stringResource(R.string.label_donate),
            ),
            title = stringResource(R.string.label_donate),
            onClick = {
                CustomTabsBrowser.launchUrl(context, R.string.url_sponsor)
            }
        )
    }
}