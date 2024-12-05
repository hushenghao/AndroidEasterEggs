package com.dede.android_eggs.embedding_splits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.tooling.preview.Preview
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.pill
import androidx.graphics.shapes.pillStar
import androidx.graphics.shapes.rectangle
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.views.theme.AppTheme
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.random.Random

/**
 * Placeholder for embedding splits
 */
class PlaceholderActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.enableEdgeToEdge(this)
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                @Suppress("UnusedMaterial3ScaffoldPaddingParameter")
                Scaffold {
                    Placeholder()
                }
            }
        }
    }

    init {
        shapes.shuffle()
    }

}

internal val shapes = arrayOf(
    // Love
    RoundedPolygon(
        vertices = floatArrayOf(
            radialToCartesian(0.8f, 0f.toRadians()).x,
            radialToCartesian(0.8f, 0f.toRadians()).y,
            radialToCartesian(1f, 90f.toRadians()).x,
            radialToCartesian(1f, 90f.toRadians()).y,
            radialToCartesian(0.8f, 180f.toRadians()).x,
            radialToCartesian(0.8f, 180f.toRadians()).y,
            radialToCartesian(1f, 250f.toRadians()).x,
            radialToCartesian(1f, 250f.toRadians()).y,
            radialToCartesian(0.1f, 270f.toRadians()).x,
            radialToCartesian(0.1f, 270f.toRadians()).y,
            radialToCartesian(1f, 290f.toRadians()).x,
            radialToCartesian(1f, 290f.toRadians()).y,
        ),
        perVertexRounding = listOf(
            CornerRounding(0.6f),
            CornerRounding(0.1f),
            CornerRounding(0.6f),
            CornerRounding(0.6f),
            CornerRounding(0.1f),
            CornerRounding(0.6f),
        )
    ),
    // PillStar
    RoundedPolygon.pillStar(
        numVerticesPerRadius = 12,
        width = 1f,
        height = 1f,
        rounding = CornerRounding(.3f),
        innerRounding = CornerRounding(.3f)
    ).rotated(45f),
    // Pill
    RoundedPolygon.pill(
        width = 1f,
        height = 0.8f,
    ).rotated(45f),
    // Triangle
    RoundedPolygon(
        numVertices = 3,
        rounding = CornerRounding(0.2f)
    ),
    // Circle
    RoundedPolygon.circle(
        numVertices = 4,
    ),
    // Square
    RoundedPolygon.rectangle(
        width = 1f,
        height = 1f,
        rounding = CornerRounding(0.3f)
    ),
    // Scallop
    RoundedPolygon.star(
        numVerticesPerRadius = 12,
        innerRadius = .828f,
        rounding = CornerRounding(.32f),
        innerRounding = CornerRounding(.32f)
    ),
    // Clover
    RoundedPolygon.star(
        numVerticesPerRadius = 4,
        innerRadius = .352f,
        rounding = CornerRounding(.32f),
        innerRounding = CornerRounding(.32f)
    ).rotated(45f),
    // Hexagon
    RoundedPolygon(
        numVertices = 6,
        rounding = CornerRounding(0.2f),
    ),
    // Triangle
    RoundedPolygon(
        vertices = floatArrayOf(
            radialToCartesian(1f, 270f.toRadians()).x,
            radialToCartesian(1f, 270f.toRadians()).y,
            radialToCartesian(1f, 30f.toRadians()).x,
            radialToCartesian(1f, 30f.toRadians()).y,
            radialToCartesian(0.1f, 90f.toRadians()).x,
            radialToCartesian(0.1f, 90f.toRadians()).y,
            radialToCartesian(1f, 150f.toRadians()).x,
            radialToCartesian(1f, 150f.toRadians()).y
        ),
        rounding = CornerRounding(0.2f),
        centerX = 0f,
        centerY = 0f
    ),
    // CornerSE
    RoundedPolygon(
        vertices = floatArrayOf(1f, 1f, -1f, 1f, -1f, -1f, 1f, -1f),
        perVertexRounding = listOf(
            CornerRounding(0.4f),
            CornerRounding(1f),
            CornerRounding(1f),
            CornerRounding(1f)
        ),
    ),

    )

@Preview
@Composable
internal fun Placeholder() {
    val progress = remember { Animatable(0f) }
    var currShape by remember { mutableIntStateOf(Random.nextInt(shapes.size)) }
    val morphed by remember(currShape) {
        derivedStateOf {
            Morph(
                shapes[currShape % shapes.size],
                shapes[(currShape + 1) % shapes.size]
            )
        }
    }
    LaunchedEffect(Unit) {
        doAnimation(progress)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        val scope = rememberCoroutineScope()
        MorphComposable(
            sizedMorph = morphed,
            modifier = Modifier
                .fillMaxSize(0.28f)
                .clickable(
                    indication = null, // Eliminate the ripple effect.
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    scope.launch {
                        currShape += 1
                        doAnimation(progress)
                    }
                },
            progress = progress.value,
        )
    }
}


@Composable
private fun MorphComposable(
    sizedMorph: Morph,
    modifier: Modifier = Modifier,
    color: Color = colorScheme.surfaceVariant,
    progress: Float,
) {
    val matrix = remember { Matrix() }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .drawWithContent {
                val path = sizedMorph
                    .toPath(progress = progress)
                    .asComposePath()

                matrix.reset()
                val bounds = path.getBounds()
                val maxDimension = max(bounds.width, bounds.height)
                matrix.scale(size.width / maxDimension, size.height / maxDimension)
                matrix.translate(-bounds.left, -bounds.top)

                path.transform(matrix)

                drawPath(path, color)

                drawContent()
            },
    )
}

private suspend fun doAnimation(progress: Animatable<Float, AnimationVector1D>) {
    progress.snapTo(0f)
    progress.animateTo(
        1f,
        animationSpec = spring(0.6f, 50f)
    )
}
