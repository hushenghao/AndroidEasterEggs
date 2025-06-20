/*
 * Copyright (C) 2023 The Android Open Source Project
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

package com.android_baklava.egg.landroid

import androidx.compose.ui.graphics.Color

/** Various UI colors. */
class Colors {
    object Android {
        val Green = Color(0xFF34A853)
        val Blue = Color(0xFF4285F4)
        val Mint = Color(0xFFE8F5E9)
        val Chartreuse = Color(0xFFC6FF00)
    }
    companion object {
        val Eigengrau = Color(0xFF16161D)
        val Eigengrau2 = Color(0xFF292936)
        val Eigengrau3 = Color(0xFF3C3C4F)
        val Eigengrau4 = Color(0xFFA7A7CA)

        val Console = Color(0xFFB7B7FF)
        val Autopilot = Android.Blue
        val Track = Android.Green
        val Flag = Android.Chartreuse
    }
}
