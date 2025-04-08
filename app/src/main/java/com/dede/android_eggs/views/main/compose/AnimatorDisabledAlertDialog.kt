package com.dede.android_eggs.views.main.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.basic.Utils
import com.dede.basic.toast
import com.dede.android_eggs.resources.R as StringsR


object AnimatorDisabledAlertDialog : EasterEggsDestination {
    override val route: String = "animator_disabled_alert_dialog"
}

@Composable
@Preview
fun AnimatorDisabledAlertDialog(onDismiss: () -> Unit = {}) {
    val context = LocalContext.current
    AlertDialog(
        title = {
            Text(
                text = stringResource(android.R.string.dialog_alert_title),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxWidth()
            )
        },
        text = {
            Column {
                Text(text = stringResource(StringsR.string.animator_disabled_alert_message))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
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
                onDismiss()
            }) {
                Text(text = stringResource(StringsR.string.action_goto_settings))
            }
        },
        onDismissRequest = onDismiss,
    )

}
