@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.dede.android_eggs.composable.appbar

import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.dede.android_eggs.composable.appbar.HazeAppBarDefaults.HazeAppBarBackButton
import com.dede.android_eggs.composable.appbar.HazeScaffoldDefaults.hazeAppBar
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.blur.materials.HazeMaterials
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun HazeScaffold(
    title: String,
    modifier: Modifier = Modifier,
    hazeState: HazeState = rememberHazeState(),
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    snackbarHost: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    topBarWindowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    content: @Composable (PaddingValues) -> Unit,
) {
    HazeScaffold(
        modifier = modifier,
        hazeState = hazeState,
        topBar = {
            HazeAppBar(
                title = title,
                subtitle = subtitle,
                modifier = Modifier.hazeAppBar(hazeState),
                navigationIcon = {
                    if (onBackClick != null) {
                        HazeAppBarBackButton(onClick = onBackClick)
                    }
                },
                actions = actions,
                windowInsets = topBarWindowInsets,
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        snackbarHost = snackbarHost,
        containerColor = containerColor,
        contentWindowInsets = contentWindowInsets,
        scrollBehavior = scrollBehavior,
        content = content,
    )
}

@Composable
fun HazeScaffold(
    modifier: Modifier = Modifier,
    hazeState: HazeState = rememberHazeState(),
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    snackbarHost: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    scrollBehavior: TopAppBarScrollBehavior? = TopAppBarDefaults.pinnedScrollBehavior(),
    content: @Composable (PaddingValues) -> Unit,
) {
    val scaffoldModifier = if (scrollBehavior != null) {
        modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    } else {
        modifier
    }

    CompositionLocalProvider(LocalHazeState provides hazeState) {
        Scaffold(
            modifier = scaffoldModifier,
            topBar = topBar,
            bottomBar = bottomBar,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
            snackbarHost = snackbarHost,
            containerColor = containerColor,
            contentWindowInsets = contentWindowInsets,
        ) { contentPadding ->
            Box(
                modifier = Modifier.hazeSource(hazeState),
            ) {
                content(contentPadding)
            }
        }
    }
}


object HazeScaffoldDefaults {

    @Composable
    fun Modifier.hazeAppBar(state: HazeState = LocalHazeState.current): Modifier = Modifier
        .hazeBlur(
            input = HazeInput.Sources(state),
            style = HazeMaterials.thick().then {
                progressive(
                    HazeProgressive.verticalGradient(
                        startIntensity = 1f,
                        endIntensity = 0f,
                    )
                )
            }
        )
        .then(this)

    @Composable
    fun Modifier.hazeBottomBar(state: HazeState = LocalHazeState.current): Modifier =
        Modifier
            .hazeBlur(
                input = HazeInput.Sources(state),
                style = HazeMaterials.ultraThick().then {
                    progressive(
                        HazeProgressive.verticalGradient(
                            easing = LinearEasing,
                            startIntensity = 0f,
                            endIntensity = 1f,
                        )
                    )
                }
            )
            .then(this)
}