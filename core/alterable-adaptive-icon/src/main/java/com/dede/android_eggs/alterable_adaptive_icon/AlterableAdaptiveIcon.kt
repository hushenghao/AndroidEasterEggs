package com.dede.android_eggs.alterable_adaptive_icon

import android.content.Context
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import com.dede.basic.DefType
import com.dede.basic.getIdentifier
import com.dede.basic.requireDrawable
import com.google.accompanist.drawablepainter.rememberDrawablePainter


private const val ADAPTIVE_ICON_INSET_FACTOR = 1 / 4f
private const val DEFAULT_CHILD_SCALE = 1 + 2 * ADAPTIVE_ICON_INSET_FACTOR

private const val BACKGROUND_ID = 0
private const val FOREGROUND_ID = 1

private class ChildDrawable(
    val drawable: Drawable,
    val id: Int
)

private fun Array<ChildDrawable>.getBackground(): Drawable {
    return checkNotNull(this.find { it.id == BACKGROUND_ID }).drawable
}

private fun Array<ChildDrawable>.getForeground(): Drawable? {
    return this.find { it.id == FOREGROUND_ID }?.drawable
}

private fun buildChildDrawableArray(context: Context, @DrawableRes res: Int): Array<ChildDrawable> {
    val drawable = context.requireDrawable(res)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && drawable is AdaptiveIconDrawable) {
        arrayOf(
            ChildDrawable(drawable.background, BACKGROUND_ID),
            ChildDrawable(drawable.foreground, FOREGROUND_ID),
        )
    } else {
        arrayOf(
            ChildDrawable(drawable, BACKGROUND_ID),
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
private fun PreviewAlterableAdaptiveIcon() {
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
    ) {
        val context = LocalContext.current
        val id = context.getIdentifier("ic_launcher", DefType.MIPMAP)
        AlterableAdaptiveIcon(
            modifier = Modifier.size(100.dp),
            res = id,
            clipShape = MaterialShapes.Clover4Leaf.toShape()
        )

        Image(
            painter = rememberDrawablePainter(context.requireDrawable(id)),
            contentDescription = null,
            modifier = Modifier.size(100.dp),
        )
    }
}

private fun Modifier.adaptiveIconChild(scale: Float = DEFAULT_CHILD_SCALE) = this then Modifier
    .layout { measurable, constraints ->
        val width: Int = if (constraints.hasBoundedWidth) {
            (constraints.maxWidth * scale).fastRoundToInt()
        } else {
            constraints.maxWidth
        }
        val height: Int = if (constraints.hasBoundedHeight) {
            (constraints.maxHeight * scale).fastRoundToInt()
        } else {
            constraints.maxHeight
        }

        val placeable = measurable.measure(Constraints(width, width, height, height))
        layout(width, height) {
            placeable.placeRelative(0, 0)
        }
    }

@Composable
fun AlterableAdaptiveIcon(
    modifier: Modifier = Modifier,
    clipShape: Shape,
    @DrawableRes res: Int,
    foregroundMatrix: Matrix = Matrix(),
) {
    val context = LocalContext.current
    val childDrawables = remember(res, context.theme) {
        buildChildDrawableArray(context, res)
    }
    val isAdaptiveIcon = childDrawables.size >= 2
    Box(
        modifier = Modifier
            .then(modifier)
            .clip(clipShape)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        val background: Drawable = childDrawables.getBackground()
        if (!isAdaptiveIcon) {
            Image(
                painter = rememberDrawablePainter(background),
                modifier = Modifier.wrapContentSize(),
                contentDescription = null,
            )
            return@Box
        }

        Image(
            painter = rememberDrawablePainter(background),
            modifier = Modifier.adaptiveIconChild(),
            contentDescription = null,
        )

        val foreground: Drawable? = childDrawables.getForeground()
        if (foreground != null) {
            Image(
                painter = rememberDrawablePainter(foreground),
                modifier = Modifier
                    .drawWithContent {
                        withTransform({
                            transform(foregroundMatrix)
                        }) {
                            this@drawWithContent.drawContent()
                        }
                    }
                    .adaptiveIconChild(),
                contentDescription = null,
            )
        }
    }
}
