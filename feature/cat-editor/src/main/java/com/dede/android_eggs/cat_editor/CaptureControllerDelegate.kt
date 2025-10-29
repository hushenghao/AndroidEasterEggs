package com.dede.android_eggs.cat_editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import dev.shreyaspatil.capturable.controller.CaptureController
import kotlinx.coroutines.Deferred


internal class CaptureControllerDelegate private constructor(private val controller: CaptureController) {

    companion object {

        @Composable
        fun rememberCaptureControllerDelegate(): CaptureControllerDelegate {
            val graphicsLayer = rememberGraphicsLayer()
            return remember(graphicsLayer) {
                CaptureControllerDelegate(
                    CaptureController(graphicsLayer)
                )
            }
        }
    }

    var onPerCapture: () -> Unit = {}

    fun getDelegate(): CaptureController = controller

    fun captureAsync(): Deferred<ImageBitmap> {
        onPerCapture()
        return controller.captureAsync()
    }
}
