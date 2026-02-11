@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.ViewCarousel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselDefaults.multiBrowseFlingBehavior
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavKey
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.navigation.LocalNavigator
import com.dede.android_eggs.ui.composes.PHI
import com.dede.android_eggs.ui.composes.SnapshotView
import com.dede.android_eggs.views.settings.compose.basic.SettingPref
import com.dede.basic.provider.EasterEgg
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import javax.inject.Inject
import com.dede.android_eggs.resources.R as StringR

@Composable
fun SnapshotPref() {
    val navigator = LocalNavigator.current
    SettingPref(
        title = stringResource(StringR.string.label_snapshot_preview),
        leadingIcon = Icons.Rounded.ViewCarousel,
        trailingContent = Icons.AutoMirrored.Rounded.NavigateNext,
        onClick = {
            navigator.navigate(EasterEggsDestination.SnapshotDialog)
        }
    )
}

@Module
@InstallIn(SingletonComponent::class)
object SnapshotDialog : EasterEggsDestination, EasterEggsDestination.Provider {
    override val type: EasterEggsDestination.Type = EasterEggsDestination.Type.Dialog

    override val route: NavKey = EasterEggsDestination.SnapshotDialog

    @Composable
    override fun Content(properties: EasterEggsDestination.DestinationProps) {
        SnapshotDialog(onDismiss = properties.onBack)
    }

    @IntoSet
    @Provides
    override fun provider(): EasterEggsDestination = this
}

@HiltViewModel
class SnapshotViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var easterEggs: List<@JvmSuppressWildcards EasterEgg>
}

@Composable
fun SnapshotDialogView(
    modifier: Modifier = Modifier,
    viewModel: SnapshotViewModel = hiltViewModel(),
    showEasterEggName: Boolean = true,
) {
    val pairList = remember(viewModel) {
        buildList {
            for (egg in viewModel.easterEggs) {
                val snapshot = egg.provideSnapshotProvider() ?: continue
                add(snapshot to egg)
            }
        }
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        val carouselState = rememberCarouselState { pairList.size }
        val hapticFeedback = LocalHapticFeedback.current
        LaunchedEffect(carouselState) {
            snapshotFlow { carouselState.currentItem }
                .distinctUntilChangedIgnoreInitializeValue(0)
                .collect {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                }
        }

        HorizontalCenteredHeroCarousel(
            state = carouselState,
            flingBehavior = multiBrowseFlingBehavior(carouselState),
            itemSpacing = 6.dp,
            minSmallItemWidth = 34.dp,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(PHI),
        ) { i ->
            SnapshotView(
                modifier = Modifier
                    .fillMaxSize()
                    .maskClip(MaterialTheme.shapes.extraLarge),
                snapshot = pairList[i].first,
            )
        }
        if (showEasterEggName) {
            Text(
                modifier = Modifier,
                text = stringResource(pairList[carouselState.currentItem].second.nameRes),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview
@Composable
private fun SnapshotDialog(onDismiss: () -> Unit = {}) {
    Dialog(onDismissRequest = onDismiss) {
        SnapshotDialogView()
    }
}


fun <T> Flow<T>.distinctUntilChangedIgnoreInitializeValue(initializeValue: T): Flow<T> {
    var saveValue: T? = initializeValue
    return distinctUntilChanged()
        .filter {
            if (saveValue != null && it == saveValue) {
                saveValue = null
                return@filter false
            }
            true
        }
}
