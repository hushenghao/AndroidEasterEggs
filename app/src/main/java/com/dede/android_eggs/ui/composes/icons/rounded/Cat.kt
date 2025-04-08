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

package com.dede.android_eggs.ui.composes.icons.rounded

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import com.dede.android_eggs.ui.composes.icons.materialIcon

public val Icons.Rounded.Cat: ImageVector
    get() {
        if (_cat != null) {
            return _cat!!
        }
        _cat = materialIcon(
            name = "Rounded.Cat",
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ) {
            materialPath {
                moveTo(12.0f, 2.0f)
                curveTo(6.5f, 2.0f, 2.0f, 6.5f, 2.0f, 12.0f)
                curveToRelative(0.0f, 5.5f, 4.5f, 10.0f, 10.0f, 10.0f)
                reflectiveCurveToRelative(10.0f, -4.5f, 10.0f, -10.0f)
                curveTo(22.0f, 6.5f, 17.5f, 2.0f, 12.0f, 2.0f)
                close()
                moveTo(5.5f, 11.0f)
                curveToRelative(0.0f, -1.6f, 3.0f, -1.6f, 3.0f, 0.0f)
                curveTo(8.5f, 12.7f, 5.5f, 12.7f, 5.5f, 11.0f)
                close()
                moveTo(17.5f, 14.6f)
                curveToRelative(-0.6f, 1.0f, -1.7f, 1.7f, -2.9f, 1.7f)
                curveToRelative(-1.1f, 0.0f, -2.0f, -0.6f, -2.6f, -1.4f)
                curveToRelative(-0.6f, 0.9f, -1.6f, 1.4f, -2.7f, 1.4f)
                curveToRelative(-1.3f, 0.0f, -2.3f, -0.7f, -2.9f, -1.8f)
                curveToRelative(-0.2f, -0.3f, 0.0f, -0.7f, 0.3f, -0.8f)
                curveToRelative(0.3f, -0.2f, 0.7f, 0.0f, 0.8f, 0.3f)
                curveToRelative(0.3f, 0.7f, 1.0f, 1.1f, 1.8f, 1.1f)
                curveToRelative(0.9f, 0.0f, 1.6f, -0.5f, 1.9f, -1.3f)
                curveToRelative(-0.2f, -0.2f, -0.4f, -0.4f, -0.4f, -0.7f)
                curveToRelative(0.0f, -1.3f, 2.3f, -1.3f, 2.3f, 0.0f)
                curveToRelative(0.0f, 0.3f, -0.2f, 0.6f, -0.4f, 0.7f)
                curveToRelative(0.3f, 0.8f, 1.1f, 1.3f, 1.9f, 1.3f)
                curveToRelative(0.8f, 0.0f, 1.5f, -0.6f, 1.8f, -1.1f)
                curveToRelative(0.2f, -0.3f, 0.6f, -0.4f, 0.9f, -0.2f)
                curveTo(17.6f, 13.9f, 17.7f, 14.3f, 17.5f, 14.6f)
                close()
                moveTo(15.5f, 11.0f)
                curveToRelative(0.0f, -1.6f, 3.0f, -1.6f, 3.0f, 0.0f)
                curveTo(18.5f, 12.7f, 15.5f, 12.7f, 15.5f, 11.0f)
                close()
            }
            materialPath {
                moveTo(5.2f, 1.0f)
                lineToRelative(4.1000004f, 4.2f)
                lineToRelative(-5.0f, 2.1000004f)
                close()
            }
            materialPath {
                moveTo(18.8f, 1.0f)
                lineToRelative(-4.0999994f, 4.2f)
                lineToRelative(5.000001f, 2.1000004f)
                close()
            }
        }
        return _cat!!
    }

private var _cat: ImageVector? = null
