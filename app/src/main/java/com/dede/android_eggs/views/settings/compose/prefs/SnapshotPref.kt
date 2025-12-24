@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.ViewCarousel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.navigation.LocalNavController
import com.dede.android_eggs.ui.composes.SnapshotView
import com.dede.android_eggs.views.settings.compose.basic.SettingPref
import com.dede.basic.provider.EasterEgg
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.dede.android_eggs.resources.R as StringR

@Composable
fun SnapshotPref() {
    val navHostController = LocalNavController.current
    SettingPref(
        title = stringResource(StringR.string.label_snapshot_preview),
        leadingIcon = Icons.Rounded.ViewCarousel,
        trailingContent = Icons.AutoMirrored.Rounded.NavigateNext,
        onClick = {
            navHostController.navigate(SnapshotDialog.route)
        }
    )
}

object SnapshotDialog : EasterEggsDestination {
    override val route: String = "snapshot_dialog"
}

@HiltViewModel
class SnapshotViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var easterEggs: List<@JvmSuppressWildcards EasterEgg>
}

@Preview
@Composable
fun SnapshotDialog(
    viewModel: SnapshotViewModel = hiltViewModel(),
    onDismiss: () -> Unit = {},
) {
    val snapshotProviders = remember(viewModel) {
        viewModel.easterEggs.mapNotNull { it.provideSnapshotProvider() }
    }
    Dialog(onDismissRequest = onDismiss) {
        val carouselState = rememberCarouselState { snapshotProviders.size }
        HorizontalCenteredHeroCarousel(
            state = carouselState,
            itemSpacing = 6.dp,
            minSmallItemWidth = 34.dp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) { i ->
            SnapshotView(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .maskClip(MaterialTheme.shapes.extraLarge),
                snapshot = snapshotProviders[i],
            )
        }
    }
}
