package com.dede.android_eggs.ui.composes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.BuildConfig

/**
 * Build type bubble
 */
@Composable
@Preview
fun BuildTypeBubble() {
    @Suppress("KotlinConstantConditions")
    val bubble = when {
        BuildConfig.DEBUG -> "Debug"
        BuildConfig.FLAVOR == "foss" -> "FOSS"
        else -> return
    }
    Box(
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Text(
            text = bubble,
            modifier = Modifier
                .background(
                    Color.Red,
                    RoundedCornerShape(50)
                        .copy(bottomStart = CornerSize(0))
                )
                .padding(horizontal = 4.dp, vertical = 1.dp),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = TextUnit(11f, TextUnitType.Sp),
            lineHeight = TextUnit(11f, TextUnitType.Sp)
        )
    }
}