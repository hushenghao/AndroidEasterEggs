package com.dede.android_eggs.views.main.compose

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.dede.android_eggs.R
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.ui.composes.LoopHorizontalPager
import com.dede.android_eggs.ui.composes.LoopPagerIndicator
import com.dede.android_eggs.ui.composes.rememberLoopPagerState
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.pref
import com.dede.android_eggs.views.settings.compose.basic.rememberPrefBoolState
import com.dede.android_eggs.resources.R as StringsR

private const val KEY = "key_welcome_status"

fun isAgreedPrivacyPolicy(context: Context): Boolean {
    return context.pref.getBoolean(KEY, false)
}

private val pagers = intArrayOf(
    R.drawable.img_android_ai_tools_hero,
    R.drawable.img_compose_cluster,
    R.drawable.img_build_apps,
    R.drawable.img_launch_app,
    R.drawable.img_billions,
    R.drawable.img_better_together_hero,
    R.drawable.img_controllers,
    R.drawable.img_android_studio,
    R.drawable.img_samples,
)

object WelcomeDialog : EasterEggsDestination {
    override val route: String = "welcome_dialog"
}

@Preview
@Composable
fun WelcomeDialog(onDismiss: () -> Unit = {}) {
    var prefShowed by rememberPrefBoolState(KEY, false)
    if (prefShowed) {
        onDismiss()
        return
    }

    LaunchedEffect(Unit) {
        pagers.shuffle()
    }
    val context = LocalContext.current
    var konfettiState by LocalKonfettiState.current
    AlertDialog(
        title = {
            Text(text = stringResource(StringsR.string.label_welcome))
        },
        text = {
            Column {
                val pagerState = rememberLoopPagerState { pagers.size }
                LoopHorizontalPager(
                    state = pagerState,
                    interval = 1500L,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(5 / 4f),
                ) {
                    Image(
                        painter = painterResource(pagers[it]),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                LoopPagerIndicator(
                    state = pagerState,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(
                            colorScheme.surfaceColorAtElevation(2.dp),
                            RoundedCornerShape(50f)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
                Text(
                    text = stringResource(StringsR.string.summary_browse_privacy_policy),
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
