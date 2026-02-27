package com.android_next.egg

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.dede.android_eggs.alterable_adaptive_icon.AlterableAdaptiveIcon
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.views.settings.compose.prefs.IconShapePrefUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import java.util.Calendar

@Module
@InstallIn(SingletonComponent::class)
object AndroidNextTimelineDialog : EasterEggsDestination, EasterEggsDestination.Provider {
    override val type: EasterEggsDestination.Type = EasterEggsDestination.Type.Dialog

    override val route: NavKey = EasterEggsDestination.AndroidNextTimelineDialog

    @Composable
    override fun Content(properties: EasterEggsDestination.DestinationProps) {
        AndroidNextTimelineDialog(onDismiss = properties.onBack)
    }

    @Provides
    @IntoSet
    override fun provider(): EasterEggsDestination = this
}

@Composable
fun AndroidNextTimelineDialog(
    @DrawableRes logoRes: Int = R.drawable.ic_droid_logo,
    @StringRes titleRes: Int = R.string.nickname_android_next,
    onDismiss: () -> Unit = {},
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AlterableAdaptiveIcon(
                    modifier = Modifier.size(42.dp),
                    clipShape = IconShapePrefUtil.getIconShape(),
                    res = logoRes,
                )
                Text(text = stringResource(id = titleRes))
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = getTimelineMessage(context),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(id = R.string.label_timeline_title),
                    style = MaterialTheme.typography.titleMedium,
                )
                Android17Schedule()
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                CustomTabsBrowser.launchUrl(context, R.string.url_android_releases)
            }) {
                Text(text = stringResource(id = R.string.label_timeline_releases))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
    )
}

@Preview
@Composable
private fun Android17Schedule() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Image(
            painter = painterResource(id = R.drawable.android_17_schedule),
            contentDescription = null,
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .height(160.dp)
                .aspectRatio(949f / 396),
        )
    }
}

private fun getTimelineMessage(context: Context): String {
    val nowDate = Calendar.getInstance().setDateZero()
    val releaseDate = getReleaseDate()
    return if (nowDate.after(releaseDate)) {
        context.getString(R.string.summary_android_release_pushed)
    } else {
        context.getString(R.string.summary_android_waiting)
    }
}
