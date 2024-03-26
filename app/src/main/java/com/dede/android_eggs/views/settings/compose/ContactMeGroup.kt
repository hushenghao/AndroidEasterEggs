package com.dede.android_eggs.views.settings.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.composes.icons.Github
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.copy

@Composable
fun ContactMeGroup() {
    val context = LocalContext.current

    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.PersonSearch,
        title = stringResource(R.string.label_contact_me)
    ) {
        Option(
            shape = OptionShapes.firstShape(),
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.AlternateEmail,
                contentDescription = stringResource(id = R.string.label_email_title)
            ),
            title = stringResource(id = R.string.label_email_title),
            desc = stringResource(id = R.string.label_mail_me),
            onClick = {
                context.copy(context.getString(R.string.label_mail_me))
            }
        )
        Option(
            shape = OptionShapes.lastShape(),
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Github,
                contentDescription = stringResource(R.string.label_github)
            ),
            title = "GitHub",
            desc = stringResource(id = R.string.label_github_me),
            onClick = {
                CustomTabsBrowser.launchUrl(context, R.string.url_github_me)
            }
        )
    }
}