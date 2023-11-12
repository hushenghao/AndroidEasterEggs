@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class
)

package com.dede.android_eggs.views.main

import android.app.Activity
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentActivity
import com.dede.android_eggs.R
import com.dede.android_eggs.main.BackPressedHandler
import com.dede.android_eggs.main.EggActionHelp
import com.dede.android_eggs.main.entity.Egg
import com.dede.android_eggs.main.entity.Egg.Companion.toEgg
import com.dede.android_eggs.ui.drawables.AlterableAdaptiveIconDrawable
import com.dede.android_eggs.ui.drawables.FontIconsDrawable
import com.dede.android_eggs.ui.views.EasterEggFooterView
import com.dede.android_eggs.ui.views.SnapshotGroupView
import com.dede.android_eggs.util.EdgeUtils
import com.dede.android_eggs.util.LocalEvent
import com.dede.android_eggs.util.ThemeUtils
import com.dede.android_eggs.views.settings.SettingsFragment
import com.dede.android_eggs.views.settings.prefs.IconShapePref
import com.dede.android_eggs.views.theme.AppTheme
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.EasterEggGroup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt
import com.dede.android_eggs.ui.Icons as FontIcons

@AndroidEntryPoint
class EasterEggsActivity : AppCompatActivity() {

    @Inject
    lateinit var easterEggs: List<@JvmSuppressWildcards BaseEasterEgg>

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtils.tryApplyOLEDTheme(this)
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
                    Box(
                        modifier = Modifier
                            .padding(delegate)
                            .background(colorScheme.surface)
                    ) {
                        LazyColumn {
                            item {
                                AndroidSnapshotView()
                                Wavy(res = R.drawable.ic_wavy_line)
                                for (easterEgg in easterEggs) {
                                    EasterEggItem(easterEgg)
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
//            IconButton(
//                onClick = {
//                },
//            ) {
//                Icon(
//                    imageVector = Icons.Rounded.Search,
//                    contentDescription = stringResource(android.R.string.search_go)
//                )
//            }
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
fun EasterEggItem(base: BaseEasterEgg) {
    val context = LocalContext.current

    var groupIndex by remember { mutableIntStateOf(0) }
    val isGroup = base is EasterEggGroup
    val egg = when (base) {
        is EasterEgg -> base
        is EasterEggGroup -> base.eggs[groupIndex]
        else -> throw UnsupportedOperationException("Unsupported type: ${base.javaClass}")
    }
    val wrapperEgg = egg.toEgg()
    var popupAnchorBounds by remember { mutableStateOf(Rect.Zero) }

    var released by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var triggerOffsetX by remember { mutableFloatStateOf(0f) }
    var trigged by remember { mutableStateOf(false) }
    if (trigged) {
        LocalView.current.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }

    LaunchedEffect(released) {
        if (released) {
            val releaseAnim = Animatable(offsetX)
            releaseAnim.animateTo(0f, animationSpec = tween(200)) {
                offsetX = value
            }
        }
    }
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(top = 12.dp)
    ) {
        EasterEggItemSub(egg, EggActionHelp.isShortcutEnable(wrapperEgg))
        Card(
            onClick = {
                EggActionHelp.launchEgg(context, wrapperEgg)
            },
            shape = shapes.extraLarge,
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth()
                .onSizeChanged { triggerOffsetX = it.width / 5f * 2 }
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .draggable(
                    reverseDirection = isRtl,
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        if (isRtl) {
                            // right drag
                            if (offsetX >= triggerOffsetX) {
                                trigged = true
                            }
                        } else {
                            // left drag
                            if (-offsetX >= triggerOffsetX) {
                                trigged = true
                            }
                        }
                        offsetX += delta
                    },
                    onDragStarted = {
                        trigged = false
                        released = false
                    },
                    onDragStopped = {
                        if (trigged) {
                            EggActionHelp.addShortcut(context, wrapperEgg)
                        }
                        released = true
                    }
                ),
        ) {
            Column(
                modifier = Modifier.padding(start = 22.dp, top = 18.dp, end = 22.dp, bottom = 18.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(egg.nameRes),
                        style = typography.headlineSmall,
                        modifier = Modifier
                            .padding(end = 10.dp, bottom = 6.dp)
                            .weight(1f, true)
                    )
                    EasterEggIcon(egg)
                }
                Row(
                    modifier = if (isGroup) {
                        Modifier.clickable {
                                // DropdownMenu style error, use native popup
                                val activity = context as Activity
                                val parent =
                                    activity.findViewById<FrameLayout>(android.R.id.content)
                                val fakeView = View(context).apply {
                                    x = popupAnchorBounds.left
                                    y = popupAnchorBounds.top
                                }
                                val params = FrameLayout.LayoutParams(
                                    popupAnchorBounds.width.roundToInt(),
                                    popupAnchorBounds.height.roundToInt()
                                )
                                parent.addView(fakeView, parent.childCount, params)
                                EggActionHelp.showEggGroupMenu(
                                    context,
                                    fakeView,
                                    base as EasterEggGroup,
                                    onSelected = {
                                        groupIndex = it
                                    },
                                    onDismiss = {
                                        parent.removeView(fakeView)
                                    })
                            }
                            .onGloballyPositioned {
                                val position = it.positionInWindow()
                                val bounds = it.boundsInParent()
                                popupAnchorBounds = Rect(
                                    position.x,
                                    position.y,
                                    position.x + bounds.width,
                                    position.y + bounds.height
                                )
                            }
                    } else Modifier
                ) {
                    Text(
                        text = Egg.VersionFormatter.create(egg.nicknameRes, egg.apiLevel)
                            .format(context).toString(),
                        style = typography.bodyMedium
                    )
                    if (isGroup) {
                        val bitmap = FontIconsDrawable(
                            context,
                            FontIcons.Rounded.expand_more,
                            24f
                        ).toBitmap()
                        Image(
                            modifier = Modifier.padding(start = 6.dp),
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = stringResource(R.string.pref_title_language_more)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EasterEggItemSub(egg: EasterEgg, enableShortcut: Boolean = true) {
    val content = LocalContext.current
    Row(
        modifier = Modifier
            .padding(horizontal = 28.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            EasterEggIcon(egg = egg, 36.dp)
            Text(
                text = Egg.ApiVersionFormatter.create(egg.apiLevel)
                    .format(content).toString(),
                modifier = Modifier.padding(start = 8.dp),
                maxLines = 2,
                style = typography.bodyMedium
            )
        }
        if (enableShortcut) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.label_add_shortcut),
                    modifier = Modifier.padding(end = 4.dp),
                    maxLines = 3,
                    style = typography.bodyMedium
                )
                val bitmap = FontIconsDrawable(content, FontIcons.Rounded.app_shortcut, 30f)
                    .toBitmap()
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = stringResource(R.string.label_add_shortcut)
                )
            }
        }
    }
}

@Composable
private fun EasterEggIcon(egg: EasterEgg, size: Dp = 44.dp) {
    if (egg.supportAdaptiveIcon) {
        val px = with(LocalDensity.current) { size.toPx().roundToInt() }
        val context = LocalContext.current
        var maskPath: String? by remember { mutableStateOf(IconShapePref.getMaskPath(context)) }
        LocalEvent.receiver(LocalLifecycleOwner.current)
            .register(IconShapePref.ACTION_CHANGED) {
                maskPath = it.getStringExtra(IconShapePref.EXTRA_ICON_SHAPE_PATH)
            }

        val bitmap = AlterableAdaptiveIconDrawable(context, egg.iconRes, maskPath).toBitmap(px, px)
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = stringResource(egg.nicknameRes),
            modifier = Modifier.size(size)
        )
    } else {
        Image(
            painter = painterResource(egg.iconRes),
            contentDescription = stringResource(egg.nicknameRes),
            modifier = Modifier.size(size)
        )
    }

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