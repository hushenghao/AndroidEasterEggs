package com.dede.android_eggs.embedding_splits

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.views.settings.compose.prefs.IconShapePrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.ThemePrefUtil
import com.dede.android_eggs.views.theme.EasterEggsTheme
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.random.Random

/**
 * Placeholder for embedding splits
 */
class PlaceholderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            EasterEggsTheme {
                @Suppress("UnusedMaterial3ScaffoldPaddingParameter")
                Scaffold {
                    Placeholder(
                        shapes = IconShapePrefUtil.providerPolygonItems().apply { shuffle() }
                    )
                }
            }
        }

        LocalEvent.receiver(this).register(ThemePrefUtil.ACTION_NIGHT_MODE_CHANGED) {
            recreate()
        }
    }

}

@Preview
@Composable
internal fun Placeholder(shapes: Array<RoundedPolygon> = IconShapePrefUtil.providerPolygonItems()) {
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
