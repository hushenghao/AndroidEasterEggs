/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.compose.material.icons.generator.vector

/**
 * Simplified representation of a vector, with root [nodes].
 *
 * @param autoMirrored a boolean that indicates if this Vector can be auto-mirrored on left to right
 * locales
 * @param nodes may either be a singleton list of the root group, or a list of root paths / groups
 * if there are multiple top level declaration
 */
class Vector(
    val autoMirrored: Boolean,
    val nodes: List<VectorNode>,
    val viewportWidth: Float,
    val viewportHeight: Float,
)

/**
 * Simplified vector node representation, as the total set of properties we need to care about
 * for Material icons is very limited.
 */
sealed class VectorNode {
    class Group(val paths: MutableList<Path> = mutableListOf()) : VectorNode()
    class Path(
        val strokeAlpha: Float,
        val fillAlpha: Float,
        val fillType: FillType,
        val nodes: List<PathNode>
    ) : VectorNode()
}
