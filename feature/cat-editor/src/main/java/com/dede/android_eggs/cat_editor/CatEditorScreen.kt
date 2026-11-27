package com.dede.android_eggs.cat_editor

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.computedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavKey
import com.dede.android_eggs.cat_editor.editor.CatEditor
import com.dede.android_eggs.cat_editor.editor.CatEditorController
import com.dede.android_eggs.cat_editor.editor.CatEditorRecords
import com.dede.android_eggs.cat_editor.editor.CatEditorRecords.Companion.rememberCatEditorRecords
import com.dede.android_eggs.cat_editor.editor.rememberCatEditorController
import com.dede.android_eggs.cat_editor.model.CatParts
import com.dede.android_eggs.cat_editor.ui.CatEditorFloatingToolbar
import com.dede.android_eggs.cat_editor.ui.CatRememberBottomSheet
import com.dede.android_eggs.cat_editor.ui.CatSeedInputDialog
import com.dede.android_eggs.cat_editor.ui.CatSvgCodeDialog
import com.dede.android_eggs.cat_editor.ui.ZoomHapticFeedback
import com.dede.android_eggs.cat_editor.ui.ZoomValuePopup
import com.dede.android_eggs.cat_editor.util.CaptureControllerDelegate.Companion.rememberCaptureControllerDelegate
import com.dede.android_eggs.cat_editor.util.Utilities
import com.dede.android_eggs.cat_editor.util.rememberPrintCatAction
import com.dede.android_eggs.cat_editor.util.rememberSaveCatAction
import com.dede.android_eggs.color_picker.ColorPickerDialog
import com.dede.android_eggs.color_picker.ColorPickerUtilities
import com.dede.android_eggs.composable.appbar.HazeScaffold
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.navigation.LocalNavigator
import com.dede.basic.copy
import com.dede.basic.trimZeroAndDot
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import com.dede.android_eggs.resources.R as StringR

@Module
@InstallIn(SingletonComponent::class)
object CatEditorScreen : EasterEggsDestination, EasterEggsDestination.Provider {
    override val route: NavKey = EasterEggsDestination.CatEditor()

    @Composable
    override fun Content(route: NavKey) {
        val editorRoute = route as? EasterEggsDestination.CatEditor
        CatEditorScreen(editorRoute?.seed)
    }

    @IntoSet
    @Provides
    override fun provider(): EasterEggsDestination = this
}

@HiltViewModel
internal class CatEditorViewModel @Inject constructor(
    private val catRepository: CatRepository,
) : ViewModel() {

    suspend fun remember(seed: Long, colors: List<Color>? = null) {
        catRepository.remember(seed, colors)
    }

    suspend fun forgetById(id: Long) {
        catRepository.forgetById(id)
    }

    suspend fun forget(seed: Long, colors: List<Color>? = null) {
        catRepository.forget(seed, colors)
    }

    suspend fun isFavorite(seed: Long, colors: List<Color>): Boolean {
        return catRepository.isFavorite(seed, colors)
    }

    suspend fun getAllCats(): List<Cat> {
        return catRepository.getAllCats()
    }
}

