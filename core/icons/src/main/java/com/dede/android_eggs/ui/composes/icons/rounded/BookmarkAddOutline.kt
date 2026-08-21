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

public val Icons.Rounded.BookmarkAddOutline: ImageVector
    get() {
        if (_bookmarkAddOutline != null) {
            return _bookmarkAddOutline!!
        }
        _bookmarkAddOutline = materialIcon(
            name = "Rounded.BookmarkAddOutline",
            viewportWidth = 960.0f,
            viewportHeight = 960.0f
        ) {
            materialPath {
                moveToRelative(480.0f, 720.0f)
                lineToRelative(-168.0f, 72.0f)
                quadToRelative(-40.0f, 17.0f, -76.0f, -6.5f)
                reflectiveQuadTo(200.0f, 719.0f)
                verticalLineToRelative(-519.0f)
                quadToRelative(0.0f, -33.0f, 23.5f, -56.5f)
                reflectiveQuadTo(280.0f, 120.0f)
                horizontalLineToRelative(200.0f)
                quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
                reflectiveQuadTo(520.0f, 160.0f)
                quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
                reflectiveQuadTo(480.0f, 200.0f)
                lineTo(280.0f, 200.0f)
                verticalLineToRelative(518.0f)
                lineToRelative(200.0f, -86.0f)
                lineToRelative(200.0f, 86.0f)
                verticalLineToRelative(-238.0f)
                quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
                reflectiveQuadTo(720.0f, 440.0f)
                quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
                reflectiveQuadTo(760.0f, 480.0f)
                verticalLineToRelative(239.0f)
                quadToRelative(0.0f, 43.0f, -36.0f, 66.5f)
                reflectiveQuadToRelative(-76.0f, 6.5f)
                lineToRelative(-168.0f, -72.0f)
                close()
                moveTo(480.0f, 200.0f)
                lineTo(280.0f, 200.0f)
                horizontalLineToRelative(240.0f)
                horizontalLineToRelative(-40.0f)
                close()
                moveTo(680.0f, 280.0f)
                horizontalLineToRelative(-40.0f)
                quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
                reflectiveQuadTo(600.0f, 240.0f)
                quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
                reflectiveQuadTo(640.0f, 200.0f)
                horizontalLineToRelative(40.0f)
                verticalLineToRelative(-40.0f)
                quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
                reflectiveQuadTo(720.0f, 120.0f)
                quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
                reflectiveQuadTo(760.0f, 160.0f)
                verticalLineToRelative(40.0f)
                horizontalLineToRelative(40.0f)
                quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
                reflectiveQuadTo(840.0f, 240.0f)
                quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
                reflectiveQuadTo(800.0f, 280.0f)
                horizontalLineToRelative(-40.0f)
                verticalLineToRelative(40.0f)
                quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
                reflectiveQuadTo(720.0f, 360.0f)
                quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
                reflectiveQuadTo(680.0f, 320.0f)
                verticalLineToRelative(-40.0f)
                close()
            }
        }
        return _bookmarkAddOutline!!
    }

private var _bookmarkAddOutline: ImageVector? = null
