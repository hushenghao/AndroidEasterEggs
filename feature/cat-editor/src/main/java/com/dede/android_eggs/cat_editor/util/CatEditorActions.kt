@file:OptIn(ExperimentalPermissionsApi::class)

package com.dede.android_eggs.cat_editor.util

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.print.PrintHelper
import com.dede.android_eggs.cat_editor.util.Utilities.toPrintBitmap
import com.dede.basic.toast
import com.dede.basic.utils.ShareCatUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch

internal class SaveCatAction(
    val save: () -> Unit,
    val share: () -> Unit,
)

@Composable
internal fun rememberSaveCatAction(
    catName: String,
    captureController: CaptureControllerDelegate,
): SaveCatAction {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    fun executeSave(onCatSaved: ((bitmap: Bitmap, uri: Uri?, catName: String) -> Unit)? = null) {
        isSaving = true
        val deferred = captureController.captureAsync()
        scope.launch {
            val bitmap = deferred.await().asAndroidBitmap()
            val uri = ShareCatUtils.saveCat(context, bitmap, catName)
            onCatSaved?.invoke(bitmap, uri, catName)
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
                pendingAction?.invoke()
            } else {
                context.toast("🚫")
            }
            pendingAction = null
        }
    )

    val currentSave: () -> Unit = {
        if (!isSaving) {
            when {
                storagePermissionState.allPermissionsGranted -> executeSave()
                storagePermissionState.shouldShowRationale -> context.toast("🚫")
                else -> {
                    pendingAction = { executeSave() }
                    storagePermissionState.launchMultiplePermissionRequest()
                }
            }
        }
    }

    val currentShare: () -> Unit = {
        if (!isSaving) {
            val shareAction = {
                executeSave { _, uri, name ->
                    if (uri != null) {
                        ShareCatUtils.shareCatOnly(context, uri, name)
                    }
                }
            }
            when {
                storagePermissionState.allPermissionsGranted -> shareAction()
                storagePermissionState.shouldShowRationale -> context.toast("🚫")
                else -> {
                    pendingAction = shareAction
                    storagePermissionState.launchMultiplePermissionRequest()
                }
            }
        }
    }

    return remember(catName, captureController, isSaving) {
        SaveCatAction(save = currentSave, share = currentShare)
    }
}

@Composable
internal fun rememberPrintCatAction(
    catName: String,
    captureController: CaptureControllerDelegate,
): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isPrinting by remember { mutableStateOf(false) }

    return {
        if (!isPrinting) {
            if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_PRINTING)) {
                context.toast("🚫")
            } else {
                isPrinting = true
                val deferred = captureController.captureAsync()
                scope.launch {
                    val bitmap = deferred.await().asAndroidBitmap().toPrintBitmap()
                    try {
                        PrintHelper(context).apply {
                            scaleMode = PrintHelper.SCALE_MODE_FIT
                        }.printBitmap(catName, bitmap)
                    } catch (_: RuntimeException) {
                        context.toast("🚫")
                    } finally {
                        isPrinting = false
                    }
                }
            }
        }
    }
}