@Composable
internal fun CatEditorScreen(
    initialSeed: Long? = null,
    viewModel: CatEditorViewModel = hiltViewModel(),
) {
    val captureController = rememberCaptureControllerDelegate()

    val navigator = LocalNavigator.current
    val context = LocalContext.current

    val catSeedState = rememberSaveable {
        mutableLongStateOf(initialSeed ?: Utilities.randomSeed())
    }
    var catSeed by catSeedState
    val catName = stringResource(R.string.default_cat_name, catSeed)// full seed name

    var colorPaletteVisible by rememberSaveable { mutableStateOf(false) }

    val catEditorRecords = rememberCatEditorRecords(firstRecord = CatEditorRecords.seed(catSeed))
    // split with cat seed
    val catEditorController = rememberCatEditorController(catSeed)

    LaunchedEffect(catEditorController) {
        snapshotFlow { catEditorController.selectPart }
            .distinctUntilChanged()
            .collect { part ->
                colorPaletteVisible = part != CatEditorController.UNSELECTED_PART
            }
    }

    var inputSeedDialogVisible by remember { mutableStateOf(false) }

    var rememberCatsDialogVisible by rememberSaveable { mutableStateOf(false) }
    var rememberedCats by remember { mutableStateOf<List<Cat>>(emptyList(), neverEqualPolicy()) }
    var isRememberCat by remember { mutableStateOf(false) }
    var isRememberCatProcessing by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(rememberCatsDialogVisible) {
        if (rememberCatsDialogVisible) {
            rememberedCats = viewModel.getAllCats()
        }
    }

    LaunchedEffect(catSeed, catEditorController.colorListVersion, rememberCatsDialogVisible) {
        if (rememberCatsDialogVisible) {
            return@LaunchedEffect
        }
        isRememberCatProcessing = true
        launch {
            isRememberCat = viewModel.isFavorite(catSeed, catEditorController.colorList)
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

    var catSvgDialogVisible by remember { mutableStateOf(false) }
    var catSvgText by remember { mutableStateOf("") }

    val saveCatAction = rememberSaveCatAction(catName, captureController)
    val printCatAction = rememberPrintCatAction(catName, captureController)

    HazeScaffold(
        title = stringResource(StringR.string.cat_editor),
        subtitle = stringResource(R.string.label_cat_seed, catSeed),
        onBackClick = navigator::goBack,
        actions = {
            IconButton(onClick = { rememberCatsDialogVisible = true }) {
                Icon(
                    imageVector = Icons.Rounded.FavoriteBorder,
                    contentDescription = null,
                    tint = colorScheme.onSurface
                )
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
                computedStateOf { "x${"%.1f".format(catEditorController.zoom).trimZeroAndDot()}" }
            }
            ZoomValuePopup(
                zoomValue = zoomValue,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.TopCenter),
            )

            ZoomHapticFeedback(zoom = catEditorController.zoom)

            ColorPickerDialog(
                visible = colorPaletteVisible,
                initialColor = catEditorController.getSelectedPartColor(Color.White),
                withAlphaPalette = true,
                onColorSelected = { color ->
                    if (!catEditorController.hasSelectedPart) {
                        return@ColorPickerDialog
                    }

                    catEditorController.setSelectedPartColor(color)
                    catEditorRecords.addRecord(
                        CatEditorRecords.colors(
                            catEditorController.colorList,
                            catSeed
                        )
                    )
                },
                isColorStrawEnabled = remember { ColorPickerUtilities.isEyeDropperSupported(context) },
                onDismiss = { colorPaletteVisible = false }
            )

            CatSeedInputDialog(
                visible = inputSeedDialogVisible,
                onConfirm = { seed ->
                    updateCatSeed(seed)
                },
                onDismiss = { inputSeedDialogVisible = false }
            )

            CatSvgCodeDialog(
                visible = catSvgDialogVisible,
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
                onDismiss = { catSvgDialogVisible = false },
            )

            CatEditorFloatingToolbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                canGoBack = catEditorRecords.canGoBack(),
                canGoNext = catEditorRecords.canGoNext(),
                onGoBack = {
                    CatEditorRecords.restoreRecord(
                        catEditorRecords.goBack(),
                        catEditorController,
                        catSeedState
                    )
                },
                onGoNext = {
                    CatEditorRecords.restoreRecord(
                        catEditorRecords.goNext(),
                        catEditorController,
                        catSeedState
                    )
                },
                hasSelectedPart = catEditorController.hasSelectedPart,
                onPaletteClick = { colorPaletteVisible = true },
                isMirrorMode = catEditorController.isMirrorMode,
                onMirrorClick = {
                    catEditorController.resetGraphicsLayer()
                    catEditorController.isMirrorMode = !catEditorController.isMirrorMode
                },
                isFavorite = isRememberCat,
                isFavoriteProcessing = isRememberCatProcessing,
                onFavoriteClick = {
                    isRememberCatProcessing = true
                    scope.launch {
                        if (isRememberCat) {
                            viewModel.forget(catSeed, catEditorController.colorList)
                        } else {
                            viewModel.remember(catSeed, catEditorController.colorList)
                        }
                        isRememberCat = !isRememberCat
                        isRememberCatProcessing = false
                    }
                },
                onRefreshClick = {
                    updateCatSeed(Utilities.randomSeed())
                },
                onSaveClick = saveCatAction.save,
                onShareClick = saveCatAction.share,
                onCopySeedClick = {
                    context.copy(catSeed.toString())
                },
                onExportSvgClick = {
                    catSvgText = CatParts.toSvg(
                        catEditorController.colorList, catEditorController.isMirrorMode
                    )
                    Log.i("CatEditor", "\n" + catSvgText)
                    catSvgDialogVisible = true
                },
                onPrintClick = printCatAction,
                isGridVisible = catEditorController.isGridVisible,
                onToggleGridClick = {
                    catEditorController.isGridVisible = !catEditorController.isGridVisible
                },
                onResetViewClick = {
                    catEditorController.resetGraphicsLayer()
                },
                onInputSeedClick = {
                    inputSeedDialogVisible = true
                }
            )
        }
    }

    CatRememberBottomSheet(
        visible = rememberCatsDialogVisible,
        cats = rememberedCats,
        onCatSelected = { cat ->
            updateCatSeed(cat.seed, cat.colors)
        },
        onCatDelete = { cat ->
            scope.launch {
                viewModel.forgetById(cat.id)
                rememberedCats = rememberedCats.filter { it.id != cat.id }
            }
        },
        onDismiss = { rememberCatsDialogVisible = false }
    )
}
