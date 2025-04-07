@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeApi::class)

package com.dede.android_eggs.cat_editor

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GridOff
import androidx.compose.material.icons.rounded.GridOn
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.dede.android_eggs.cat_editor.CaptureControllerDelegate.Companion.rememberCaptureControllerDelegate
import com.dede.android_eggs.cat_editor.CatEditorRecords.Companion.rememberCatEditorRecords
import com.dede.basic.utils.ShareCatUtils
import kotlinx.coroutines.launch

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

    val captureController = rememberCaptureControllerDelegate()

    val context = LocalContext.current

    val catSpeedState = remember { mutableLongStateOf(System.currentTimeMillis()) }
    var catSpeed by catSpeedState
    val catName = stringResource(R.string.default_cat_name, catSpeed % 1000)

    var isSaving by remember { mutableStateOf(false) }
    val colorPaletteState = remember { mutableStateOf(false) }

    val catEditorRecords = rememberCatEditorRecords()
    // split with cat speed
    val catEditorController = rememberCatEditorController(catSpeed)

    LaunchedEffect(catEditorController.selectPart) {
        if (catEditorController.hasSelectedPart) {
            colorPaletteState.value = true
        }
    }

    LaunchedEffect(Unit) {
        // add first speed record
        catEditorRecords.addRecord(CatEditorRecords.speed(catSpeed))
    }

    ModalBottomSheet(
        onDismissRequest = {
            showSheet = false
        },
        sheetState = sheetState,
        dragHandle = {},
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) }
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
                                CatEditorRecords.restoreRecord(
                                    catEditorRecords.goBack(),
                                    catEditorController,
                                    catSpeedState
                                )
                            },
                            enabled = catEditorRecords.canGoBack()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null
                            )
                        }
                        IconButton(
                            onClick = {
                                CatEditorRecords.restoreRecord(
                                    catEditorRecords.goNext(),
                                    catEditorController,
                                    catSpeedState
                                )
                            },
                            enabled = catEditorRecords.canGoNext()
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
                            enabled = catEditorController.hasSelectedPart
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Palette,
                                contentDescription = null
                            )
                        }

                        IconButton(
                            onClick = {
                                catEditorController.isGridVisible =
                                    !catEditorController.isGridVisible
                            }
                        ) {
                            Crossfade(catEditorController.isGridVisible) {
                                if (it) {
                                    Icon(
                                        imageVector = Icons.Rounded.GridOff,
                                        contentDescription = null
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Rounded.GridOn,
                                        contentDescription = null
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = {
                                isSaving = true
                                val deferred =
                                    captureController.captureAsync(Bitmap.Config.ARGB_8888)
                                scope.launch {
                                    val bitmap = deferred.await().asAndroidBitmap()
                                    ShareCatUtils.saveCat(context, bitmap, catName)
                                    isSaving = false
                                }
                            },
                            enabled = !isSaving
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
                                catEditorController.updateColors(catSpeed)
                                catEditorRecords.addRecord(CatEditorRecords.speed(catSpeed))
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
                CatEditor(
                    controller = catEditorController,
                    captureController = captureController,
                )

                ColorPalette(
                    visibility = colorPaletteState,
                    selectedColor = catEditorController.getSelectedPartColor(Color.White),
                    onColorSelected = { color ->
                        if (!catEditorController.hasSelectedPart) {
                            return@ColorPalette
                        }

                        catEditorController.setSelectedPartColor(color)
                        catEditorRecords.addRecord(
                            CatEditorRecords.colors(
                                catEditorController.colorList,
                                catSpeed
                            )
                        )
                    }
                )
            }
        }
    }
}
