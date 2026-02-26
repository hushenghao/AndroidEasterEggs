/*
 * Copyright 2026 The Android Open Source Project
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

package com.dede.android_eggs.ui.composes.icons.rounded

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import com.dede.android_eggs.ui.composes.icons.materialIcon

public val Icons.Rounded.Experiment: ImageVector
    get() {
        if (_experiment != null) {
            return _experiment!!
        }
        _experiment = materialIcon(
            name = "Rounded.Experiment",
            viewportWidth = 960.0f,
            viewportHeight = 960.0f
        ) {
            materialPath {
                moveTo(200.0f, 840.0f)
                quadTo(149.0f, 840.0f, 127.5f, 794.5f)
                quadTo(106.0f, 749.0f, 138.0f, 710.0f)
                lineTo(360.0f, 440.0f)
                lineTo(360.0f, 200.0f)
                lineTo(320.0f, 200.0f)
                quadTo(303.0f, 200.0f, 291.5f, 188.5f)
                quadTo(280.0f, 177.0f, 280.0f, 160.0f)
                quadTo(280.0f, 143.0f, 291.5f, 131.5f)
                quadTo(303.0f, 120.0f, 320.0f, 120.0f)
                lineTo(640.0f, 120.0f)
                quadTo(657.0f, 120.0f, 668.5f, 131.5f)
                quadTo(680.0f, 143.0f, 680.0f, 160.0f)
                quadTo(680.0f, 177.0f, 668.5f, 188.5f)
                quadTo(657.0f, 200.0f, 640.0f, 200.0f)
                lineTo(600.0f, 200.0f)
                lineTo(600.0f, 440.0f)
                lineTo(822.0f, 710.0f)
                quadTo(854.0f, 749.0f, 832.5f, 794.5f)
                quadTo(811.0f, 840.0f, 760.0f, 840.0f)
                lineTo(200.0f, 840.0f)
                close()
                moveTo(280.0f, 720.0f)
                lineTo(680.0f, 720.0f)
                lineTo(544.0f, 560.0f)
                lineTo(416.0f, 560.0f)
                lineTo(280.0f, 720.0f)
                close()
                moveTo(200.0f, 760.0f)
                lineTo(760.0f, 760.0f)
                lineTo(520.0f, 468.0f)
                lineTo(520.0f, 200.0f)
                lineTo(440.0f, 200.0f)
                lineTo(440.0f, 468.0f)
                lineTo(200.0f, 760.0f)
                close()
                moveTo(480.0f, 480.0f)
                lineTo(480.0f, 480.0f)
                lineTo(480.0f, 480.0f)
                lineTo(480.0f, 480.0f)
                lineTo(480.0f, 480.0f)
                lineTo(480.0f, 480.0f)
                close()
            }
        }
        return _experiment!!
    }

private var _experiment: ImageVector? = null
