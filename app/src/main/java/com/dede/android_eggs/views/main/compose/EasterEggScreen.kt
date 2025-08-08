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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dede.android_eggs.R
import com.dede.android_eggs.inject.EasterEggModules
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.navigation.LocalNavController
import com.dede.android_eggs.ui.composes.ReverseModalNavigationDrawer
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.OrientationAngleSensor
import com.dede.android_eggs.util.Receiver
import com.dede.android_eggs.util.compose.end
import com.dede.android_eggs.util.compose.plus
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.android_eggs.views.main.util.EasterEggLogoSensorMatrixConvert
import com.dede.android_eggs.views.settings.SettingsScreen
import com.dede.android_eggs.views.settings.compose.basic.SettingPrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.IconVisualEffectsPrefUtil
import com.dede.basic.Utils
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

object EasterEggsScreen : EasterEggsDestination {
    override val route: String = "easter_eggs"
}

@HiltViewModel
class EasterEggViewModel @Inject constructor(
    val easterEggs: List<@JvmSuppressWildcards BaseEasterEgg>,
) : ViewModel()

@Composable
fun EasterEggScreen(
    viewModel: EasterEggViewModel = hiltViewModel<EasterEggViewModel>(),
) {
    val context = LocalContext.current

    val logoSensor = remember { EasterEggLogoSensorMatrixConvert() }
    if (IconVisualEffectsPrefUtil.isSupported()) {
        var orientationAngleSensor: OrientationAngleSensor? = remember { null }
        val lifecycleOwner = LocalLifecycleOwner.current

        fun handleOrientationAngleSensor(enable: Boolean) {
            if (enable && orientationAngleSensor == null) {
                orientationAngleSensor = OrientationAngleSensor(
                    context, lifecycleOwner, logoSensor
                )
            } else if (!enable && orientationAngleSensor != null) {
                orientationAngleSensor!!.destroy()
                orientationAngleSensor = null
            }
        }

        LocalEvent.Receiver(IconVisualEffectsPrefUtil.ACTION_CHANGED) {
            val enable = it.getBooleanExtra(SettingPrefUtil.EXTRA_VALUE, false)
            handleOrientationAngleSensor(enable)
        }

        LaunchedEffect(Unit) {
            handleOrientationAngleSensor(IconVisualEffectsPrefUtil.isEnable(context))
        }
    }

    val navController = LocalNavController.current
    LaunchedEffect(navController) {
        if (!isAgreedPrivacyPolicy(context)) {
            navController.navigate(WelcomeDialog.route)
        }

        if (!AnimatorDisabledAlertDialog.isDontShowAgain(context) &&
            !Utils.areAnimatorEnabled(context)
        ) {
            navController.navigate(AnimatorDisabledAlertDialog.route)
        }
    }

    val konfettiState = LocalKonfettiState.current
    CompositionLocalProvider(
        LocalEasterEggLogoSensor provides logoSensor,
    ) {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        ReverseModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet(
                    drawerState = drawerState,
                    drawerShape = shapes.extraLarge.end(0.dp),
                    windowInsets = WindowInsets(0, 0, 0, 0)
                ) {
                    val maxWidth =
                        LocalConfiguration.current.smallestScreenWidthDp * 0.8f
                    Box(modifier = Modifier.width(maxWidth.dp)) {
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
                    BottomSearchBar(searchBarState)
                }
            ) { contentPadding ->
                EasterEggList(viewModel.easterEggs, searchBarState.searchText, contentPadding)
            }
        }

        Konfetti(konfettiState)
    }
}

private const val HIGHEST_COUNT = 1

@Composable
@Preview(showBackground = true)
fun EasterEggList(
    easterEggs: List<BaseEasterEgg> = EasterEggHelp.previewEasterEggs(),
    searchFilter: String = "",
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val context = LocalContext.current
    val pureEasterEggs = remember(easterEggs) {
        EasterEggModules.providePureEasterEggList(easterEggs)
    }
    val searchText = remember(searchFilter) {
        searchFilter.trim().uppercase()
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
        modifier = Modifier.fillMaxSize(),
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
                        items(items = currentList) {
                            EasterEggItem(it, enableItemAnim = true)
                        }
                    } else {
                        val highestList = currentList.subList(0, HIGHEST_COUNT)
                        val normalList = currentList.subList(HIGHEST_COUNT, currentList.size)
                        items(items = highestList) {
                            EasterEggHighestItem(it)
                        }
                        item("wavy") {
                            Wavy(
                                modifier = Modifier
                                    .fillMaxWidth(0.4f)
                                    .padding(vertical = 26.dp),
                            )
                        }
                        items(items = normalList) {
                            EasterEggItem(it, enableItemAnim = false)
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
