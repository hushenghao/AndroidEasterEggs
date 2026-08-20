@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.main.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation3.runtime.NavKey
import com.dede.android_eggs.composable.appbar.HazeScaffold
import com.dede.android_eggs.composable.appbar.HazeScaffoldDefaults.hazeAppBarModifier
import com.dede.android_eggs.composable.appbar.HazeScaffoldDefaults.hazeBottomBarModifier
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.ui.composes.ReverseDismissibleNavigationDrawer
import com.dede.android_eggs.util.OrientationAngleSensor
import com.dede.android_eggs.views.main.util.EasterEggLogoSensorMatrixConvert
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
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        ReverseDismissibleNavigationDrawer(
            drawerContent = {
                DismissibleDrawerSheet(
                    drawerState = drawerState,
                    windowInsets = WindowInsets(0, 0, 0, 0),
                ) {
                    val maxWidth = LocalConfiguration.current.smallestScreenWidthDp * 0.8f
                    Box(
                        modifier = Modifier.width(maxWidth.dp),
                    ) {
                        SettingsScreen(drawerState)
                    }
                }
            },
            drawerState = drawerState,
        ) {
            val searchBarState = rememberBottomSearchBarState()
            val hazeState = rememberHazeState()
            HazeScaffold(
                hazeState = hazeState,
                topBar = {
                    MainTitleBar(
                        modifier = Modifier.hazeAppBarModifier(hazeState),
                        searchBarState = searchBarState,
                        drawerState = drawerState,
                    )
                },
                bottomBar = {
                    BottomSearchBar(
                        modifier = Modifier.hazeBottomBarModifier(hazeState),
                        elevation = 0.dp,
                        containerColor = Color.Transparent,
                        state = searchBarState,
                    )
                },
            ) { contentPadding ->
                EasterEggList(
                    easterEggs = viewModel.easterEggs,
                    searchText = searchBarState.searchText,
                    contentPadding = contentPadding,
                )
            }
        }

        Konfetti(
            visible = konfettiController.visible,
            onFinished = konfettiController::dismiss
        )
    }
}
