package com.dede.android_eggs.views.main.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.dede.android_eggs.R
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.views.settings.compose.rememberPrefBoolState

private const val KEY = "key_welcome_status"

@Composable
@Preview
fun Welcome() {
    var visible by rememberSaveable { mutableStateOf(true) }
    if (!visible) {
        return
    }
    var prefShowed by rememberPrefBoolState(KEY, false)
    if (prefShowed) {
        return
    }
    val context = LocalContext.current
    var konfettiState by LocalKonfettiState.current
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.label_welcome))
        },
        text = {
            Column {
                Image(
                    painter = painterResource(R.drawable.better_together_hero),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.summary_browse_privacy_policy),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                FilledTonalButton(
                    modifier = Modifier.align(Alignment.End),
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    onClick = {
                        CustomTabsBrowser.launchUrl(
                            context, context.getString(R.string.url_privacy).toUri()
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PrivacyTip,
                        contentDescription = stringResource(R.string.label_privacy_policy)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = stringResource(R.string.label_privacy_policy))
                }
            }
        },
        onDismissRequest = {
            visible = false
        },
        dismissButton = {
            TextButton(onClick = { visible = false }) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                visible = false
                prefShowed = true
                konfettiState = true
            }) {
                Text(text = stringResource(R.string.action_agree))
            }
        },
    )
}