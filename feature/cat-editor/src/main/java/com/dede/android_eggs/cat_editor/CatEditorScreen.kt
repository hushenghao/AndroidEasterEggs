@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.dede.android_eggs.cat_editor

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CenterFocusWeak
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Flip
import androidx.compose.material.icons.rounded.GridOff
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dede.android_eggs.cat_editor.CaptureControllerDelegate.Companion.rememberCaptureControllerDelegate
import com.dede.android_eggs.cat_editor.CatEditorRecords.Companion.rememberCatEditorRecords
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.navigation.LocalNavController
import com.dede.android_eggs.ui.composes.icons.rounded.Cat
import com.dede.android_eggs.views.settings.compose.basic.rememberPrefBoolState
import com.dede.basic.copy
import com.dede.basic.toast
import com.dede.basic.trimZeroAndDot
import com.dede.basic.utils.ShareCatUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import androidx.appcompat.R as AppCompatR
import com.dede.android_eggs.resources.R as StringR

@Module
@InstallIn(SingletonComponent::class)
object CatEditorScreen : EasterEggsDestination, EasterEggsDestination.Provider {
    override val route: String = "cat_editor"

    @Composable
    override fun Content() {
        CatEditorScreen()
    }

    @IntoSet
    @Provides
    override fun provider(): EasterEggsDestination = this
}

