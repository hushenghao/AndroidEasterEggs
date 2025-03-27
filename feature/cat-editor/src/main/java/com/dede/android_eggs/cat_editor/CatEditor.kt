@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeApi::class,
    ExperimentalComposeUiApi::class
)

package com.dede.android_eggs.cat_editor

import android.graphics.Bitmap
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SaveAlt
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.cat_editor.CatParts.VIEW_PORT_SIZE
import com.dede.android_eggs.views.theme.blend
import com.dede.basic.utils.ShareCatUtils
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import kotlin.math.min

private const val TAG = "CatEditor"


@Composable
fun CatEditorSheetDialog(
    showSheetState: MutableState<Boolean>,
) {
    var showSheet by showSheetState
    if (!showSheet) {
        return
    }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    ModalBottomSheet(
        onDismissRequest = {
            showSheet = false
        },
        sheetState = sheetState,
        dragHandle = {},

        ) {

        CenterAlignedTopAppBar(
            windowInsets = WindowInsets.systemBars
                .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
            title = {
                Text(
                    text = "Cat Editor",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium
                )
            },
            actions = {
                IconButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            showSheet = false
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            var speed by remember { mutableLongStateOf(System.currentTimeMillis()) }
            val colors = remember(speed) { CatPartColors.colors(speed) }
            val catName = remember(speed) { "Cat #${speed % 1000}" }

            val captureController = rememberCaptureController()

            val context = LocalContext.current

            CatEditor(colors = colors, captureController = captureController)

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 32.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(
                    onClick = {
                        val deferred = captureController.captureAsync(Bitmap.Config.ARGB_8888)
                        scope.launch {
                            val bitmap = deferred.await().asAndroidBitmap()
                            ShareCatUtils.saveCat(context, bitmap, catName)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SaveAlt,
                        contentDescription = null
                    )
                }

                FloatingActionButton(
                    onClick = {
                        speed = System.currentTimeMillis()
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null,
                        )
                        Text(
                            text = catName,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

            }

        }
    }
}


@Composable
fun CatEditor(
    modifier: Modifier = Modifier,
    colors: IntArray = CatPartColors.colors(),
    touchable: Boolean = false,
    captureController: CaptureController = rememberCaptureController()
) {
    var selectedPartIndex by remember { mutableIntStateOf(-1) }

    val infiniteTransition = rememberInfiniteTransition(label = "CatEditor_SelectedPart")
    val ratio by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "HighlightColorBlend"
    )

    val matrix = remember { Matrix() }
    Canvas(
        contentDescription = "Cat Editor",
        modifier = Modifier
            .capturable(captureController)
            .fillMaxSize()
            .onSizeChanged {
                val size = min(it.width, it.height)
                matrix.reset()
                matrix.translate((it.width - size) / 2f, (it.height - size) / 2f)
                matrix.scale(size / VIEW_PORT_SIZE, size / VIEW_PORT_SIZE)
            }
            .pointerInput(Unit) {
                if (!touchable) {
                    return@pointerInput
                }
                detectTapGestures {
                    var handler = false
                    val position = it
                    for (i in CatParts.drawOrders.size - 1 downTo 0) {
                        val rect = matrix.map(CatParts.drawOrders[i].bounds)
                        if (rect.contains(position)) {
                            selectedPartIndex = if (selectedPartIndex == i) -1 else i
                            handler = true
                            break
                        }
                    }
                    if (!handler) {
                        selectedPartIndex = -1
                    }
                }
            }
            .drawWithCache {
                this.obtainGraphicsLayer()
                onDrawBehind {

                }
            }
            .then(modifier),
        onDraw = {
            withTransform({ transform(matrix) }) {
                CatParts.drawOrders.forEachIndexed { index, pathDraw ->
                    var color = colors[index]
                    if (selectedPartIndex == index) {
                        color = color.blend(CatPartColors.getInverseColor(color), ratio)
                    }
                    pathDraw.drawLambda.invoke(this, Color(color))
                }
            }
        }
    )
}
