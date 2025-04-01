@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeApi::class,
    ExperimentalComposeUiApi::class
)

package com.dede.android_eggs.cat_editor

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.dede.android_eggs.cat_editor.CatEditorRecords.Companion.rememberCatEditorRecords
import com.dede.android_eggs.cat_editor.CatParts.VIEW_PORT_SIZE
import com.dede.android_eggs.views.theme.blend
import com.dede.basic.utils.ShareCatUtils
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import kotlin.math.min

private const val TAG = "CatEditor"


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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

    val captureController = rememberCaptureController()

    val context = LocalContext.current

    var catSpeed by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val catColors = remember(catSpeed) {
        mutableStateListOf(*CatPartColors.colors(catSpeed))
    }
    val catName = stringResource(R.string.default_cat_name, catSpeed % 1000)

    val colorPaletteState = remember { mutableStateOf(false) }

    val selectedPartIndexState = remember { mutableIntStateOf(-1) }
    var selectedPartIndex by selectedPartIndexState

    val records = rememberCatEditorRecords()

    LaunchedEffect(Unit) {
        // add first speed record
        records.addRecord(CatEditorRecords.speed(catSpeed))
    }

    ModalBottomSheet(
        onDismissRequest = {
            showSheet = false
        },
        sheetState = sheetState,
        dragHandle = {},
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = catName,//"Cat Editor",
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
            },
            bottomBar = {
                BottomAppBar(
                    actions = {
                        IconButton(
                            onClick = {
                                records.goBack(catColors)
                            },
                            enabled = records.canGoBack()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null
                            )
                        }
                        IconButton(
                            onClick = {
                                records.goNext(catColors)
                            },
                            enabled = records.canGoNext()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null
                            )
                        }

                        IconButton(
                            onClick = {
                                colorPaletteState.value = true
                            },
                            enabled = selectedPartIndex != -1
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Palette,
                                contentDescription = null
                            )
                        }

                        IconButton(
                            onClick = {
                                val deferred =
                                    captureController.captureAsync(Bitmap.Config.ARGB_8888)
                                scope.launch {
                                    val bitmap = deferred.await().asAndroidBitmap()
                                    ShareCatUtils.saveCat(context, bitmap, catName)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Save,
                                contentDescription = null
                            )
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                catSpeed = System.currentTimeMillis()
                                records.addRecord(CatEditorRecords.speed(catSpeed))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = null,
                            )
                        }
                    }
                )
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CatEditorGridLine()

                CatEditor(
                    colors = catColors,
                    captureController = captureController,
                    selectedPartIndexState = selectedPartIndexState
                )

                ColorPalette(
                    visibility = colorPaletteState,
                    onColorSelected = { color ->
                        val index = selectedPartIndex
                        if (index == -1) {
                            return@ColorPalette
                        }

                        catColors[index] = color.toArgb()
                        selectedPartIndex = -1

                        records.addRecord(CatEditorRecords.color(catColors[index], index))
                    }
                )
            }
        }
    }
}


@Composable
fun CatEditor(
    modifier: Modifier = Modifier,
    colors: SnapshotStateList<Int> = remember { mutableStateListOf(*CatPartColors.colors()) },
    selectedPartIndexState: MutableIntState = remember { mutableIntStateOf(-1) },
    touchable: Boolean = true,
    captureController: CaptureController = rememberCaptureController()
) {
    var selectedPartIndex by selectedPartIndexState

    val infiniteTransition = rememberInfiniteTransition(label = "CatEditor_SelectedPart")
    val ratio by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "HighlightColorBlend"
    )

    val matrix = remember { Matrix() }
    Canvas(
        contentDescription = "Cat Editor",
        modifier = Modifier
            .then(modifier)
            .capturable(captureController)
            .aspectRatio(1f)
            .fillMaxSize()
            .onSizeChanged {
                val size = min(it.width, it.height)
                matrix.reset()
                matrix.translate((it.width - size) / 2f, (it.height - size) / 2f)
                matrix.scale(size / VIEW_PORT_SIZE, size / VIEW_PORT_SIZE)
            }
            .pointerInput(touchable) {
                if (!touchable) {
                    return@pointerInput
                }
                detectTapGestures {
                    var handler = false
                    val position = it
                    for (i in CatParts.drawOrders.size - 1 downTo 0) {
                        val pathDraw = CatParts.drawOrders[i]
                        if (!pathDraw.touchable) {
                            continue
                        }
                        val rect = matrix.map(pathDraw.bounds)
                        if (rect.contains(position)) {
                            handler = selectedPartIndex != i
                            selectedPartIndex = i
                            break
                        }
                    }
                    if (!handler) {
                        selectedPartIndex = -1
                    }
                }
            },
        onDraw = {
            withTransform({ transform(matrix) }) {
                CatParts.drawOrders.forEachIndexed { index, pathDraw ->
                    var color = colors[index]
                    if (selectedPartIndex == index) {
                        val blend = Utilities.getHighlightColor(color).toArgb()
                        color = color.blend(blend, ratio)
                    }
                    pathDraw.drawLambda.invoke(this, Color(color))
                }
            }
        }
    )
}
