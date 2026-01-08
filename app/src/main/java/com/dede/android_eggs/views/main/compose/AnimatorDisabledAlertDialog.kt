package com.dede.android_eggs.views.main.compose

import android.content.Context
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
import androidx.core.content.edit
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.navigation.LocalNavController
import com.dede.android_eggs.util.pref
import com.dede.basic.Utils
import com.dede.basic.toast
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import com.dede.android_eggs.resources.R as StringsR

@Module
@InstallIn(SingletonComponent::class)
object AnimatorDisabledAlertDialog : EasterEggsDestination, EasterEggsDestination.Provider {

    override val type: EasterEggsDestination.Type = EasterEggsDestination.Type.Dialog

    override val route: String = "animator_disabled_alert_dialog"

    private const val PREF_DONT_SHOW_AGAIN = "animator_disabled_alert_dialog_dont_show_again"

    fun isDontShowAgain(context: Context): Boolean {
        return context.pref.getBoolean(PREF_DONT_SHOW_AGAIN, false)
    }

    fun setDontShowAgain(context: Context) {
        context.pref.edit { putBoolean(PREF_DONT_SHOW_AGAIN, true) }
    }

    @Composable
    override fun Content() {
        val navController = LocalNavController.current
        AnimatorDisabledAlertDialog {
            navController.popBackStack()
        }
    }

    @Provides
    @IntoSet
    override fun provider(): EasterEggsDestination = this
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
            TextButton(onClick = {
                AnimatorDisabledAlertDialog.setDontShowAgain(context)
                onDismiss()
            }) {
                Text(text = stringResource(StringsR.string.action_dont_show_again))
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
