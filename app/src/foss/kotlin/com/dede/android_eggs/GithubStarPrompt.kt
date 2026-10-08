package com.dede.android_eggs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dede.android_eggs.navigation.OverlayContentProvider
import com.dede.android_eggs.navigation.OverlayRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import com.dede.android_eggs.resources.R as StringsR

object GithubStarRoute : OverlayRoute

@Module
@InstallIn(SingletonComponent::class)
object GithubStarOverlayProvider : OverlayContentProvider {

    override val route: OverlayRoute = GithubStarRoute

    @Provides
    @IntoSet
    fun provide(): OverlayContentProvider = this

    @Composable
    override fun Content(onDismiss: () -> Unit) {
        GithubStarAlertDialog(onDismiss = onDismiss)
    }
}

@Composable
@Preview
fun GithubStarAlertDialog(onDismiss: () -> Unit = {}) {
    val uriHandler = LocalUriHandler.current
    val githubUrl = stringResource(R.string.url_github)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(StringsR.string.github_star_alert_title))
        },
        text = {
            Text(text = stringResource(StringsR.string.github_star_alert_message))
        },
        confirmButton = {
            TextButton(onClick = {
                uriHandler.openUri(githubUrl)
                onDismiss()
            }) {
                Text(text = stringResource(StringsR.string.action_star_github))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
    )
}
