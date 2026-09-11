@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.main.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation3.runtime.NavKey
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.dede.android_eggs.composable.WindowWidthPane
import com.dede.android_eggs.composable.appbar.HazeScaffold
import com.dede.android_eggs.composable.appbar.HazeScaffoldDefaults.hazeAppBar
import com.dede.android_eggs.composable.appbar.HazeScaffoldDefaults.hazeBottomBar
import com.dede.android_eggs.composable.currentWindowWidthPane
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.ui.composes.ReverseDismissibleNavigationDrawer
import com.dede.android_eggs.ui.composes.ReverseModalNavigationDrawer
import com.dede.android_eggs.ui.composes.ReversePermanentNavigationDrawer
import com.dede.android_eggs.util.OrientationAngleSensor
import com.dede.android_eggs.util.compose.only
import com.dede.android_eggs.views.main.util.EasterEggLogoSensorMatrixConvert
import com.dede.android_eggs.views.settings.SettingsContent
import com.dede.android_eggs.views.settings.SettingsScreen
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.IconVisualEffectsPrefUtil
import com.dede.basic.provider.BaseEasterEgg
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
object EasterEggsScreen : EasterEggsDestination, EasterEggsDestination.Provider {
    override val route: NavKey = EasterEggsDestination.EasterEggs

    @Composable
    override fun Content() {
        EasterEggScreen()
    }

    @IntoSet
    @Provides
    override fun provider(): EasterEggsDestination = this
}

@HiltViewModel
class EasterEggViewModel @Inject constructor(
    val easterEggs: List<@JvmSuppressWildcards BaseEasterEgg>,
) : ViewModel()

@Composable
fun EasterEggScreen(
    viewModel: EasterEggViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val logoSensor = remember { EasterEggLogoSensorMatrixConvert() }
    if (IconVisualEffectsPrefUtil.isSupported()) {
        val lifecycleOwner = LocalLifecycleOwner.current
        val iconVisualEffectsEnabled =
            SettingPrefUtil.iconVisualEffectsState.intValue == SettingPrefUtil.ON
        DisposableEffect(iconVisualEffectsEnabled) {
            var orientationAngleSensor: OrientationAngleSensor? = null
            if (iconVisualEffectsEnabled) {
                orientationAngleSensor = OrientationAngleSensor(
                    context, lifecycleOwner, logoSensor
                )
            }
            onDispose {
                orientationAngleSensor?.destroy()
                orientationAngleSensor = null
            }
        }
    }

    val konfettiController = LocalKonfettiState.current
    CompositionLocalProvider(
        LocalEasterEggLogoSensor provides logoSensor,
    ) {
        when (currentWindowWidthPane()) {
            WindowWidthPane.COMPACT -> {
                EggScreenScaffoldCompact(viewModel)
            }
            WindowWidthPane.MEDIUM -> {
                EggScreenScaffoldMedium(viewModel)
            }
            WindowWidthPane.EXPANDED -> {
                EggScreenScaffoldExpanded(viewModel)
            }
        }

        Konfetti(
            visible = konfettiController.visible,
            onFinished = konfettiController::dismiss
        )
    }
}

@Composable
private fun EggScreenScaffoldCompact(viewModel: EasterEggViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    ReverseModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = MaterialTheme.shapes.extraLarge.copy(
                    topEnd = CornerSize(0.dp),
                    bottomEnd = CornerSize(0.dp),
                ),
                windowInsets = WindowInsets(0, 0, 0, 0),
            ) {
                Box(modifier = Modifier.sizeIn(maxWidth = MaxDrawerWidth)) {
                    SettingsScreen(drawerState)
                }
            }
            CloseDrawerOnBack(drawerState)
        },
        drawerState = drawerState,
    ) {
        val searchBarState = rememberBottomSearchBarState()
        EggScreenScaffold(
            drawerState = drawerState,
            searchBarState = searchBarState,
        ) { contentPadding ->
            EasterEggList(
                easterEggs = viewModel.easterEggs,
                searchText = searchBarState.searchText,
                contentPadding = contentPadding,
            )
        }
    }
}

private val MaxDrawerWidth = 300.dp

@Composable
private fun EggScreenScaffoldMedium(viewModel: EasterEggViewModel) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val searchBarState = rememberBottomSearchBarState()
    EggScreenScaffold(
        drawerState = drawerState,
        searchBarState = searchBarState,
    ) { contentPadding ->
        ReverseDismissibleNavigationDrawer(
            drawerContent = {
                DismissibleDrawerSheet(
                    drawerState = drawerState,
                    windowInsets = WindowInsets(0, 0, 0, 0),
                ) {
                    SettingsContent(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .sizeIn(maxWidth = MaxDrawerWidth),
                        contentPadding = contentPadding.only(WindowInsetsSides.Vertical + WindowInsetsSides.End),
                    )
                }
                CloseDrawerOnBack(drawerState)
            },
            drawerState = drawerState,
        ) {
            EasterEggList(
                easterEggs = viewModel.easterEggs,
                searchText = searchBarState.searchText,
                contentPadding = contentPadding.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start),
            )
        }
    }
}

@Composable
private fun EggScreenScaffoldExpanded(viewModel: EasterEggViewModel) {
    val searchBarState = rememberBottomSearchBarState()
    EggScreenScaffold(
        drawerState = null,
        showSettingsAction = false,
        searchBarState = searchBarState,
    ) { contentPadding ->
        ReversePermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet(
                    windowInsets = WindowInsets(0, 0, 0, 0)
                ) {
                    SettingsContent(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .sizeIn(maxWidth = MaxDrawerWidth),
                        contentPadding = contentPadding.only(WindowInsetsSides.Vertical + WindowInsetsSides.End),
                    )
                }
            },
        ) {
            EasterEggList(
                easterEggs = viewModel.easterEggs,
                searchText = searchBarState.searchText,
                contentPadding = contentPadding.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start),
            )
        }
    }
}

@Composable
private fun EggScreenScaffold(
    searchBarState: BottomSearchBarState,
    drawerState: DrawerState? = null,
    showSettingsAction: Boolean = true,
    content: @Composable (contentPadding: PaddingValues) -> Unit,
) {
    val hazeState = rememberHazeState()
    HazeScaffold(
        hazeState = hazeState,
        topBar = {
            MainTitleBar(
                modifier = Modifier.hazeAppBar(hazeState),
                searchBarState = searchBarState,
                drawerState = drawerState,
                showSettingsAction = showSettingsAction,
            )
        },
        bottomBar = {
            BottomSearchBar(
                modifier = Modifier.hazeBottomBar(hazeState),
                elevation = 0.dp,
                containerColor = Color.Transparent,
                state = searchBarState,
            )
        },
    ) { contentPadding ->
        content(contentPadding)
    }
}

/**
 * Closes the navigation drawer on system back while it is open.
 *
 * Uses the navigationevent [NavigationBackHandler] (not the legacy
 * `BackHandler`) so it takes priority over [androidx.navigation3.ui.NavDisplay]'s
 * own back handling, which would otherwise swallow the event.
 */
@Composable
private fun CloseDrawerOnBack(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    val state = rememberNavigationEventState(NavigationEventInfo.None)
    NavigationBackHandler(
        state = state,
        isBackEnabled = drawerState.targetValue == DrawerValue.Open,
        onBackCompleted = { scope.launch { drawerState.close() } },
    )
}
