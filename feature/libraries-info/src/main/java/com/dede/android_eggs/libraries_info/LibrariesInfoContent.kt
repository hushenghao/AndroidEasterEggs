package com.dede.android_eggs.libraries_info

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.style.LibraryActionBadges
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActionKind
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActionMode
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryBadges
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryDetailMode
import com.mikepenz.aboutlibraries.util.withContext

@Composable
fun LibrariesInfoContent(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val libraries = remember {
        Libs.Builder().withContext(context).build()
    }
    LibrariesContainer(
        libraries = libraries,
        modifier = modifier,
        contentPadding = contentPadding,
        badges = LibraryBadges(
            author = true,
            description = true,
            version = true,
            license = true,
            funding = false,
        ),
        actionLabels = LibraryActionBadges(
            sourceEnabled = true,
            websiteEnabled = true,
            licenseEnabled = true,
            sponsorEnabled = false,
        ),
        detailMode = LibraryDetailMode.Sheet,
        actionMode = LibraryActionMode.Icons,
        onActionClick = { lib, kind ->
            when (kind) {
                LibraryActionKind.Source -> lib.scm?.url?.let { uriHandler.openUri(it) }
                LibraryActionKind.Website -> lib.website?.let { uriHandler.openUri(it) }
                LibraryActionKind.License ->
                    lib.licenses.firstOrNull()?.url?.let { uriHandler.openUri(it) }
                else -> return@LibrariesContainer false
            }
            true
        }
    )
}
