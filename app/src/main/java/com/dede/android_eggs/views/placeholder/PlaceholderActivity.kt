package com.dede.android_eggs.views.placeholder

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.views.main.compose.EasterEggLogo
import com.dede.android_eggs.views.settings.compose.prefs.DynamicColorPrefUtil
import com.dede.android_eggs.views.settings.compose.prefs.ThemePrefUtil
import com.dede.android_eggs.views.theme.AppTheme
import com.dede.basic.isAdaptiveIconDrawable
import com.dede.basic.provider.EasterEgg
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random

// todo Move to embedding-splits module
@AndroidEntryPoint
class PlaceholderActivity : AppCompatActivity() {

    @Inject
    lateinit var pureEasterEggs: List<@JvmSuppressWildcards EasterEgg>

    private lateinit var iconRes: List<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.tryApplyOLEDTheme(this)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        iconRes = pureEasterEggs.filter { isAdaptiveIconDrawable(it.iconRes) }
            .map { it.iconRes }

        setContent {
            AppTheme {
                @Suppress("UnusedMaterial3ScaffoldPaddingParameter")
                Scaffold {
                    Placeholder(randomRes())
                }
            }
        }

        with(LocalEvent.receiver(this)) {
            register(ThemePrefUtil.ACTION_NIGHT_MODE_CHANGED) {
                recreate()
            }
            register(DynamicColorPrefUtil.ACTION_DYNAMIC_COLOR_CHANGED) {
                recreate()
            }
        }

    }

    private fun randomRes(): Int {
        val array = iconRes
        val index = Random.nextInt(array.size)
        return array[index]
    }

}

@Composable
fun Placeholder(res: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        AnimatedVisibility(
            visibleState = remember { MutableTransitionState(false) }
                .apply { targetState = true },
            enter = scaleIn(
                initialScale = 0.3f,
                animationSpec = tween(500, delayMillis = 100)
            ) + fadeIn(animationSpec = tween(500, delayMillis = 100)),
        ) {
            EasterEggLogo(
                modifier = Modifier.size(56.dp),
                res = res,
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
