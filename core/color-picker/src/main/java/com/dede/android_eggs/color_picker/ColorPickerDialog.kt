@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.dede.android_eggs.color_picker

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Colorize
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.color_picker.ColorPickerUtilities.getHsv
import com.dede.android_eggs.composable.ScrollableModalBottomSheet
import com.dede.android_eggs.icon_shape.IconShapePrefUtil
import kotlinx.coroutines.launch
import kotlin.random.Random


@Composable
fun ColorPickerDialog(
    visible: Boolean,
    initialColor: Color = Color.White,
    withAlphaPalette: Boolean = false,
    onColorSelected: (Color) -> Unit = {},
    isColorStrawEnabled: Boolean = true,
    onDismiss: () -> Unit = {},
) {
    val performColorSelected by rememberUpdatedState(onColorSelected)

    val eyeDropperLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val colorInt = data.getIntExtra(ColorPickerUtilities.EXTRA_COLOR, 0)
                    if (colorInt != 0) {
                        val pickedColor = Color(colorInt)
                        if (initialColor != pickedColor) {
                            performColorSelected(pickedColor)
                        }
                    }
                }
            }
        }

    if (!visible) {
        return
    }

    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
    )

    val hsv = initialColor.getHsv()
    var hue by remember { mutableFloatStateOf(hsv[0]) }
    var saturation by remember { mutableFloatStateOf(hsv[1]) }
    var value by remember { mutableFloatStateOf(hsv[2]) }
    var alpha by remember(withAlphaPalette) {
        mutableFloatStateOf(if (withAlphaPalette) initialColor.alpha else 1f)
    }

    // The color being edited with alpha stripped out; the alpha slider fades from
    // it to transparent so the gradient always reflects the current color.
    val opaqueColor = remember(hue, saturation, value) { Color.hsv(hue, saturation, value) }

    val finalColor =
        remember(hue, saturation, value, alpha) { Color.hsv(hue, saturation, value, alpha) }

    val scope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    val paddingValues = BottomSheetDefaults.modalWindowInsets.asPaddingValues()

    ScrollableModalBottomSheet(
        modifier = Modifier.sizeIn(maxWidth = 420.dp),
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        scrollState = scrollState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = scrollState)
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp + paddingValues.calculateBottomPadding()),
        ) {
            PresetColorSwatches(
                hue = hue,
                saturation = saturation,
                value = value,
                onColorSelected = { h, s, v ->
                    hue = h
                    saturation = s
                    value = v
                },
            )

            Spacer(modifier = Modifier.height(12.dp))

            ColorPickerHeader(
                modifier = Modifier.fillMaxWidth(),
                color = finalColor,
                withAlphaPalette = withAlphaPalette,
                onShuffle = {
                    hue = Random.nextFloat() * 360f
                    saturation = Random.nextFloat()
                    value = Random.nextFloat()
                },
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .sizeIn(maxWidth = 360.dp)
                    .fillMaxWidth()
                    .aspectRatio(3f / 2f)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ColorSvPalette(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    hue = hue,
                    defaultColor = Color.hsv(hue, saturation, value),
                    onColorChanged = { s, v ->
                        saturation = s
                        value = v
                    },
                )

                VerticalGradientSlider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(30.dp),
                    value = hue,
                    colors = listOf(
                        Color.Red, Color.Yellow, Color.Green,
                        Color.Cyan, Color.Blue, Color.Magenta, Color.Red,
                    ),
                    valueRange = 0f..360f,
                    onValueChange = { newValue ->
                        hue = newValue
                    },
                )

                if (withAlphaPalette) {
                    VerticalGradientSlider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(30.dp),
                        value = alpha,
                        colors = listOf(Color.Transparent, opaqueColor),
                        onValueChange = { newValue ->
                            alpha = newValue
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isColorStrawEnabled) {
                    FilledTonalIconButton(
                        shape = IconShapePrefUtil.getIconShape(),
                        onClick = {
                            scope.launch {
                                sheetState.hide()
                                onDismiss()
                                eyeDropperLauncher.launch(
                                    Intent(ColorPickerUtilities.ACTION_OPEN_EYE_DROPPER)
                                )
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Colorize,
                            contentDescription = null,
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                ) {
                    Text(text = stringResource(android.R.string.cancel))
                }

                Spacer(modifier = Modifier.width(14.dp))

                TextButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                            if (initialColor != finalColor) {
                                performColorSelected(finalColor)
                            }
                        }
                    },
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            }
        }
    }
}
