@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.dede.android_eggs.cat_editor

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.navigation.LocalNavController
import com.dede.basic.toast
import com.dede.basic.utils.ShareCatUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch

object CatEditorScreen : EasterEggsDestination {
    override val route: String = "cat_editor"
}

@Composable
fun CatEditorScreen() {
    val scope = rememberCoroutineScope()
    val captureController = rememberCaptureControllerDelegate()

    val navController = LocalNavController.current
    val context = LocalContext.current

    val catSpeedState = rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    var catSpeed by catSpeedState
    val catName = stringResource(R.string.default_cat_name, catSpeed % 1000)

    var isSaving by remember { mutableStateOf(false) }
    val colorPaletteState = remember { mutableStateOf(false) }

    val catEditorRecords = rememberCatEditorRecords(firstRecord = CatEditorRecords.speed(catSpeed))
    // split with cat speed
    val catEditorController = rememberCatEditorController(catSpeed)

    LaunchedEffect(catEditorController.selectPart) {
        if (catEditorController.hasSelectedPart) {
            colorPaletteState.value = true
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                title = {
                    Text(
                        text = catName,//"Cat Editor",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                },
            )
        },
        bottomBar = {

            fun saveCatToAlbum() {
                isSaving = true
                val deferred = captureController.captureAsync()
                scope.launch {
                    val bitmap = deferred.await().asAndroidBitmap()
                    val uri = ShareCatUtils.saveCat(context, bitmap, catName)
                    if (uri != null) {
                        context.toast("🐱")
                    } else {
                        context.toast("🚫")
                    }
                    isSaving = false
                }
            }

            val storagePermissionState = rememberMultiplePermissionsStateCompat(
                permissions = ShareCatUtils.storagePermissions,
                isNotRequire = ShareCatUtils.isNotRequireStoragePermissions,
                onPermissionsResult = { result ->
                    if (result.all { it.value }) {
                        saveCatToAlbum()
                    } else {
                        context.toast("🚫")
                    }
                }
            )

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
                            when {
                                storagePermissionState.allPermissionsGranted -> saveCatToAlbum()
                                storagePermissionState.shouldShowRationale -> context.toast("🚫")
                                else -> storagePermissionState.launchMultiplePermissionRequest()
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
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            CatEditor(
                controller = catEditorController,
                captureController = captureController,
            )

            ColorPaletteDialog(
                visibleState = colorPaletteState,
                selectedColor = catEditorController.getSelectedPartColor(Color.White),
                onColorSelected = { color ->
                    if (!catEditorController.hasSelectedPart) {
                        return@ColorPaletteDialog
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
