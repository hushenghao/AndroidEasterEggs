@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.dede.android_eggs.composable.colorpicker

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Colorize
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.composable.ScrollableModalBottomSheet
import com.dede.android_eggs.composable.colorpicker.ColorPickerUtilities.getHsv
import com.dede.android_eggs.icon_shape.IconShapePrefUtil
import com.dede.basic.copy
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

    val context = LocalContext.current

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier.clip(IconShapePrefUtil.getIconShape()),
                    contentAlignment = Alignment.Center,
                ) {
                    Checkerboard(
                        modifier = Modifier
                            .size(IconButtonDefaults.mediumContainerSize())
                            .drawWithContent {
                                drawContent()
                                drawRect(finalColor)
                            },
                    )
                    Icon(
                        imageVector = Icons.Rounded.Palette,
                        contentDescription = null,
                        tint = ColorPickerUtilities.getHighlightColor(finalColor),
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = ColorPickerUtilities.getHexColor(finalColor, withAlphaPalette),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Monospace,
                            ),
                            modifier = Modifier.weight(1f),
                        )

                        FilledTonalIconButton(
                            shape = IconShapePrefUtil.getIconShape(),
                            onClick = {
                                val h = Random.nextFloat() * 360f
                                val s = Random.nextFloat()
                                val v = Random.nextFloat()
                                hue = h
                                saturation = s
                                value = v
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shuffle,
                                contentDescription = null,
                            )
                        }

                        FilledTonalIconButton(
                            shape = IconShapePrefUtil.getIconShape(),
                            onClick = {
                                val hexColor =
                                    ColorPickerUtilities.getHexColor(finalColor, withAlphaPalette)
                                context.copy(hexColor)
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ContentCopy,
                                contentDescription = stringResource(android.R.string.copy),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    onColorChanged = { _, s, v ->
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

            Spacer(modifier = Modifier.height(12.dp))

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
