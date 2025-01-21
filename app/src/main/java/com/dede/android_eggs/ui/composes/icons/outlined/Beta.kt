/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dede.android_eggs.ui.composes.icons.outlined

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import com.dede.android_eggs.ui.composes.icons.materialIcon

public val Icons.Outlined.Beta: ImageVector
    get() {
        if (_beta != null) {
            return _beta!!
        }
        _beta = materialIcon(
            name = "Outlined.Beta",
            viewportWidth = 1_024.0f,
            viewportHeight = 1_024.0f
        ) {
            materialPath {
                moveTo(393.8f, 750.5f)
                lineTo(393.8f, 986.5f)
                lineTo(293.5f, 986.5f)
                lineTo(293.5f, 286.7f)
                curveTo(293.5f, 224.9f, 311.9f, 176.2f, 348.2f, 139.9f)
                curveTo(384.0f, 103.7f, 433.9f, 85.3f, 495.4f, 85.3f)
                curveTo(554.7f, 85.3f, 600.3f, 99.8f, 634.5f, 128.0f)
                curveTo(668.2f, 157.0f, 684.8f, 197.1f, 684.8f, 247.9f)
                curveTo(684.8f, 282.9f, 673.7f, 315.7f, 651.5f, 346.0f)
                curveTo(629.3f, 376.3f, 600.7f, 397.2f, 565.3f, 408.7f)
                lineTo(565.3f, 410.5f)
                curveTo(618.7f, 419.0f, 660.1f, 438.2f, 688.2f, 469.3f)
                curveTo(716.4f, 499.6f, 730.5f, 538.5f, 730.5f, 586.2f)
                curveTo(730.5f, 642.6f, 710.8f, 688.6f, 672.0f, 724.1f)
                curveTo(632.7f, 759.5f, 581.5f, 777.0f, 517.5f, 777.0f)
                curveTo(472.3f, 777.0f, 430.9f, 768.0f, 393.8f, 750.5f)
                moveTo(457.4f, 458.7f)
                lineTo(457.4f, 376.7f)
                curveTo(494.5f, 372.1f, 524.8f, 358.4f, 549.1f, 335.4f)
                curveTo(573.0f, 311.9f, 585.0f, 285.9f, 585.0f, 256.0f)
                curveTo(585.0f, 197.1f, 554.7f, 167.3f, 494.9f, 167.3f)
                curveTo(462.5f, 167.3f, 437.3f, 177.5f, 419.8f, 198.4f)
                curveTo(402.3f, 219.3f, 393.8f, 248.3f, 393.8f, 286.3f)
                lineTo(393.8f, 661.3f)
                curveTo(432.6f, 683.9f, 470.6f, 695.0f, 507.3f, 695.0f)
                curveTo(543.1f, 695.0f, 571.3f, 685.7f, 591.4f, 667.3f)
                curveTo(611.4f, 648.5f, 621.2f, 622.1f, 621.2f, 588.4f)
                curveTo(621.2f, 512.0f, 566.6f, 469.3f, 457.4f, 458.7f)
                close()
            }
        }
        return _beta!!
    }

private var _beta: ImageVector? = null
