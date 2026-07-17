package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.composable.colorpicker.ColorPickerDialog
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.android_eggs.views.settings.compose.basic.rememberPrefIntState
import com.dede.android_eggs.views.settings.compose.prefs.ColorSourcePrefUtil.ColorSource
import com.dede.android_eggs.views.theme.resolveColorScheme
import com.dede.android_eggs.resources.R as StringsR


@Preview
@Composable
fun ColorSourcePref() {
    var colorSourcePacked by rememberPrefIntState(
        ColorSourcePrefUtil.KEY_COLOR_SOURCE,
        ColorSourcePrefUtil.DEFAULT_VALUE,
    )
    val currentSource = ColorSourcePrefUtil.decodeSource(colorSourcePacked)
    val currentSeedColor = ColorSourcePrefUtil.decodeSeedColor(colorSourcePacked)

    val updateSource = { source: ColorSource ->
        val newPacked = ColorSourcePrefUtil.encode(source, currentSeedColor)
        colorSourcePacked = newPacked
        ColorSourcePrefUtil.colorSourceState.intValue = newPacked
    }

    val updateSeed = { seedColor: Int ->
        val newPacked = ColorSourcePrefUtil.encode(ColorSource.CUSTOM, seedColor)
        colorSourcePacked = newPacked
        ColorSourcePrefUtil.colorSourceState.intValue = newPacked
    }

    var showColorPicker by remember { mutableStateOf(false) }

    val options = buildList {
        add(ColorSource.DEFAULT)
        if (ColorSourcePrefUtil.isDynamicColorSupported()) {
            add(ColorSource.DYNAMIC)
        }
        add(ColorSource.CUSTOM)
    }

    Column() {

        ExpandOptionsPref(
            leadingIcon = Icons.Rounded.Palette,
            title = stringResource(StringsR.string.pref_title_color_source),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                options.forEach { source ->
                    ColorSourceCard(
                        modifier = Modifier.weight(1f),
                        source = source,
                        selected = currentSource == source,
                        colorSourcePacked = colorSourcePacked,
                        onCardClick = { updateSource(source) },
                        onEditClick = { showColorPicker = true },
                    )
                }
            }
        }
    }

    ColorPickerDialog(
        visible = showColorPicker,
        initialColor = Color(currentSeedColor),
        withAlphaPalette = false,
        isColorStrawEnabled = false,
        onColorSelected = { color ->
            updateSeed(color.toArgb())
        },
        onDismiss = { showColorPicker = false },
    )
}

@Composable
private fun ColorSourceCard(
    modifier: Modifier = Modifier,
    source: ColorSource,
    selected: Boolean,
    colorSourcePacked: Int,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
) {
    val labelRes = when (source) {
        ColorSource.DEFAULT -> StringsR.string.summary_system_default
        ColorSource.DYNAMIC -> StringsR.string.summary_color_source_dynamic
        ColorSource.CUSTOM -> StringsR.string.summary_color_source_custom
    }

    val themeMode by ThemePrefUtil.themeModeState
    val seedColor = when (source) {
        ColorSource.CUSTOM -> ColorSourcePrefUtil.decodeSeedColor(colorSourcePacked)
        else -> ColorSourcePrefUtil.DEFAULT_SEED_COLOR
    }
    val scheme = resolveColorScheme(themeMode, source, seedColor)

    Card(
        onClick = onCardClick,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) colorScheme.primaryContainer else colorScheme.surface,
            contentColor = colorScheme.onSurface,
        ),
        modifier = modifier.heightIn(min = 88.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ThemeColorPreview(
                    scheme = scheme,
                    modifier = Modifier.size(48.dp),
                    shape = IconShapePrefUtil.getIconShape(),
                )
                Text(
                    text = stringResource(labelRes),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                )
            }

            if (source == ColorSource.CUSTOM && selected) {
                FilledTonalIconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .size(28.dp),
                    onClick = onEditClick,
                    shape = IconShapePrefUtil.getIconShape(),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = stringResource(labelRes),
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Preview(heightDp = 56, widthDp = 56)
@Composable
private fun ThemeColorPreview(
    modifier: Modifier = Modifier,
    scheme: ColorScheme = colorScheme,
    shape: Shape = CircleShape
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(shape),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(scheme.primary),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(scheme.secondary),
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(scheme.tertiary),
                )
            }
        }
    }
}
