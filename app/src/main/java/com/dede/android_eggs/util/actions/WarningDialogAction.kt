package com.dede.android_eggs.util.actions

import android.app.Activity
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.HtmlCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.dede.android_eggs.R
import com.dede.android_eggs.util.ActivityActionDispatcher
import com.dede.android_eggs.views.theme.EasterEggsTheme
import com.dede.basic.androidLifecycleOwner
import com.dede.basic.androidSavedStateOwner
import com.dede.basic.getBoolean
import com.dede.basic.putBoolean

class WarningDialogAction : ActivityActionDispatcher.ActivityAction {

    private class WarningInfo(
        val key: String,
        @StringRes val title: Int,
        @StringRes val message: Int,
    )

    companion object {
        private val target = mapOf(
            com.android_t.egg.PlatLogoActivity::class to WarningInfo(
                "key_t_trypophobia_warning",
                android.R.string.dialog_alert_title,
                R.string.message_trypophobia_warning
            ),
            com.android_s.egg.PlatLogoActivity::class to WarningInfo(
                "key_s_trypophobia_warning",
                android.R.string.dialog_alert_title,
                R.string.message_trypophobia_warning
            ),
        )
    }

    override fun onCreate(activity: Activity) {
        val info = target[activity.javaClass.kotlin] ?: return
        if (activity.getBoolean(info.key, false)) return

        val composeView = ComposeView(activity)
        composeView.setViewTreeLifecycleOwner(activity.androidLifecycleOwner)
        // composeView.setViewTreeViewModelStoreOwner()
        composeView.setViewTreeSavedStateRegistryOwner(activity.androidSavedStateOwner)
        activity.window.decorView.post {
            activity.addContentView(
                composeView,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }
        composeView.setContent {
            EasterEggsTheme {
                val context = LocalContext.current
                WarningDialog(
                    info.title,
                    info.message,
                    onConfirm = {
                        context.putBoolean(info.key, true)
                    },
                    onCancel = {
                        @Suppress("DEPRECATION")
                        activity.onBackPressed()
                    },
                )
            }
        }
    }

}

@Composable
private fun WarningDialog(
    @StringRes title: Int,
    @StringRes message: Int,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }
    if (!visible) {
        return
    }
    AlertDialog(
        onDismissRequest = {
        },
        properties = remember {
            DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.WarningAmber,
                    contentDescription = stringResource(id = title),
                    modifier = Modifier.size(30.dp)
                )
                Text(text = stringResource(id = title))
            }
        },
        text = {
            val messageText = stringResource(message)
            val messageSpanned = remember(messageText) {
                HtmlCompat.fromHtml(messageText, HtmlCompat.FROM_HTML_MODE_COMPACT)
            }
            val textStyle = MaterialTheme.typography.bodyMedium
            val textColor = MaterialTheme.colorScheme.onSurfaceVariant
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = {
                    AppCompatTextView(it)
                },
                update = {
                    it.text = messageSpanned
                    it.setTextColor(textColor.toArgb())
                    it.textSize = textStyle.fontSize.value
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    visible = false
                    onConfirm()
                }
            ) {
                Text(text = stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    visible = false
                    onCancel()
                }
            ) {
                Text(text = stringResource(android.R.string.cancel))
            }
        }
    )
}
