package com.dede.android_eggs.views.placeholder

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.dede.android_eggs.R
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.util.EdgeUtils
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.views.settings.prefs.DynamicColorPref
import com.dede.android_eggs.views.settings.prefs.NightModePref
import com.dede.android_eggs.views.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.random.Random

@AndroidEntryPoint
class PlaceholderActivity : AppCompatActivity() {

    @Inject
    lateinit var iconRes: IntArray

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.tryApplyOLEDTheme(this)
        EdgeUtils.applyEdge(window)
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Scaffold {
                    Placeholder(randomRes(), randomPath())
                }
            }
        }

        with(LocalEvent.receiver(this)) {
            register(NightModePref.ACTION_NIGHT_MODE_CHANGED) {
                recreate()
            }
            register(DynamicColorPref.ACTION_DYNAMIC_COLOR_CHANGED) {
                recreate()
            }
        }

    }

    private fun randomRes(): Int {
        val array = iconRes
        val index = Random.nextInt(array.size)
        return array[index]
    }

    private fun randomPath(): String {
        val array = resources.getStringArray(R.array.icon_shape_override_paths)
        // 排除第一个
        val index = Random.nextInt(array.size - 1) + 1
        return array[index]
    }

}

@Composable
fun Placeholder(res: Int, mask: String? = null) {
    val drawable = AlterableAdaptiveIconDrawable(LocalContext.current, res, mask)
    val px = with(LocalDensity.current) { 56.dp.toPx().roundToInt() }
    val bitmap = remember { drawable.toBitmap(px, px).asImageBitmap() }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            visibleState = remember { MutableTransitionState(false) }
                .apply { targetState = true },
            enter = scaleIn(
                initialScale = 0.3f,
                animationSpec = tween(500, delayMillis = 100)
            ) + fadeIn(animationSpec = tween(500, delayMillis = 100)),
        ) {
            Image(
                bitmap = bitmap,
                contentDescription = stringResource(R.string.app_name),
            )
        }
    }
}

@Preview
@Composable
fun PreviewPlaceholder() {
    Placeholder(R.mipmap.ic_launcher_round)
}