@Composable
fun CatEditorScreen() {
    val captureController = rememberCaptureControllerDelegate()

    val navController = LocalNavController.current
    val context = LocalContext.current

    val catSeedState = rememberSaveable { mutableLongStateOf(Utilities.randomSeed()) }
    var catSeed by catSeedState
    val catName = stringResource(R.string.default_cat_name, catSeed)// full seed name

    val colorPaletteState = rememberSaveable { mutableStateOf(false) }

    val catEditorRecords = rememberCatEditorRecords(firstRecord = CatEditorRecords.seed(catSeed))
    // split with cat seed
    val catEditorController = rememberCatEditorController(catSeed)

    LaunchedEffect(catEditorController) {
        snapshotFlow { catEditorController.selectPart }
            .distinctUntilChanged()
            .collect { part ->
                colorPaletteState.value = part != CatEditorController.UNSELECTED_PART
            }
    }

    var moreOptionsPopVisible by remember { mutableStateOf(false) }
    val inputSeedDialogState = remember { mutableStateOf(false) }

    val rememberCatsDialogState = rememberSaveable { mutableStateOf(false) }
    var isRememberCat by remember { mutableStateOf(false) }
    var isRememberCatProcessing by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(catSeed, catEditorController.colorListVersion, rememberCatsDialogState.value) {
        if (rememberCatsDialogState.value) {
            return@LaunchedEffect
        }
        isRememberCatProcessing = true
        launch {
            isRememberCat = CatRememberDataStore.isFavorite(catSeed, catEditorController.colorList)
            isRememberCatProcessing = false
        }
    }

    fun updateCatSeed(seed: Long, colors: List<Color>? = null) {
        catSeed = seed
        if (colors == null) {
            catEditorController.updateColors(catSeed)
            catEditorRecords.addRecord(CatEditorRecords.seed(catSeed))
        } else {
            catEditorController.updateColors(colors)
            catEditorRecords.addRecord(CatEditorRecords.colors(colors, catSeed))
        }
    }

    val catSvgDialogState = remember { mutableStateOf(false) }
    var catSvgText by remember { mutableStateOf("") }

    val goBackButton: @Composable () -> Unit = {
        IconButton(
            onClick = {
                CatEditorRecords.restoreRecord(
                    catEditorRecords.goBack(),
                    catEditorController,
                    catSeedState
                )
            },
            enabled = catEditorRecords.canGoBack()
        ) {
            Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
        }
    }
    val goNextButton: @Composable () -> Unit = {
        IconButton(
            onClick = {
                CatEditorRecords.restoreRecord(
                    catEditorRecords.goNext(),
                    catEditorController,
                    catSeedState
                )
            },
            enabled = catEditorRecords.canGoNext()
        ) {
            Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
        }
    }
    val paletteButton: @Composable () -> Unit = {
        IconButton(
            onClick = { colorPaletteState.value = true },
            enabled = catEditorController.hasSelectedPart
        ) {
            Icon(imageVector = Icons.Rounded.Palette, contentDescription = null)
        }
    }
    val gridButton: @Composable () -> Unit = {
        IconButton(
            onClick = { catEditorController.isGridVisible = !catEditorController.isGridVisible }
        ) {
            Crossfade(catEditorController.isGridVisible) {
                if (it) {
                    Icon(imageVector = Icons.Rounded.GridOff, contentDescription = null)
                } else {
                    Icon(imageVector = Icons.Rounded.GridOn, contentDescription = null)
                }
            }
        }
    }
    val mirrorButton: @Composable () -> Unit = {
        IconButton(onClick = {
            catEditorController.resetGraphicsLayer()
            catEditorController.isMirrorMode = !catEditorController.isMirrorMode
        }) {
            val rotationY by animateFloatAsState(if (catEditorController.isMirrorMode) 180f else 0f)
            Icon(
                modifier = Modifier.graphicsLayer {
                    this.rotationY = rotationY
                },
                imageVector = Icons.Rounded.Flip,
                contentDescription = null
            )
        }
    }
    val zoomIn: @Composable () -> Unit = {
        IconButton(
            onClick = { catEditorController.zoom *= S_STEP },
            enabled = catEditorController.zoom < S_MAX
        ) {
            Icon(
                imageVector = Icons.Rounded.ZoomIn,
                contentDescription = null
            )
        }
    }
    val zoomOut: @Composable () -> Unit = {
        IconButton(
            onClick = { catEditorController.zoom /= S_STEP },
            enabled = catEditorController.zoom > S_MIN
        ) {
            Icon(
                imageVector = Icons.Rounded.ZoomOut,
                contentDescription = null
            )
        }
    }
    val resetGraphicsLayer: @Composable () -> Unit = {
        IconButton(
            onClick = { catEditorController.resetGraphicsLayer() },
        ) {
            Icon(
                imageVector = Icons.Rounded.CenterFocusWeak,
                contentDescription = null
            )
        }
    }
    val saveButton: @Composable () -> Unit = {
        SaveCatButton(
            catName = catName,
            captureController = captureController,
        )
    }
    val shareButton: @Composable () -> Unit = {
        SaveCatButton(
            imageVector = Icons.Rounded.Share,
            catName = catName,
            captureController = captureController,
            onCatSaved = { _, uri, catName ->
                if (uri != null) {
                    ShareCatUtils.shareCatOnly(context, uri, catName)
                }
            }
        )
    }
    val copyButton: @Composable () -> Unit = {
        IconButton(onClick = { context.copy(catSeed.toString()) }) {
            Icon(
                imageVector = Icons.Rounded.ContentCopy,
                contentDescription = stringResource(android.R.string.copy)
            )
        }
    }
    val svgButton: @Composable () -> Unit = {
        IconButton(onClick = {
            catSvgText = CatParts.toSvg(
                catEditorController.colorList, catEditorController.isMirrorMode
            )
            Log.i("CatEditor", "\n" + catSvgText)
            catSvgDialogState.value = true
        }) {
            Icon(
                imageVector = Icons.Rounded.Code,
                contentDescription = null
            )
        }
    }
    val inputCatButton: @Composable () -> Unit = {
        IconButton(onClick = { inputSeedDialogState.value = true }) {
            Icon(imageVector = Icons.Rounded.Draw, contentDescription = null)
        }
    }
    val refreshButton: @Composable () -> Unit = {
        IconButton(onClick = {
            updateCatSeed(Utilities.randomSeed())
        }) {
            Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null)
        }
    }
    val favoriteButton: @Composable () -> Unit = {
        IconButton(
            onClick = {
                isRememberCatProcessing = true
                scope.launch {
                    if (isRememberCat) {
                        CatRememberDataStore.forget(catSeed, catEditorController.colorList)
                    } else {
                        CatRememberDataStore.remember(catSeed, catEditorController.colorList)
                    }
                    isRememberCat = !isRememberCat
                    isRememberCatProcessing = false
                }
            },
            enabled = !isRememberCatProcessing
        ) {
            Crossfade(targetState = isRememberCat) {
                if (it) {
                    Icon(imageVector = Icons.Rounded.Favorite, contentDescription = null)
                } else {
                    Icon(imageVector = Icons.Rounded.FavoriteBorder, contentDescription = null)
                }
            }
        }
    }

    val bottomMenuButtonList = remember {
        listOf(
            goBackButton, goNextButton,
            paletteButton, gridButton,
            favoriteButton, saveButton,
            mirrorButton, zoomIn, zoomOut, resetGraphicsLayer,
            shareButton, copyButton, svgButton,
            inputCatButton, refreshButton,
        )
    }

    var bottomButtonCount by remember { mutableIntStateOf(bottomMenuButtonList.size) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    TooltipBox(
                        positionProvider = rememberTooltipPositionProvider(TooltipAnchorPosition.Below),
                        tooltip = {
                            PlainTooltip { Text(stringResource(StringR.string.label_back)) }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null,
                                tint = colorScheme.onSurface
                            )
                        }
                    }
                },
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(StringR.string.cat_editor),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = stringResource(R.string.label_cat_seed, catSeed),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Normal
                            ),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { rememberCatsDialogState.value = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Cat,
                            contentDescription = null,
                            tint = colorScheme.onSurface
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomOptionsBar(
                totalOptionsCount = bottomMenuButtonList.size,
                onVisibleOptionCountChanged = { visibleCount, _ ->
                    bottomButtonCount = visibleCount
                },
                onMoreOptionsClick = {
                    moreOptionsPopVisible = !moreOptionsPopVisible
                }
            ) {
                for (i in 0..<bottomButtonCount) {
                    bottomMenuButtonList[i]()
                }
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            CatEditor(
                controller = catEditorController,
                captureController = captureController,
            )

            val zoomValue by remember(catEditorController) {
                derivedStateOf { "x${"%.1f".format(catEditorController.zoom).trimZeroAndDot()}" }
            }
            ZoomValuePopup(
                zoomValue = zoomValue,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.TopCenter),
            )

            ColorPaletteDialog(
                visibleState = colorPaletteState,
                selectedColor = catEditorController.getSelectedPartColor(Color.White),
                onColorSelected = { color ->
                    if (!catEditorController.hasSelectedPart) {
                        return@ColorPaletteDialog
                    }

                    catEditorController.setSelectedPartColor(color)
                    catEditorRecords.addRecord(
                        CatEditorRecords.colors(
                            catEditorController.colorList,
                            catSeed
                        )
                    )
                }
            )

            CatSeedInputDialog(inputSeedDialogState, onConfirm = { seed ->
                updateCatSeed(seed)
            })

            CatSvgCodeDialog(
                catSvgDialogState,
                cat = remember(
                    catSeed,
                    catEditorController.colorListVersion,
                    catEditorController.isMirrorMode
                ) {
                    Cat.createCat(
                        catSeed,
                        catEditorController.colorList,
                        catEditorController.isMirrorMode
                    )
                },
                svg = catSvgText,
            )

            if (bottomButtonCount < bottomMenuButtonList.size) {
                MoreOptionsPopup(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(horizontal = 14.dp),
                    visible = moreOptionsPopVisible,
                ) {
                    for (i in bottomButtonCount..<bottomMenuButtonList.size) {
                        bottomMenuButtonList[i]()
                    }
                }
            }
        }
    }

    CatRememberBottomSheet(
        visibleState = rememberCatsDialogState,
        onCatSelected = { cat ->
            updateCatSeed(cat.seed, cat.colors)
        }
    )
}

