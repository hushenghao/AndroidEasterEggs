package com.android_b.egg

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.dede.android_eggs.views.theme.EasterEggsTheme
import com.dede.basic.requireDrawable
import com.dede.basic.toast
import com.google.accompanist.drawablepainter.rememberDrawablePainter

class PlatLogoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val iconRes = intent.getIntExtra(EXTRA_ICON_RES, 0)
        val nicknameRes = intent.getIntExtra(EXTRA_NICKNAME_RES, 0)
        val apiLevel = intent.getIntExtra(EXTRA_API_LEVEL, 0)

        val versionName = getVersionNameByApiLevel(apiLevel)
        val nickname = getString(nicknameRes)

        setContent {
            EasterEggsTheme {
                Scaffold(
                    containerColor = Color.Transparent,
                ) {
                    val desc = "Android $versionName: $nickname"
                    val context = LocalContext.current
                    PlatLogoScreen(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize(),
                        iconRes = iconRes,
                        contentDescription = desc,
                        onClick = {
                            context.toast(desc)
                        },
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_ICON_RES = "extra_icon_res"
        const val EXTRA_NICKNAME_RES = "extra_nickname_res"
        const val EXTRA_API_LEVEL = "extra_api_level"
    }
}

private fun getVersionNameByApiLevel(apiLevel: Int): String {
    return when (apiLevel) {
        Build.VERSION_CODES.FROYO -> "2.2"
        Build.VERSION_CODES.ECLAIR_MR1 -> "2.1"
        Build.VERSION_CODES.ECLAIR_0_1 -> "2.0.1"
        Build.VERSION_CODES.ECLAIR -> "2.0"
        Build.VERSION_CODES.DONUT -> "1.6"
        Build.VERSION_CODES.CUPCAKE -> "1.5"
        Build.VERSION_CODES.BASE_1_1 -> "1.1"
        Build.VERSION_CODES.BASE -> "1.0"
        else -> "???"
    }
}

@Composable
private fun PlatLogoScreen(
    modifier: Modifier = Modifier,
    iconRes: Int,
    contentDescription: String? = null,
    onClick: () -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val imageSize = remember(maxWidth, maxHeight) {
            minOf(maxWidth, maxHeight) * 0.6f
        }
        val context = LocalContext.current
        Image(
            painter = rememberDrawablePainter(context.requireDrawable(iconRes)),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(imageSize)
                .clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = null
                ),
        )
    }
}
