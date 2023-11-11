@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

package com.dede.android_eggs.views.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentActivity
import com.android_u.egg.landroid.toLocalPx
import com.dede.android_eggs.R
import com.dede.android_eggs.main.BackPressedHandler
import com.dede.android_eggs.main.EggActionHelp
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.main.entity.Egg.Companion.toEgg
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.ui.views.EasterEggFooterView
import com.dede.android_eggs.ui.views.SnapshotGroupView
import com.dede.android_eggs.util.EdgeUtils
import com.dede.android_eggs.views.settings.SettingsFragment
import com.dede.android_eggs.views.theme.AppTheme
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggGroup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class EasterEggsActivity : AppCompatActivity() {

    @Inject
    lateinit var easterEggs: List<@JvmSuppressWildcards BaseEasterEgg>

    override fun onCreate(savedInstanceState: Bundle?) {
        EdgeUtils.applyEdge(window)
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Scaffold(
                    topBar = { TitleBar() }
                ) { contentPadding ->
                    val delegate = object : PaddingValues by contentPadding {
                        override fun calculateBottomPadding() = Dp.Hairline
                    }
                    Box(modifier = Modifier.padding(delegate)) {
                        LazyColumn {
                            item {
                                AndroidSnapshotView()
                                Wavy(res = R.drawable.ic_wavy_line)
                                for (easterEgg in easterEggs) {
                                    if (easterEgg is EasterEgg) {
                                        EasterEggItem(egg = easterEgg)
                                    } else if (easterEgg is EasterEggGroup) {
                                        EasterEggItem(egg = easterEgg.eggs[0])
                                    }
                                }
                                Wavy(res = R.drawable.ic_wavy_line)
                                AndroidFooterView()
                                Spacer(Modifier.height(contentPadding.calculateBottomPadding()))
                            }
                        }
                    }
                }
            }
        }

        BackPressedHandler(this).register()
    }
}

@Composable
fun TitleBar() {
    val activity = LocalContext.current as FragmentActivity

    var startRotate by remember { mutableStateOf(false) }
    val rotateAnim by animateFloatAsState(
        targetValue = if (startRotate) 360f else 0f,
        animationSpec = tween(500),
        label = "SettingIconRotate"
    )

    CenterAlignedTopAppBar(
        title = {
            Text(text = stringResource(R.string.app_name))
        },
        actions = {
            IconButton(
                onClick = {
                },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = stringResource(android.R.string.search_go)
                )
            }
            IconButton(
                onClick = {
                    SettingsFragment().apply {
                        onSlide = {
                            this.onSlide = null
                            startRotate = false
                        }
                    }.show(activity.supportFragmentManager, "Settings")
                    startRotate = true
                },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = stringResource(R.string.label_settings),
                    modifier = Modifier.rotate(rotateAnim)
                )
            }
        }
    )
}

@Composable
fun AndroidSnapshotView() {
    AndroidView(
        factory = { SnapshotGroupView(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    )
}

@Composable
fun AndroidFooterView() {
    AndroidView(
        factory = { EasterEggFooterView(it) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun Wavy(res: Int) {
    Image(
        painter = painterResource(id = res),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp)
    )
}

@Composable
fun EasterEggItem(egg: EasterEgg) {
    val context = LocalContext.current

    var released by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(released) {
        if (released) {
            val releaseAnim = Animatable(offsetX)
            releaseAnim.animateTo(0f, animationSpec = tween(200)) {
                offsetX = value
            }
        }
    }
    Box {
        EasterEggItemSub(egg)
        Card(
            onClick = {
                EggActionHelp.launchEgg(context, egg.toEgg())
            },
            shape = shapes.extraLarge,
            modifier = Modifier
                .padding(start = 12.dp, top = 12.dp, end = 12.dp)
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        offsetX += delta
                    },
                    onDragStarted = {
                        released = false
                    },
                    onDragStopped = {
                        released = true
                    }
                ),
        ) {
            Column(
                modifier = Modifier.padding(start = 22.dp, top = 20.dp, end = 22.dp, bottom = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = egg.nameRes),
                        style = typography.headlineSmall,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    EasterEggIcon(egg)
                }
                Text(
                    text = Egg.VersionFormatter.create(egg.nicknameRes, egg.apiLevel)
                        .format(context).toString(),
                    style = typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun EasterEggItemSub(egg: EasterEgg) {

}

@Composable
private fun EasterEggIcon(egg: EasterEgg) {
    val dp = 46.dp
    if (egg.supportAdaptiveIcon) {
        val size = dp.toLocalPx().toInt()
        val bitmap = AlterableAdaptiveIconDrawable(LocalContext.current, egg.iconRes)
            .toBitmap(size, size)
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = stringResource(id = egg.nicknameRes),
            modifier = Modifier.size(dp)
        )
    } else {
        Image(
            painter = painterResource(egg.iconRes),
            contentDescription = stringResource(id = egg.nicknameRes),
            modifier = Modifier.size(dp)
        )
    }

}