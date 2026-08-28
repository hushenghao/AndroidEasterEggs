@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.composable

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * A scrollable content [ModalBottomSheet], with the sheet gestures and safe area
 * insets handled uniformly.
 *
 * - The sheet gestures are disabled while [scrollState] can scroll backward, so dragging down
 *   inside the scrolled content scrolls it back up instead of dragging the sheet down.
 *   https://issuetracker.google.com/issues/353304855
 * - Pass `null` (default) when the scrollable content is not composed or not scrollable, then
 *   the sheet gestures stay enabled. This also prevents a stale scroll state from blocking the
 *   sheet gestures when e.g. a list is emptied and leaves the composition.
 * - The top window inset is applied by the sheet to keep content below the status bar when the
 *   sheet expands to full height. The bottom inset must be handled by the content itself, e.g.
 *   as the lazy list content padding or the scrollable column bottom padding, so the content
 *   can scroll behind the navigation bar.
 *
 * @see BottomSheetDefaults.modalWindowInsets
 */
@Composable
fun ScrollableModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
    ),
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    scrollState: ScrollableState? = null,
    contentWindowInsets: WindowInsets = BottomSheetDefaults.modalWindowInsets.only(WindowInsetsSides.Top),
    content: @Composable ColumnScope.() -> Unit,
) {
    // https://issuetracker.google.com/issues/353304855
    val sheetGesturesEnabled by remember(scrollState) {
        // disable sheet gestures when the content can scroll backward
        derivedStateOf { scrollState?.canScrollBackward != true }
    }
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        scrimColor = scrimColor,
        sheetGesturesEnabled = sheetGesturesEnabled,
        contentWindowInsets = { contentWindowInsets },
        content = content,
    )
}
