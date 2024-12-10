package com.dede.android_eggs.views.main.compose

import android.os.Bundle
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animate
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import kotlinx.coroutines.flow.catch
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

    @Suppress("RedundantIf")
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
            return bundleOf(
                KEY_VISIBLE to value.visible,
                KEY_SEARCH_TEXT to value.searchText
            )
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
    var backProgress by remember { mutableFloatStateOf(0f) }
    PredictiveBackHandler(enabled = state.visible) { flow ->
        flow.catch {
            animate(backProgress, 0f) { value, _ ->
                backProgress = value
            }
        }.collect { event ->
            backProgress = event.progress
        }
        state.close()
    }
    LaunchedEffect(state.visible) {
        if (state.visible) {
            backProgress = 0f
        }
    }
    AnimatedVisibility(
        visible = state.visible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
    ) {
        BottomSearchBarView(
            state = state,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = 1F - (0.1F * backProgress),
                    scaleY = 1F - (0.1F * backProgress),
                    transformOrigin = remember { TransformOrigin(0.5f, 1f) },
                ),
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
            keyboardController?.hide()
        }
    }
    Surface(
        modifier = Modifier
            .then(modifier)
            .imePadding(),
        shape = shape,
        color = colorScheme.surfaceColorAtElevation(4.dp),
        contentColor = colorScheme.onSurface,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
    ) {
        TextField(modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .navigationBarsPadding()
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
            shape = RoundedCornerShape(50),
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