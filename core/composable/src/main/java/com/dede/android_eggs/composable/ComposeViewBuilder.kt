@file:JvmName("ComposeViewBuilder")
@file:Suppress("FunctionName")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.dede.android_eggs.composable

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.dede.android_eggs.views.settings.compose.prefs.ThemePrefUtil
import com.dede.android_eggs.views.theme.EasterEggsTheme
import com.dede.basic.lifecycleOwnerCompat
import com.dede.basic.savedStateOwnerCompat
import com.dede.basic.viewModelStoreOwnerCompat

fun ComposeViewBuilder(
    context: Context,
    lifecycleOwner: LifecycleOwner? = null,
    viewModelStoreOwner: ViewModelStoreOwner? = null,
    savedStateRegistryOwner: SavedStateRegistryOwner? = null,
    builder: @Composable () -> Unit
): ComposeView {
    val composeView = ComposeView(context)
    composeView.setViewTreeLifecycleOwner(lifecycleOwner)
    composeView.setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
    composeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)

    composeView.setContent(builder)
    return composeView
}

fun ComposeViewBuilder(
    activity: Activity,
    lifecycleOwner: LifecycleOwner? = activity.lifecycleOwnerCompat,
    viewModelStoreOwner: ViewModelStoreOwner? = activity.viewModelStoreOwnerCompat,
    savedStateRegistryOwner: SavedStateRegistryOwner? = activity.savedStateOwnerCompat,
    builder: @Composable () -> Unit
): ComposeView {
    return ComposeViewBuilder(
        context = activity,
        lifecycleOwner = lifecycleOwner,
        viewModelStoreOwner = viewModelStoreOwner,
        savedStateRegistryOwner = savedStateRegistryOwner,
        builder = builder
    )
}

fun ComposeViewThemeBuilder(
    activity: Activity,
    lifecycleOwner: LifecycleOwner? = activity.lifecycleOwnerCompat,
    viewModelStoreOwner: ViewModelStoreOwner? = activity.viewModelStoreOwnerCompat,
    savedStateRegistryOwner: SavedStateRegistryOwner? = activity.savedStateOwnerCompat,
    builder: @Composable () -> Unit
): ComposeView {
    return ComposeViewBuilder(
        context = activity,
        lifecycleOwner = lifecycleOwner,
        viewModelStoreOwner = viewModelStoreOwner,
        savedStateRegistryOwner = savedStateRegistryOwner,
    ) {
        EasterEggsTheme {
            builder()
        }
    }
}

fun buildDarkThemeLoadingIndicator(activity: Activity): View {
    return ComposeViewBuilder(activity) {
        EasterEggsTheme(theme = ThemePrefUtil.DARK) {
            LoadingIndicator(
                modifier = Modifier.size(92.dp)
            )
        }
    }
}
