package com.dede.android_eggs.views.settings.compose.groups

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Hive
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.composes.icons.Crowdin
import com.dede.android_eggs.ui.composes.icons.rounded.Experiment
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.android_eggs.views.settings.compose.basic.Option
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock
import com.dede.android_eggs.resources.R as StringsR

@Composable
fun ContributeGroup() {
    val uriHandler = LocalUriHandler.current
    val translationUrl = stringResource(R.string.url_translation)
    val sponsorUrl = stringResource(R.string.url_sponsor)
    val pgyerUrl = stringResource(R.string.url_pgyer)
    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.Hive,
        title = stringResource(id = StringsR.string.label_contribute)
    ) {
        Option(
            shape = OptionShapes.firstShape(),
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Crowdin,
                contentDescription = stringResource(id = StringsR.string.label_contribute),
            ),
            title = stringResource(StringsR.string.label_translation),
            desc = translationUrl,
            onClick = {
                uriHandler.openUri(translationUrl)
            }
        )
        Option(
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.Coffee,
                contentDescription = stringResource(StringsR.string.label_donate),
            ),
            title = stringResource(StringsR.string.label_donate),
            onClick = {
                uriHandler.openUri(sponsorUrl)
            }
        )
        Option(
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.Experiment,
                contentDescription = stringResource(StringsR.string.label_experiment),
            ),
            title = stringResource(StringsR.string.label_experiment),
            desc = pgyerUrl,
            onClick = {
                uriHandler.openUri(pgyerUrl)
            },
            shape = OptionShapes.lastShape(),
        )
    }
}
