@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.main.compose

import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation3.runtime.NavKey
import com.dede.android_eggs.R
import com.dede.android_eggs.inject.EasterEggModules
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.ui.composes.PredictiveBackProgressHandler.predictiveBackShrink
import com.dede.android_eggs.ui.composes.ReverseModalNavigationDrawer
import com.dede.android_eggs.ui.composes.predictiveBackProgressState
import com.dede.android_eggs.util.OrientationAngleSensor
import com.dede.android_eggs.util.compose.end
import com.dede.android_eggs.util.compose.plus
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.android_eggs.views.main.util.EasterEggLogoSensorMatrixConvert
import com.dede.android_eggs.views.settings.SettingsScreen
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.IconVisualEffectsPrefUtil
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
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
        val iconVisualEffectsEnabled = SettingPrefUtil.iconVisualEffectsState.value
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

    val konfettiState = LocalKonfettiState.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    CompositionLocalProvider(
        LocalEasterEggLogoSensor provides logoSensor,
        LocalDrawerState provides drawerState,
    ) {
        ReverseModalNavigationDrawer(
            drawerContent = {
                val backProgress by predictiveBackProgressState(
                    enabled = drawerState.isOpen,
                    backEndValue = { 0f },
                ) {
                    drawerState.close()
                }
                val layoutDirection = LocalLayoutDirection.current
                ModalDrawerSheet(
                    modifier = Modifier
                        .graphicsLayer {
                            predictiveBackShrink(
                                progress = backProgress,
                                shrinkOrigin = Alignment.CenterEnd,
                                layoutDirection = layoutDirection
                            )
                        },
                    drawerShape = shapes.extraLarge.end(0.dp),
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
            drawerState = drawerState
        ) {
            val searchBarState = rememberBottomSearchBarState()
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            Scaffold(
                topBar = {
                    MainTitleBar(
                        scrollBehavior = scrollBehavior,
                        searchBarState = searchBarState,
                        drawerState = drawerState,
                    )
                },
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                bottomBar = {
                    BottomSearchBar(state = searchBarState)
                }
            ) { contentPadding ->
                EasterEggList(
                    easterEggs = viewModel.easterEggs,
                    searchText = searchBarState.searchText,
                    contentPadding = contentPadding,
                )
            }
        }

        Konfetti(konfettiState)
    }
}

private const val HIGHEST_COUNT = 1

private fun BaseEasterEgg.lazyItemKey(): String {
    if (apiLevelRange.first == apiLevelRange.last) {
        return "${this.javaClass.name}:${apiLevelRange.first}"
    }
    return "${this.javaClass.name}:${apiLevelRange.first}-${apiLevelRange.last}"
}

@Composable
@Preview(showBackground = true)
fun EasterEggList(
    modifier: Modifier = Modifier,
    easterEggs: List<BaseEasterEgg> = EasterEggHelp.previewEasterEggs(),
    searchText: String = "",
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val context = LocalContext.current
    val pureEasterEggs = remember(easterEggs) {
        EasterEggModules.providePureEasterEggList(easterEggs)
    }
    val searchMode = searchText.isNotBlank()
    val currentList = remember(searchText, searchMode, easterEggs, pureEasterEggs) {
        if (searchMode) {
            filterEasterEggs(context, pureEasterEggs, searchText)
        } else {
            easterEggs
        }
    }
    Box(
        modifier = Modifier
            .then(modifier)
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Crossfade(
            targetState = currentList.isEmpty(),
            modifier = Modifier.sizeIn(maxWidth = 560.dp),
            label = "EasterEggList",
        ) { isEmpty ->
            if (isEmpty) {
                SearchEmpty(contentPadding)
            } else {
                LazyColumn(
                    contentPadding = contentPadding + PaddingValues(vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (searchMode) {
                        items(
                            items = currentList,
                            key = BaseEasterEgg::lazyItemKey,
                        ) {
                            EasterEggItem(it, enableItemAnim = true)
                        }
                    } else {
                        val highestList = currentList.subList(0, HIGHEST_COUNT)
                        val normalList = currentList.subList(HIGHEST_COUNT, currentList.size)
                        items(
                            items = highestList,
                            key = BaseEasterEgg::lazyItemKey,
                        ) {
                            EasterEggHighestItem(it)
                        }
                        item("wavy") {
                            Wavy(
                                modifier = Modifier
                                    .fillMaxWidth(0.4f)
                                    .padding(vertical = 26.dp),
                            )
                        }
                        itemsIndexed(
                            items = normalList,
                            key = { _, item -> item.lazyItemKey() }
                        ) { index, item ->
                            EasterEggItem(base = item, enableItemAnim = false, index = index)
                        }
                        item("wavy") {
                            Wavy(
                                modifier = Modifier
                                    .fillMaxWidth(0.4f)
                                    .padding(vertical = 26.dp),
                            )
                        }
                        item("footer") {
                            ProjectDescription()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchEmpty(contentPadding: PaddingValues) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        DrawableImage(
            res = R.drawable.img_samples,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(0.6f)
        )
    }
}

private fun filterEasterEggs(
    context: Context,
    pureEasterEggs: List<EasterEgg>,
    searchText: String,
): List<EasterEgg> {

    fun EasterEgg.matchStringResNames(searchText: String): Boolean {
        return context.getString(nameRes).contains(searchText, true) ||
                context.getString(nicknameRes).contains(searchText, true)
    }

    fun EasterEgg.matchApiLevel(searchText: String): Boolean {
        val isApiLevel = Regex("^\\d{1,2}$").matches(searchText)
        if (isApiLevel) {
            val apiLevel = searchText.toIntOrNull() ?: return false
            return apiLevelRange.contains(apiLevel)
        }
        return false
    }

    fun EasterEgg.matchAndroidVersion(searchText: String): Boolean {
        val versionNameResult = Regex("[\\d.]{1,3}").find(searchText) ?: return false
        val versionNameValue = versionNameResult.value
        for (level in apiLevelRange) {
            val versionName = try {
                EasterEggHelp.getVersionNameByApiLevel(level)
            } catch (e: IllegalArgumentException) {
                // illegal api level, skip
                return false
            }
            if (versionName.startsWith(versionNameValue, true)) {
                return true
            }
        }
        return false
    }
    return pureEasterEggs.filter {
        it.matchStringResNames(searchText) ||
                it.matchApiLevel(searchText) ||
                it.matchAndroidVersion(searchText)
    }
}
