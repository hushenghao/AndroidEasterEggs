package com.android_b.egg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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

/**
 * Base [ComponentActivity] for the early Android platform Easter eggs
 * (Android 1.0 – 2.2), which only ship a static logo and a toast.
 *
 * Each API level is represented by a concrete nested subclass supplying its
 * [iconRes], [nicknameRes] and [versionName].
 */
abstract class PlatLogoActivity : ComponentActivity() {

    @get:DrawableRes
    abstract val iconRes: Int

    @get:StringRes
    abstract val nicknameRes: Int

    abstract val versionName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    /** Android 2.2 (API 8, Froyo) */
    class Froyo : PlatLogoActivity() {
        override val iconRes: Int = R.drawable.b_android_froyo
        override val nicknameRes: Int = R.string.b_nickname_android_froyo
        override val versionName: String = "2.2"
    }

    /** Android 2.0 – 2.1 (API 5–7, Eclair) */
    class Eclair : PlatLogoActivity() {
        override val iconRes: Int = R.drawable.b_android_eclair
        override val nicknameRes: Int = R.string.b_nickname_android_eclair
        override val versionName: String = "2.0"
    }

    /** Android 1.6 (API 4, Donut) */
    class Donut : PlatLogoActivity() {
        override val iconRes: Int = R.drawable.b_android_donut
        override val nicknameRes: Int = R.string.b_nickname_android_donut
        override val versionName: String = "1.6"
    }

    /** Android 1.5 (API 3, Cupcake) */
    class Cupcake : PlatLogoActivity() {
        override val iconRes: Int = R.drawable.b_android_cupcake
        override val nicknameRes: Int = R.string.b_nickname_android_cupcake
        override val versionName: String = "1.5"
    }

    /** Android 1.1 (API 2, Petit Four) */
    class PetitFour : PlatLogoActivity() {
        override val iconRes: Int = R.drawable.b_android_classic
        override val nicknameRes: Int = R.string.b_nickname_android_petit_four
        override val versionName: String = "1.1"
    }

    /** Android 1.0 (API 1, Base) */
    class Base : PlatLogoActivity() {
        override val iconRes: Int = R.drawable.b_android_classic
        override val nicknameRes: Int = R.string.b_nickname_android_base
        override val versionName: String = "1.0"
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
