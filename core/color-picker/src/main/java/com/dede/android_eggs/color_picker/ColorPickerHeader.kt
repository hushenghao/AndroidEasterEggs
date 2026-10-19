package com.dede.android_eggs.color_picker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.icon_shape.IconShapePrefUtil
import com.dede.basic.copy

/**
 * Current color preview with the hex text and the shuffle / copy actions.
 */
@Composable
fun ColorPickerHeader(
    modifier: Modifier = Modifier,
    color: Color,
    withAlphaPalette: Boolean,
    onShuffle: () -> Unit,
) {
    val context = LocalContext.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.clip(IconShapePrefUtil.getIconShape()),
            contentAlignment = Alignment.Center,
        ) {
            Checkerboard(
                modifier = Modifier
                    .size(48.dp)
                    .drawWithContent {
                        drawContent()
                        drawRect(color)
                    },
            )
            Icon(
                imageVector = Icons.Rounded.Palette,
                contentDescription = null,
                tint = ColorPickerUtilities.getHighlightColor(color),
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

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
                    text = ColorPickerUtilities.getHexColor(color, withAlphaPalette),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Monospace,
                    ),
                    modifier = Modifier.weight(1f),
                )

                FilledTonalIconButton(
                    shape = IconShapePrefUtil.getIconShape(),
                    onClick = onShuffle,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shuffle,
                        contentDescription = null,
                    )
                }

                FilledTonalIconButton(
                    shape = IconShapePrefUtil.getIconShape(),
                    onClick = {
                        context.copy(ColorPickerUtilities.getHexColor(color, withAlphaPalette))
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
}
