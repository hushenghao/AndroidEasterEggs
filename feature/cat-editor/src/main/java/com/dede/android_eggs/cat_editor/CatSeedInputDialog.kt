package com.dede.android_eggs.cat_editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.LocaleList
import com.dede.android_eggs.resources.R
import com.dede.android_eggs.ui.composes.icons.rounded.Cat

/**
 * A dialog for inputting a cat seed.
 */
@Composable
fun CatSeedInputDialog(
    visibleState: MutableState<Boolean>,
    onConfirm: (seed: Long) -> Unit,
    onDismiss: () -> Unit = {},
) {
    var visible by visibleState
    if (!visible) {
        return
    }

    var inputSeedText by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf(false) }

    fun dismiss() {
        visible = false
        inputSeedText = ""
        inputError = false
        onDismiss()
    }

    fun done() {
        if (inputSeedText.isBlank()) {
            inputError = true
            return
        }

        val seed = Utilities.string2Seed(inputSeedText)
        onConfirm(seed)
        dismiss()
    }

    AlertDialog(
        onDismissRequest = {
            dismiss()
        },
        title = {
            Text(text = stringResource(R.string.cat_editor))
        },
        text = {
            val focusRequester = remember { FocusRequester() }
            val keyboardController = LocalSoftwareKeyboardController.current
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                keyboardController?.show()
            }

            TextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = inputSeedText,
                onValueChange = {
                    inputSeedText = it
                    inputError = false
                },
                placeholder = {
                    Text(text = "XXX")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.Cat, contentDescription = null)
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = inputSeedText.isNotEmpty(),
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut(),
                    ) {
                        IconButton(onClick = { inputSeedText = "" }) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = null
                            )
                        }
                    }
                },
                isError = inputError,
                label = {
                    Text(text = stringResource(R.string.cat_editor_input_seed))
                },
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                    showKeyboardOnFocus = true,
                    hintLocales = LocaleList("en"),
                    capitalization = KeyboardCapitalization.None,
                ),
                keyboardActions = KeyboardActions {
                    done()
                }
            )
        },
        dismissButton = {
            TextButton(onClick = {
                dismiss()
            }) {
                Text(text = stringResource(android.R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                done()
            }) {
                Text(text = stringResource(android.R.string.ok))
            }
        },
    )
}