package com.dede.android_eggs.views.settings.compose.groups

import android.content.ClipData
import android.util.Base64
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.R
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.android_eggs.views.settings.compose.basic.Option
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock
import com.dede.basic.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
            desc = FuckRobotHarass.oG0vY4xD,
            onClick = copyClick(FuckRobotHarass.oG0vY4xD)
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

@Composable
fun copyClick(
    text: String,
    scope: CoroutineScope = rememberCoroutineScope(),
    toast: Boolean = true
): () -> Unit {
    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    return {
        scope.launch {
            val clipEntry = ClipData.newPlainText(null, text).toClipEntry()
            clipboard.setClipEntry(clipEntry)
            if (toast) {
                context.toast(android.R.string.copy)
            }
        }
    }
}


private object FuckRobotHarass {
    private val sN0rN9sA =
        byteArrayOf(
            90, 71, 86, 107, 90, 83, 53, 111, 100, 85,
            66, 120, 99, 83, 53, 106, 98, 50, 48, 61
        )

    val oG0vY4xD: String
        get() = String(Base64.decode(sN0rN9sA, Base64.DEFAULT))
}
