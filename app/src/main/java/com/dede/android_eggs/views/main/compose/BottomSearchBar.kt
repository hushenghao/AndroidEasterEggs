package com.dede.android_eggs.views.main.compose

import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.ui.composes.PredictiveBackProgressHandler.predictiveBackShrink
import com.dede.android_eggs.ui.composes.predictiveBackProgressState
import com.dede.basic.bundleBuilder
import com.dede.android_eggs.resources.R as StringsR

@Stable
class BottomSearchBarState(initVisible: Boolean, initSearchText: String) {

    var visible: Boolean by mutableStateOf(initVisible)
        private set
    var searchText: String by mutableStateOf(initSearchText)

    fun close() {
        visible = false
        searchText = ""
    }

    fun open() {
        visible = true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BottomSearchBarState) return false

        if (visible != other.visible) return false
        if (searchText != other.searchText) return false

        return true
    }

    override fun hashCode(): Int {
        var result = visible.hashCode()
        result = 31 * result + searchText.hashCode()
        return result
    }

    object BundleSaver : Saver<BottomSearchBarState, Bundle> {

        private const val KEY_VISIBLE = "key_visible"
        private const val KEY_SEARCH_TEXT = "key_search_text"

        override fun restore(value: Bundle): BottomSearchBarState {
            return BottomSearchBarState(
                value.getBoolean(KEY_VISIBLE, false),
                value.getString(KEY_SEARCH_TEXT, "")
            )
        }

        override fun SaverScope.save(value: BottomSearchBarState): Bundle {
            return bundleBuilder {
                putBoolean(KEY_VISIBLE, value.visible)
                putString(KEY_SEARCH_TEXT, value.searchText)
            }
        }
    }
}

@Composable
fun rememberBottomSearchBarState(
    initVisible: Boolean = false,
    initSearchText: String = "",
): BottomSearchBarState {
    return rememberSaveable(saver = BottomSearchBarState.BundleSaver) {
        BottomSearchBarState(initVisible, initSearchText)
    }
}

@Composable
@Preview
fun BottomSearchBar(
    state: BottomSearchBarState = rememberBottomSearchBarState(true),
    onClose: (() -> Unit)? = null
) {
    val backProgress by predictiveBackProgressState(enabled = state.visible) {
        state.close()
    }
    AnimatedVisibility(
        visible = state.visible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
    ) {
        BottomSearchBarView(
            state = state,
            modifier = Modifier
                .graphicsLayer {
                    predictiveBackShrink(
                        progress = backProgress,
                        shrinkOrigin = Alignment.BottomCenter
                    )
                },
            shape = RoundedCornerShape(
                topStart = (28 * backProgress).dp,
                topEnd = (28 * backProgress).dp
            ),
            onClose = onClose,
        )
    }
}

@Composable
private fun BottomSearchBarView(
    state: BottomSearchBarState,
    modifier: Modifier,
    shape: Shape,
    onClose: (() -> Unit)?,
) {
    val currentOnClose by rememberUpdatedState(newValue = onClose)
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(state.visible) {
        if (state.visible) {
            focusRequester.requestFocus()
            keyboardController?.show()
        } else {
            focusRequester.freeFocus()
            keyboardController?.hide()
        }
    }
    Surface(
        modifier = Modifier
            .then(modifier)
            .navigationBarsPadding()
            .imePadding(),
        shape = shape,
        color = colorScheme.surfaceColorAtElevation(4.dp),
        contentColor = colorScheme.onSurface,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            value = state.searchText,
            onValueChange = {
                state.searchText = it
            },
            placeholder = {
                Text(text = stringResource(StringsR.string.label_search_hint))
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Search
            ),
            singleLine = true,
            shape = CircleShape,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            leadingIcon = {
                IconButton(
                    onClick = {
                        state.close()
                        keyboardController?.hide()
                        currentOnClose?.invoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null,
                    )
                }
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = state.searchText.isNotBlank(),
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                ) {
                    IconButton(onClick = { state.searchText = "" }) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = null,
                        )
                    }

                }
            })
    }
}