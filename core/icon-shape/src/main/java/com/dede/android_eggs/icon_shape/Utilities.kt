/*
 * Copyright 2023 The Android Open Source Project
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

package com.dede.android_eggs.icon_shape

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.TransformResult

fun RoundedPolygon.rotated(rotate: Float): RoundedPolygon {
    return this.transformed(Matrix().apply { rotateZ(rotate) })
}

/**
 * Transforms a [RoundedPolygon] with the given [Matrix]
 */
fun RoundedPolygon.transformed(matrix: Matrix): RoundedPolygon =
    transformed { x, y ->
        val transformedPoint = matrix.map(Offset(x, y))
        TransformResult(transformedPoint.x, transformedPoint.y)
    }
