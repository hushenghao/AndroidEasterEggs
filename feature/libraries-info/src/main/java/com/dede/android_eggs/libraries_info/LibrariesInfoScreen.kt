package com.dede.android_eggs.libraries_info

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import com.dede.android_eggs.composable.appbar.HazeScaffold
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.navigation.LocalNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import com.dede.android_eggs.resources.R as StringsR

@Module
@InstallIn(SingletonComponent::class)
object LibrariesInfoScreen : EasterEggsDestination, EasterEggsDestination.Provider {

    override val route: NavKey = EasterEggsDestination.LibrariesInfo

    @Composable
    override fun Content() {
        LibrariesInfoScreen()
    }

    @Provides
    @IntoSet
    override fun provider(): EasterEggsDestination = this
}

@Composable
fun LibrariesInfoScreen() {
    val navigator = LocalNavigator.current
    HazeScaffold(
        title = stringResource(StringsR.string.label_open_source_license),
        onBackClick = navigator::goBack,
    ) {
        LibrariesInfoContent(
            contentPadding = it
        )
    }
}
