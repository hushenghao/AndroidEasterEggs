package com.dede.android_eggs.cat_editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.Deferred


internal class CaptureControllerDelegate private constructor(private val controller: CaptureController) {

    companion object {

        @Composable
        fun rememberCaptureControllerDelegate(): CaptureControllerDelegate {
            val captureController = rememberCaptureController()
            return remember(captureController) { CaptureControllerDelegate(captureController) }
        }
    }

    var onPerCapture: () -> Unit = {}

    fun getDelegate(): CaptureController = controller

    fun captureAsync(): Deferred<ImageBitmap> {
        onPerCapture()
        return controller.captureAsync()
    }
}
