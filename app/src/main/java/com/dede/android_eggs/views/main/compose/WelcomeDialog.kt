package com.dede.android_eggs.views.main.compose

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.util.pref
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil.KEY_PRIVACY_POLICY_AGREED
import com.dede.android_eggs.views.settings.compose.basic.rememberPrefBoolState
import com.dede.android_eggs.views.settings.compose.prefs.SnapshotDialogView
import com.dede.android_eggs.resources.R as StringsR

fun isAgreedPrivacyPolicy(context: Context): Boolean {
    return context.pref.getBoolean(KEY_PRIVACY_POLICY_AGREED, false)
}

@Preview
@Composable
fun WelcomeDialog(onDismiss: () -> Unit = {}) {
    var privacyPolicyAgreed by rememberPrefBoolState(KEY_PRIVACY_POLICY_AGREED, false)
    val uriHandler = LocalUriHandler.current
    val privacyUrl = stringResource(R.string.url_privacy)
    val konfettiController = LocalKonfettiState.current
    AlertDialog(
        title = {
            Text(text = stringResource(StringsR.string.label_welcome))
        },
        text = {
            Column {
                SnapshotDialogView(showEasterEggName = false, carouselFeedback = false)
                Text(
                    text = stringResource(StringsR.string.summary_browse_privacy_policy),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                FilledTonalButton(
                    modifier = Modifier.align(Alignment.End),
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    onClick = {
                        uriHandler.openUri(privacyUrl)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PrivacyTip,
                        contentDescription = stringResource(StringsR.string.label_privacy_policy)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = stringResource(StringsR.string.label_privacy_policy))
                }
            }
        },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                privacyPolicyAgreed = true
                konfettiController.trigger()
                onDismiss()
            }) {
                Text(text = stringResource(StringsR.string.action_agree))
            }
        },
    )
}
