package com.dede.android_eggs.views.main.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.util.toast
import com.dede.basic.Utils


@Composable
@Preview
fun AnimatorDisabledAlertDialog(
    state: MutableState<Boolean> = remember { mutableStateOf(true) }
) {
    var visible by state

    if (!visible) {
        return
    }

    val context = LocalContext.current
    AlertDialog(
        title = {
            Text(
                text = stringResource(android.R.string.dialog_alert_title),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(6.dp)
                    .fillMaxWidth()
            )
        },
        text = {
            Column {
                Text(text = stringResource(R.string.animator_disabled_alert_message))
            }
        },
        dismissButton = {
            TextButton(onClick = { visible = false }) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val intent = Utils.getDevOptsAnimatorDurationScaleIntent()
                try {
                    context.startActivity(intent)
                } catch (ignore: Exception) {
                    context.toast("Open failure!")
                }
                visible = false
            }) {
                Text(text = stringResource(R.string.action_goto_settings))
            }
        },
        onDismissRequest = {
            visible = false
        },
    )

}
