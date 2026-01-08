package com.dede.android_eggs.libraries_info

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.fromHtml
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.util.CustomTabsBrowser
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.util.withContext
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object LibrariesInfoScreen : EasterEggsDestination, EasterEggsDestination.Provider {

    override val route: String = "libraries_info"

    @Composable
    override fun Content() {
        LibrariesInfoScreen()
    }

    @Provides
    @IntoSet
    override fun provider(): EasterEggsDestination = this
}

internal fun Library.link(): String? =
    (scm?.url ?: website ?: licenses.firstOrNull()?.url)?.replace("git://", "")

internal fun Library.openLink(context: Context) {
    val link = this.link()
    if (!link.isNullOrBlank()) {
        CustomTabsBrowser.launchUrl(context, link)
    }
}

@Composable
fun LibrariesInfoScreen() {
    val context = LocalContext.current
    val libraries = remember {
        Libs.Builder().withContext(context).build()
    }
    var openLibrary: Library? by remember { mutableStateOf(null) }
    LibrariesContainer(
        libraries = libraries,
        modifier = Modifier,
        contentPadding = WindowInsets.systemBars.asPaddingValues(),
        showDescription = true,
        onLibraryClick = {
            val license = it.licenses.firstOrNull()
            if (license == null || license.licenseContent.isNullOrBlank()) {
                it.openLink(context)
            } else {
                openLibrary = it
            }
        },
    )

    if (openLibrary != null) {
        val library = openLibrary!!
        AlertDialog(
            onDismissRequest = { openLibrary = null },
            title = {
                Text(library.name)
            },
            text = {
                SelectionContainer(
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .verticalScroll(state = rememberScrollState())
                ) {
                    val html = remember(library) {
                        val htmlContent =
                            library.licenses.joinToString(separator = "<br /><br /><br /><br />") {
                                val licenseContent = it.licenseContent
                                if (licenseContent.isNullOrBlank()) return@joinToString ""
                                licenseContent
                                    .replace(Regex("https?://\\S*"), "<a href=\"$0\">$0</a>")
                                    .replace("\n", "<br />")
                            }
                        AnnotatedString.fromHtml(
                            htmlString = htmlContent,
                            linkInteractionListener = {
                                val url = it as? LinkAnnotation.Url
                                if (url != null && url.url.isNotBlank()) {
                                    CustomTabsBrowser.launchUrl(context, url.url)
                                }
                            }
                        )
                    }
                    Text(text = html)
                }
            },

            confirmButton = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = { openLibrary = null }) {
                        Text(stringResource(android.R.string.cancel))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    FilledTonalIconButton(onClick = { library.openLink(context) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
            },
        )
    }
}
