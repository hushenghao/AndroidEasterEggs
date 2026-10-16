package com.dede.android_eggs.composable.colorpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val HUE_SEGMENT_COUNT = 30

private const val BRIGHTNESS_SEGMENT_COUNT = 3
private const val RED_BRIGHTNESS_SEGMENT_COUNT = 4

/**
 * One preset swatch in HSV space; [color] is what the swatch renders.
 */
private data class SwatchColor(
    val hue: Float,
    val saturation: Float,
    val value: Float,
) {
    val color: Color get() = Color.hsv(
        ((hue % 360f) + 360f) % 360f,
        saturation.coerceIn(0f, 1f),
        value.coerceIn(0f, 1f),
    )
}

private fun SwatchColor.matches(hue: Float, saturation: Float, value: Float): Boolean {
    val h = ((hue % 360f) + 360f) % 360f
    val hueDiff = abs(h - this.hue).let { min(it, 360f - it) }
    // Hue and saturation carry no information on black (value 0), the same way
    // hue carries none on gray (saturation 0); skipping them there is what
    // lets a black picked from the palette match the black swatch, whatever
    // hue and saturation the palette reported.
    val black = value <= 0f || this.value <= 0f
    val hueMatches = black ||
            saturation <= 0f ||
            this.saturation <= 0f ||
            hueDiff <= 180f / HUE_SEGMENT_COUNT
    val svTolerance = 1f / (max(BRIGHTNESS_SEGMENT_COUNT, RED_BRIGHTNESS_SEGMENT_COUNT) * 2)
    val saturationMatches = black || abs(saturation - this.saturation) <= svTolerance
    return hueMatches &&
            saturationMatches &&
            abs(value - this.value) <= svTolerance
}

/**
 * Horizontally scrollable row of circular swatches: a white-to-black-to-red
 * brightness ramp flowing into the hue ramp Red, Yellow, Green, Cyan, Blue,
 * Magenta and back toward Red. The hue ramp omits pure red — the brightness
 * ramp ends on it, and the circle closes there. The swatch matching the
 * current color is marked with a check icon and scrolled into view when it is
 * not fully visible; tapping a swatch reports its HSV values.
 */
@Composable
fun PresetColorSwatches(
    modifier: Modifier = Modifier,
    hue: Float,
    saturation: Float,
    value: Float,
    onColorSelected: (hue: Float, saturation: Float, value: Float) -> Unit,
) {
    val swatches = remember {
        val brightness = List(BRIGHTNESS_SEGMENT_COUNT) { i ->
            SwatchColor(0f, 0f, 1f - i / BRIGHTNESS_SEGMENT_COUNT.toFloat())
        } + List(RED_BRIGHTNESS_SEGMENT_COUNT) { i ->
            SwatchColor(0f, 1f, i / RED_BRIGHTNESS_SEGMENT_COUNT.toFloat())
        }
        val hue = List(HUE_SEGMENT_COUNT) { i ->
            SwatchColor(i * 360f / HUE_SEGMENT_COUNT, 1f, 1f)
        }
        brightness + hue
    }
    val selectedIndex = swatches.indexOfFirst { it.matches(hue, saturation, value) }
    val listState = rememberLazyListState()
    LaunchedEffect(selectedIndex) {
        if (selectedIndex < 0) {
            return@LaunchedEffect
        }
        val layoutInfo = listState.layoutInfo
        val fullyVisible = layoutInfo.visibleItemsInfo.any { item ->
            item.index == selectedIndex &&
                    item.offset >= layoutInfo.viewportStartOffset &&
                    item.offset + item.size <= layoutInfo.viewportEndOffset
        }
        if (!fullyVisible) {
            listState.animateScrollToItem(selectedIndex)
        }
    }
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        ),
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(swatches.size) { index ->
                val swatch = swatches[index]
                val selected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(swatch.color)
                        .clickable {
                            onColorSelected(swatch.hue, swatch.saturation, swatch.value)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    if (selected) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = ColorPickerUtilities.getHighlightColor(
                                swatch.color, darkColor = Color.Black
                            ),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}
