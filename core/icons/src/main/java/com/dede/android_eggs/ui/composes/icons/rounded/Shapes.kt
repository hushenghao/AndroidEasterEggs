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

public val Icons.Rounded.Shapes: ImageVector
    get() {
        if (_shapes != null) {
            return _shapes!!
        }
        _shapes = materialIcon(
            name = "Rounded.Shapes",
            viewportWidth = 960.0f,
            viewportHeight = 960.0f
        ) {
            materialPath {
                moveTo(600.0f, 600.0f)
                close()
                moveTo(400.0f, 880.0f)
                quadToRelative(-33.0f, 0.0f, -56.5f, -23.5f)
                reflectiveQuadTo(320.0f, 800.0f)
                verticalLineToRelative(-40.0f)
                quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
                reflectiveQuadTo(360.0f, 720.0f)
                quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
                reflectiveQuadTo(400.0f, 760.0f)
                verticalLineToRelative(40.0f)
                horizontalLineToRelative(400.0f)
                verticalLineToRelative(-400.0f)
                horizontalLineToRelative(-40.0f)
                quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
                reflectiveQuadTo(720.0f, 360.0f)
                quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
                reflectiveQuadTo(760.0f, 320.0f)
                horizontalLineToRelative(40.0f)
                quadToRelative(33.0f, 0.0f, 56.5f, 23.5f)
                reflectiveQuadTo(880.0f, 400.0f)
                verticalLineToRelative(400.0f)
                quadToRelative(0.0f, 33.0f, -23.5f, 56.5f)
                reflectiveQuadTo(800.0f, 880.0f)
                lineTo(400.0f, 880.0f)
                close()
                moveTo(360.0f, 640.0f)
                quadToRelative(-117.0f, 0.0f, -198.5f, -81.5f)
                reflectiveQuadTo(80.0f, 360.0f)
                quadToRelative(0.0f, -117.0f, 81.5f, -198.5f)
                reflectiveQuadTo(360.0f, 80.0f)
                quadToRelative(117.0f, 0.0f, 198.5f, 81.5f)
                reflectiveQuadTo(640.0f, 360.0f)
                quadToRelative(0.0f, 117.0f, -81.5f, 198.5f)
                reflectiveQuadTo(360.0f, 640.0f)
                close()
                moveTo(360.0f, 560.0f)
                quadToRelative(83.0f, 0.0f, 141.5f, -58.5f)
                reflectiveQuadTo(560.0f, 360.0f)
                quadToRelative(0.0f, -83.0f, -58.5f, -141.5f)
                reflectiveQuadTo(360.0f, 160.0f)
                quadToRelative(-83.0f, 0.0f, -141.5f, 58.5f)
                reflectiveQuadTo(160.0f, 360.0f)
                quadToRelative(0.0f, 83.0f, 58.5f, 141.5f)
                reflectiveQuadTo(360.0f, 560.0f)
                close()
                moveTo(360.0f, 360.0f)
                close()
            }
        }
        return _shapes!!
    }

private var _shapes: ImageVector? = null
