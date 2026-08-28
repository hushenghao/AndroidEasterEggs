@file:OptIn(ExperimentalMaterial3Api::class)

package com.android_next.egg

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.alterable_adaptive_icon.AlterableAdaptiveIcon
import com.dede.android_eggs.composable.ScrollableModalBottomSheet
import com.dede.android_eggs.navigation.OverlayContentProvider
import com.dede.android_eggs.navigation.OverlayRoute
import com.dede.android_eggs.views.settings.compose.prefs.IconShapePrefUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.coroutines.launch
import java.util.Calendar

object AndroidNextTimelineRoute : OverlayRoute

@Module
@InstallIn(SingletonComponent::class)
object AndroidNextOverlayProvider : OverlayContentProvider {

    override val route: OverlayRoute = AndroidNextTimelineRoute

    @Provides
    @IntoSet
    fun provide(): OverlayContentProvider = this

    @Composable
    override fun Content(onDismiss: () -> Unit) {
        AndroidNextTimelineDialog(onDismiss = onDismiss)
    }
}

@Composable
fun AndroidNextTimelineDialog(
    @DrawableRes logoRes: Int = R.drawable.ic_droid_logo,
    @StringRes titleRes: Int = R.string.nickname_android_next,
    onDismiss: () -> Unit = {},
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val androidReleasesUrl = stringResource(R.string.url_android_releases)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
    )
    val paddingValues = WindowInsets.safeDrawing.asPaddingValues()

    ScrollableModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        scrollState = scrollState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = scrollState)
                .padding(horizontal = 24.dp)
                .padding(bottom = 12.dp + paddingValues.calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AlterableAdaptiveIcon(
                    modifier = Modifier.size(42.dp),
                    clipShape = IconShapePrefUtil.getIconShape(),
                    res = logoRes,
                )
                Text(
                    text = stringResource(id = titleRes),
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            Text(
                text = getTimelineMessage(context),
                style = MaterialTheme.typography.bodyMedium
            )

            AndroidScheduleArtist(
                betaReleaseMonth = AndroidNextEasterEgg.BETA_RELEASE_MONTH,
                platformStabilityMonth = AndroidNextEasterEgg.PLATFORM_STABILITY_MONTH,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = {
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                }) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
                TextButton(onClick = {
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                    uriHandler.openUri(androidReleasesUrl)
                }) {
                    Text(text = stringResource(id = R.string.label_timeline_releases))
                }
            }
        }
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