@Composable
private fun SaveCatButton(
    catName: String,
    captureController: CaptureControllerDelegate,
    imageVector: ImageVector = Icons.Rounded.Save,
    contentDescription: String? = null,
    onCatSaved: (bitmap: Bitmap, uri: Uri?, catName: String) -> Unit = { _, _, _ -> },
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isSaving by remember { mutableStateOf(false) }

    fun saveCatToAlbum() {
        isSaving = true
        val deferred = captureController.captureAsync()
        scope.launch {
            val bitmap = deferred.await().asAndroidBitmap()
            val uri = ShareCatUtils.saveCat(context, bitmap, catName)
            onCatSaved(bitmap, uri, catName)
            if (uri != null) {
                context.toast("🐱")
            } else {
                context.toast("🚫")
            }
            isSaving = false
        }
    }

    val storagePermissionState = rememberMultiplePermissionsStateCompat(
        permissions = ShareCatUtils.storagePermissions,
        isNotRequire = ShareCatUtils.isNotRequireStoragePermissions,
        onPermissionsResult = { result ->
            if (result.all { it.value }) {
                saveCatToAlbum()
            } else {
                context.toast("🚫")
            }
        }
    )
    IconButton(
        onClick = {
            when {
                storagePermissionState.allPermissionsGranted -> saveCatToAlbum()
                storagePermissionState.shouldShowRationale -> context.toast("🚫")
                else -> storagePermissionState.launchMultiplePermissionRequest()
            }
        },
        enabled = !isSaving
    ) {
        Icon(imageVector = imageVector, contentDescription = contentDescription)
    }
}

