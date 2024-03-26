package com.dede.android_eggs.views.settings.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material.icons.rounded.Hive
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.composes.icons.crowdin
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
            leadingIcon = {
                Icon(
                    imageVector = Icons.crowdin,
                    contentDescription = stringResource(id = R.string.label_contribute),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(1.dp)
                )
            },
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