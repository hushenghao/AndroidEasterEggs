package com.dede.android_eggs.libraries_info

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.util.CustomTabsBrowser
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.util.withContext

internal fun Library.link(): String? =
    (scm?.url ?: website ?: licenses.firstOrNull()?.url)?.replace("git://", "")

internal fun Library.openLink(context: Context) {
    val link = this.link()
    if (!link.isNullOrBlank()) {
        CustomTabsBrowser.launchUrl(context, link)
    }
}

@Composable
fun LibrariesInfoContent(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current
    val libraries = remember {
        Libs.Builder().withContext(context).build()
    }
    var openLibrary: Library? by remember { mutableStateOf(null) }
    LibrariesContainer(
        libraries = libraries,
        modifier = modifier,
        contentPadding = contentPadding,
        showDescription = true,
        showFundingBadges = false,
        dimensions = LibraryDefaults.libraryDimensions(),
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
        LibrariesInfoDetail(
            library = openLibrary!!,
            onDismissRequest = { openLibrary = null },
        )
    }
}
