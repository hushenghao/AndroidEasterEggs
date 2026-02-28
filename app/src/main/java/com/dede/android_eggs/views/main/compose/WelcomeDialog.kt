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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.dede.android_eggs.R
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.pref
import com.dede.android_eggs.views.settings.compose.basic.rememberPrefBoolState
import com.dede.android_eggs.views.settings.compose.prefs.SnapshotDialogView
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import com.dede.android_eggs.resources.R as StringsR

private const val KEY = "key_welcome_status"

fun isAgreedPrivacyPolicy(context: Context): Boolean {
    return context.pref.getBoolean(KEY, false)
}

@Module
@InstallIn(SingletonComponent::class)
object WelcomeDialog : EasterEggsDestination, EasterEggsDestination.Provider {

    override val type: EasterEggsDestination.Type = EasterEggsDestination.Type.Dialog

    override val route: NavKey = EasterEggsDestination.WelcomeDialog

    @Composable
    override fun Content(properties: EasterEggsDestination.DestinationProps) {
        WelcomeDialog(onDismiss = properties.onBack)
    }

    @Provides
    @IntoSet
    override fun provider(): EasterEggsDestination = this
}

@Preview
@Composable
fun WelcomeDialog(onDismiss: () -> Unit = {}) {
    var prefShowed by rememberPrefBoolState(KEY, false)
    val context = LocalContext.current
    var konfettiState by LocalKonfettiState.current
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
                        CustomTabsBrowser.launchUrl(context, R.string.url_privacy)
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
                prefShowed = true
                konfettiState = true
                onDismiss()
            }) {
                Text(text = stringResource(StringsR.string.action_agree))
            }
        },
    )
}
