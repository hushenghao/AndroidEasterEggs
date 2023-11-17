package com.dede.android_eggs.views.main.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.edit
import androidx.core.net.toUri
import com.dede.android_eggs.R
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.pref
import com.dede.android_eggs.views.theme.blend
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

private const val KEY = "key_welcome_status"

@Composable
@Preview
fun Welcome() {
    val context = LocalContext.current
    var visible by remember {
        mutableStateOf(!context.pref.getBoolean(KEY, false))
//        mutableStateOf(true)
    }
    if (!visible) {
        return
    }
    val konfettiState = LocalKonfettiState.currentOutInspectionMode
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
        onDismissRequest = {
            visible = false
        },
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
                    visible = false
                }) {
                    Text(text = stringResource(android.R.string.cancel))
                }
                TextButton(onClick = {
                    visible = false
                    context.pref.edit {
                        putBoolean(KEY, true)
                    }
                    konfettiState?.value = true
                }) {
                    Text(text = stringResource(R.string.action_agree))
                }
            }
        },
    )
}