package com.dede.android_eggs.composable.appbar

import android.graphics.BlurMaskFilter
import android.os.Build
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.nativePaint
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.composable.appbar.HazeScaffoldDefaults.hazeBottomBar
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.materials.HazeMaterials

/**
 * Floating capsule surface that switches between a tonal Haze glassmorphic backdrop with a hollow
 * outer drop shadow (when [LocalHazeEnabled] is true) and a solid Material 3 elevated surface.
 */
@Composable
fun HazeFloatingSurface(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    state: HazeState = LocalHazeState.current,
    elevation: Dp = 6.dp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable () -> Unit,
) {
    val hazeEnabled = LocalHazeEnabled.current
    val effectiveElevation = if (hazeEnabled) 0.dp else elevation
    val effectiveContainerColor = if (hazeEnabled) Color.Transparent else containerColor
    val surfaceModifier = if (hazeEnabled) {
        val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f
        val shadowColor = Color.Black.copy(alpha = if (isDarkTheme) 0.42f else 0.16f)
        modifier
            .hollowDropShadow(
                shape = shape,
                color = shadowColor,
                blurRadius = 10.dp,
                offsetY = 3.dp,
            )
            .clip(shape)
            .hazeBottomBar(
                state = state,
                style = HazeMaterials.thin(containerColor),
            )
    } else {
        modifier
    }
    Surface(
        modifier = surfaceModifier,
        shape = shape,
        color = effectiveContainerColor,
        contentColor = contentColor,
        tonalElevation = effectiveElevation,
        shadowElevation = effectiveElevation,
        content = content,
    )
}

private fun Modifier.hollowDropShadow(
    shape: Shape,
    color: Color,
    blurRadius: Dp,
    offsetY: Dp = 0.dp,
): Modifier {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
        return this
    }
    return this.drawWithCache {
        val blurRadiusPx = blurRadius.toPx()
        val offsetYPx = offsetY.toPx()
        val outlinePath = Path().apply {
            addOutline(shape.createOutline(size, layoutDirection, this@drawWithCache))
        }
        val shadowPaint = Paint().apply {
            isAntiAlias = true
            this.color = color
            if (blurRadiusPx > 0f) {
                nativePaint.maskFilter = BlurMaskFilter(
                    blurRadiusPx,
                    BlurMaskFilter.Blur.NORMAL,
                )
            }
        }
        onDrawBehind {
            clipPath(outlinePath, ClipOp.Difference) {
                drawIntoCanvas { canvas ->
                    canvas.save()
                    canvas.translate(0f, offsetYPx)
                    canvas.drawPath(outlinePath, shadowPaint)
                    canvas.restore()
                }
            }
        }
    }
}
