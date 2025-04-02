package com.dede.android_eggs.cat_editor

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import dev.shreyaspatil.capturable.controller.CaptureController
import kotlinx.coroutines.Deferred


internal class CaptureControllerDelegate private constructor(private val controller: CaptureController) {

    companion object {

        @Composable
        fun rememberCaptureControllerDelegate(): CaptureControllerDelegate {
            return remember { CaptureControllerDelegate(CaptureController()) }
        }
    }

    var onPerCapture: () -> Unit = {}

    fun getDelegate(): CaptureController = controller

    @ExperimentalComposeApi
    fun captureAsync(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Deferred<ImageBitmap> {
        onPerCapture()
        return controller.captureAsync(config)
    }
}
