package com.dede.android_eggs.views.main.compose

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.rounded.Mic
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.ui.composes.predictiveBackProgressState
import com.dede.basic.bundleBuilder
import com.dede.android_eggs.resources.R as StringsR

@Stable
class BottomSearchBarState(initVisible: Boolean, initSearchText: String) {

    var visible: Boolean by mutableStateOf(initVisible)
        private set

    // Text and cursor as a single editing state: programmatic updates (voice
    // input, clear) decide where the cursor lands, no view-side syncing needed.
    var textFieldValue: TextFieldValue by mutableStateOf(
        TextFieldValue(initSearchText, TextRange(initSearchText.length))
    )
        private set

    var text: String
        get() = textFieldValue.text
        set(value) {
            // Replace the whole text with the cursor moved to the end.
            textFieldValue = TextFieldValue(value, TextRange(value.length))
        }

    /** Write back the editing state as-is, preserving cursor and IME composition. */
    fun onTextChange(value: TextFieldValue) {
        textFieldValue = value
    }

    fun close() {
        visible = false
        text = ""
    }

    fun open() {
        visible = true
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
                putString(KEY_SEARCH_TEXT, value.text)
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
    modifier: Modifier = Modifier,
    state: BottomSearchBarState = rememberBottomSearchBarState(true),
    elevation: Dp = 4.dp,
    containerColor: Color = colorScheme.surfaceColorAtElevation(elevation),
    contentColor: Color = colorScheme.onSurface,
    onClose: (() -> Unit)? = null,
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
                    translationY = size.height * 0.3f * backProgress
                }
                .then(modifier),
            shape = RoundedCornerShape(
                topStart = (14 + 14 * backProgress).dp,
                topEnd = (14 + 14 * backProgress).dp
            ),
            onClose = onClose,
            elevation = elevation,
            containerColor = containerColor,
            contentColor = contentColor,
        )
    }
}

@Composable
private fun BottomSearchBarView(
    state: BottomSearchBarState,
    modifier: Modifier,
    shape: Shape,
    onClose: (() -> Unit)?,
    elevation: Dp = 4.dp,
    containerColor: Color = colorScheme.surfaceColorAtElevation(elevation),
    contentColor: Color = colorScheme.onSurface,
) {
    val currentOnClose by rememberUpdatedState(newValue = onClose)
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val searchHint = stringResource(StringsR.string.label_search_hint)
    val hasText = state.text.isNotBlank()

    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val text = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull()
        if (!text.isNullOrBlank()) {
            state.text = text
        }
    }
    val voiceSearchAvailable = remember(context) {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .resolveActivity(context.packageManager) != null
    }

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
            .imePadding()
            .then(modifier)
            .navigationBarsPadding(),
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = elevation,
        shadowElevation = elevation,
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            value = state.textFieldValue,
            onValueChange = state::onTextChange,
            placeholder = {
                Text(text = searchHint)
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
                errorIndicatorColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
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
                Crossfade(targetState = hasText, label = "TrailingIcon") { hasContent ->
                    if (hasContent) {
                        IconButton(onClick = { state.text = "" }) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = null,
                            )
                        }
                    } else if (voiceSearchAvailable) {
                        IconButton(
                            onClick = {
                                val intent =
                                    Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(
                                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
                                        )
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, searchHint)
                                        // Match the in-app language, not the system one
                                        val appLocales =
                                            AppCompatDelegate.getApplicationLocales()
                                        if (!appLocales.isEmpty) {
                                            putExtra(
                                                RecognizerIntent.EXTRA_LANGUAGE,
                                                appLocales.get(0)?.toLanguageTag(),
                                            )
                                        }
                                    }
                                try {
                                    voiceLauncher.launch(intent)
                                } catch (_: ActivityNotFoundException) {
                                    // The recognizer may disappear between the availability check and launch
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Mic,
                                contentDescription = null,
                            )
                        }
                    }
                }
            })
    }
}
