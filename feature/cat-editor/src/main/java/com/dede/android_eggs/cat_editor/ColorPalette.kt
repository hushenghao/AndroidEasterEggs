@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.cat_editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.cat_editor.Utilities.getHsv
import com.dede.basic.copy
import kotlinx.coroutines.launch
import kotlin.random.Random


@Composable
fun ColorPalette(
    visibility: MutableState<Boolean> = mutableStateOf(false),
    selectedColor: Color = Color.White,
    onColorSelected: (color: Color) -> Unit = {}
) {
    var visible by remember { visibility }
    if (!visible) {
        return
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val performColorSelected by rememberUpdatedState(onColorSelected)

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            visible = false
        }
    ) {
        val hsv = selectedColor.getHsv()
        var hue by remember { mutableFloatStateOf(hsv[0]) }
        var saturation by remember { mutableFloatStateOf(hsv[1]) }
        var value by remember { mutableFloatStateOf(hsv[2]) }
        var alpha by remember { mutableFloatStateOf(selectedColor.alpha) }
        var hsvColor by remember { mutableStateOf(Color.hsv(hue, saturation, 1f)) }

        val finalColor =
            remember(hue, saturation, value, alpha) { Color.hsv(hue, saturation, value, alpha) }

        val context = LocalContext.current

        val scope = rememberCoroutineScope()
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
                .verticalScroll(state = scrollState)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.clip(MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Checkerboard(
                        modifier = Modifier
                            .size(54.dp)
                            .drawWithContent {
                                drawContent()
                                drawRect(finalColor)
                            })
                    Icon(
                        imageVector = Icons.Rounded.Palette,
                        contentDescription = null,
                        tint = Utilities.getHighlightColor(finalColor)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Card {
                    Row(
                        modifier = Modifier
                            .height(56.dp)
                            .padding(start = 14.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = Utilities.getHexColor(finalColor),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                val h = Random.nextFloat() * 360f
                                val s = Random.nextFloat()
                                val v = Random.nextFloat()
                                hue = h
                                saturation = s
                                value = v
                                hsvColor = Color.hsv(hue, saturation, 1f)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = null
                            )
                        }

                        IconButton(
                            onClick = {
                                context.copy(Utilities.getHexColor(finalColor))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ContentCopy,
                                contentDescription = stringResource(android.R.string.copy)
                            )
                        }

                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ColorHsvPalette(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .align(Alignment.CenterHorizontally),
                defaultColor = Color.hsv(hue, saturation, 1f),
                onColorChanged = { hsv, h, s ->
                    hue = h
                    saturation = s
                    hsvColor = hsv
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            LinearGradientSlider(
                modifier = Modifier.padding(horizontal = 10.dp),
                value = value,
                startColor = Color.Black,
                endColor = hsvColor,
                onValueChange = { newValue ->
                    value = newValue
                }
            )

            LinearGradientSlider(
                modifier = Modifier.padding(horizontal = 10.dp),
                value = alpha,
                startColor = Color.Transparent,
                endColor = hsvColor,
                onValueChange = { newValue ->
                    alpha = newValue
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.align(Alignment.End)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            visible = false
                        }
                    }
                ) {
                    Text(text = stringResource(android.R.string.cancel))
                }

                Spacer(modifier = Modifier.width(14.dp))

                Button(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            visible = false
                            if (selectedColor != finalColor) {
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