@Composable
private fun ZoomValuePopup(zoomValue: String, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(zoomValue) {
        visible = true
        delay(1000)
        visible = false
    }
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(500)),
        exit = fadeOut(tween(500)),
    ) {
        Card(
            shape = CircleShape,
            colors = cardColors(containerColor = colorScheme.surfaceColorAtElevation(4.dp))
        ) {
            Text(
                text = zoomValue,
                modifier = Modifier
                    .padding(12.dp)
                    .sizeIn(minWidth = 30.dp),
                style = typography.labelMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun BottomOptionsBar(
    totalOptionsCount: Int,
    onVisibleOptionCountChanged: (visibleCount: Int, hasMoreOptions: Boolean) -> Unit,
    onMoreOptionsClick: () -> Unit,
    options: @Composable RowScope.() -> Unit,
) {
    BottomAppBar(
        actions = {
            var moreOptionsVisible by remember { mutableStateOf(false) }
            var iconButtonCount by remember { mutableIntStateOf(totalOptionsCount) }

            LaunchedEffect(iconButtonCount) {
                onVisibleOptionCountChanged(iconButtonCount, iconButtonCount < totalOptionsCount)
            }

            val density = LocalDensity.current
            val componentSize = LocalMinimumInteractiveComponentSize.current
            Row(
                modifier = Modifier
                    .weight(1f)
                    .onSizeChanged {
                        val groupWidth = with(density) { it.width.toDp() }
                        val count = (groupWidth / componentSize).toInt()
                        if (count < totalOptionsCount) {
                            // Add 'more options' button
                            iconButtonCount = count - 1
                            moreOptionsVisible = true
                        } else {
                            // Show all options
                            iconButtonCount = totalOptionsCount
                            moreOptionsVisible = false
                        }
                    }
            ) {
                options()

                // more options button
                if (moreOptionsVisible) {
                    Spacer(modifier = Modifier.weight(1f))

                    FilledTonalIconButton(onClick = onMoreOptionsClick) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = stringResource(AppCompatR.string.abc_action_menu_overflow_description)
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun MoreOptionsPopup(
    modifier: Modifier = Modifier,
    visible: Boolean,
    content: @Composable RowScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        label = "More Options Visibility",
        enter = slideInVertically(spring(Spring.DampingRatioLowBouncy)) { it / 2 } + fadeIn(),
        exit = slideOutVertically(spring(Spring.DampingRatioLowBouncy)) { it / 2 } + fadeOut(),
    ) {
        Card(
            modifier = Modifier.padding(vertical = 14.dp),
            shape = CircleShape,
            colors = cardColors(containerColor = colorScheme.surfaceColorAtElevation(4.dp))
        ) {
            var moreOptionGuide by rememberPrefBoolState("cat_editor_more_option_guide", true)
            val scrollState = rememberScrollState()
            LaunchedEffect(scrollState) {
                if (moreOptionGuide && scrollState.canScrollForward) {
                    scrollState.animateScrollTo(scrollState.maxValue, tween(600, delayMillis = 200))
                    scrollState.animateScrollTo(0, tween(600, delayMillis = 500))
                    moreOptionGuide = false
                }
            }
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .horizontalScroll(scrollState),
            ) {
                Spacer(modifier = Modifier.width(4.dp))
                content()
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}
