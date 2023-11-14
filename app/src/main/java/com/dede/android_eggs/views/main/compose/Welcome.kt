package com.dede.android_eggs.views.main.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.edit
import androidx.core.net.toUri
import com.dede.android_eggs.R
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.pref

private const val KEY = "key_welcome_status"

@Composable
@Preview
fun Welcome() {
    val context = LocalContext.current
    val pref = remember { context.pref }
    var visiable by remember {
        mutableStateOf(!pref.getBoolean(KEY, false))
    }
    if (!visiable) {
        return
    }
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.label_welcome))
        },
        text = {
            Column {
                Text(text = stringResource(R.string.summary_browse_privacy_policy))
                Image(
                    painter = painterResource(R.drawable.img_welcome_poster),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        onDismissRequest = {},
        confirmButton = {
            Row {
                TextButton(onClick = {
                    CustomTabsBrowser.launchUrl(
                        context,
                        context.getString(R.string.url_privacy).toUri()
                    )
                }) {
                    Text(text = stringResource(R.string.label_privacy_policy))
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = {
                    visiable = false
                }) {
                    Text(text = stringResource(android.R.string.cancel))
                }
                TextButton(onClick = {
                    visiable = false
                    pref.edit {
                        putBoolean(KEY, true)
                    }
                }) {
                    Text(text = stringResource(R.string.action_agree))
                }
            }
        },
    )
}