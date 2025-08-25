package com.dede.android_eggs.views.settings.compose.groups

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.R
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.android_eggs.views.settings.compose.basic.Option
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock
import com.dede.basic.copy
import com.dede.android_eggs.resources.R as StringsR

@Composable
fun ContactMeGroup() {
    val context = LocalContext.current

    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.PersonSearch,
        title = stringResource(StringsR.string.label_contact_me)
    ) {
        Option(
            shape = OptionShapes.firstShape(),
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.AlternateEmail,
                contentDescription = stringResource(id = StringsR.string.label_email_title)
            ),
            title = stringResource(id = StringsR.string.label_email_title),
            desc = stringResource(id = R.string.label_mail_me),
            onClick = {
                context.copy(context.getString(R.string.label_mail_me))
            }
        )
        Option(
            leadingIcon = imageVectorIconBlock(
                imageVector = Icons.Rounded.BugReport,
                contentDescription = stringResource(StringsR.string.label_feedback),
            ),
            title = stringResource(StringsR.string.label_feedback),
            onClick = {
                CustomTabsBrowser.launchUrl(context, R.string.url_github_issues)
            }
        )
    }
}