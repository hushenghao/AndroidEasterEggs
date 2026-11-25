@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
)

package com.dede.android_eggs.cat_editor.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AppBarRow
import androidx.compose.material3.AppBarScope
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.appcompat.R as AppCompatR
import com.dede.android_eggs.resources.R as StringR

// Space occupied by the adjacent FAB (56dp), the gap between toolbar and FAB (8dp),
// and HorizontalFloatingToolbar's default horizontal contentPadding (8dp * 2).
// HorizontalFloatingToolbar wraps its toolbar Row in horizontalScroll, which passes
// infinite maxWidth to children, so we subtract this budget from BoxWithConstraints'
// maxWidth to give AppBarRow a finite width for adaptive overflow.
private val FloatingToolbarFabReservedWidth = 80.dp

@Composable
internal fun CatEditorFloatingToolbar(
    modifier: Modifier = Modifier,
    canGoBack: Boolean,
    canGoNext: Boolean,
    onGoBack: () -> Unit,
    onGoNext: () -> Unit,
    hasSelectedPart: Boolean,
    onPaletteClick: () -> Unit,
    isMirrorMode: Boolean,
    onMirrorClick: () -> Unit,
    isFavorite: Boolean,
    isFavoriteProcessing: Boolean,
    onFavoriteClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onCopySeedClick: () -> Unit,
    onExportSvgClick: () -> Unit,
    onPrintClick: () -> Unit,
    isGridVisible: Boolean,
    onToggleGridClick: () -> Unit,
    onResetViewClick: () -> Unit,
    onInputSeedClick: () -> Unit,
) {
    val undoLabel = stringResource(StringR.string.cat_editor_action_undo)
    val redoLabel = stringResource(StringR.string.cat_editor_action_redo)
    val paletteLabel = stringResource(StringR.string.cat_editor_action_palette)
    val favoriteLabel = stringResource(
        if (isFavorite) {
            StringR.string.cat_editor_action_unfavorite
        } else {
            StringR.string.cat_editor_action_favorite
        }
    )
    val saveLabel = stringResource(StringR.string.cat_editor_action_save)
    val mirrorLabel = stringResource(StringR.string.cat_editor_action_mirror)
    val gridLabel = stringResource(StringR.string.cat_editor_action_grid)
    val shareLabel = stringResource(StringR.string.label_share)
    val resetViewLabel = stringResource(StringR.string.cat_editor_action_reset_view)
    val copySeedLabel = stringResource(StringR.string.cat_editor_action_copy_seed)
    val exportSvgLabel = stringResource(StringR.string.cat_editor_action_export_svg)
    val inputSeedLabel = stringResource(StringR.string.cat_editor_input_seed)
    val printLabel = stringResource(StringR.string.cat_editor_action_print)
    val randomLabel = stringResource(StringR.string.cat_editor_action_random)
    val overflowDescription = @Suppress("PrivateResource") stringResource(
        AppCompatR.string.abc_action_menu_overflow_description
    )

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val availableRowWidth = (maxWidth - FloatingToolbarFabReservedWidth).coerceAtLeast(0.dp)

        val tooltipShape = shapes.extraSmall
        // AppBarRow creates its internal DropdownMenu with MenuDefaults.shape
        // (MaterialTheme.shapes.extraSmall) without exposing a shape parameter.
        // Override extraSmall locally to shapes.extraLarge to match EasterEggGroupSelector,
        // while preserving tooltipShape explicitly on PlainTooltip.
        val menuShapes = shapes.copy(extraSmall = shapes.extraLarge)

        HorizontalFloatingToolbar(
            expanded = true,
            floatingActionButton = {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Above
                    ),
                    tooltip = { PlainTooltip(shape = tooltipShape) { Text(randomLabel) } },
                    state = rememberTooltipState(),
                ) {
                    FloatingToolbarDefaults.StandardFloatingActionButton(
                        onClick = onRefreshClick
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = randomLabel,
                        )
                    }
                }
            },
        ) {
            MaterialTheme(shapes = menuShapes) {
                AppBarRow(
                    modifier = Modifier.widthIn(max = availableRowWidth),
                    overflowIndicator = { menuState ->
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                TooltipAnchorPosition.Above
                            ),
                            tooltip = {
                                PlainTooltip(shape = tooltipShape) {
                                    Text(overflowDescription)
                                }
                            },
                            state = rememberTooltipState(),
                        ) {
                            IconButton(onClick = { menuState.show() }) {
                                Icon(
                                    imageVector = Icons.Rounded.MoreVert,
                                    contentDescription = overflowDescription,
                                )
                            }
                        }
                    },
                ) {
                    actionItem(
                        onClick = onGoBack,
                        label = undoLabel,
                        enabled = canGoBack,
                        tooltipShape = tooltipShape,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = undoLabel,
                        )
                    }

                    actionItem(
                        onClick = onGoNext,
                        label = redoLabel,
                        enabled = canGoNext,
                        tooltipShape = tooltipShape,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = redoLabel,
                        )
                    }

                    actionItem(
                        onClick = onPaletteClick,
                        label = paletteLabel,
                        enabled = hasSelectedPart,
                        tooltipShape = tooltipShape,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Palette,
                            contentDescription = paletteLabel,
                        )
                    }

                    actionItem(
                        onClick = onFavoriteClick,
                        label = favoriteLabel,
                        enabled = !isFavoriteProcessing,
                        tooltipShape = tooltipShape,
                    ) {
                        Crossfade(
                            targetState = isFavorite,
                            label = "Favorite Icon Crossfade",
                        ) { favorite ->
                            Icon(
                                imageVector = if (favorite) {
                                    Icons.Rounded.Favorite
                                } else {
                                    Icons.Rounded.FavoriteBorder
                                },
                                contentDescription = favoriteLabel,
                            )
                        }
                    }

                    actionItem(
                        onClick = onSaveClick,
                        label = saveLabel,
                        tooltipShape = tooltipShape,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Save,
                            contentDescription = saveLabel,
                        )
                    }

                    actionItem(
                        onClick = onMirrorClick,
                        label = mirrorLabel,
                        tooltipShape = tooltipShape,
                    ) {
                        val rotationY by animateFloatAsState(
                            targetValue = if (isMirrorMode) 180f else 0f,
                            label = "Cat Mirror Rotation",
                        )
                        Icon(
                            modifier = Modifier.graphicsLayer { this.rotationY = rotationY },
                            imageVector = Icons.Rounded.Flip,
                            contentDescription = mirrorLabel,
                        )
                    }

                    actionItem(
                        onClick = onToggleGridClick,
                        label = gridLabel,
                        tooltipShape = tooltipShape,
                    ) {
                        Icon(
                            imageVector = if (isGridVisible) {
                                Icons.Rounded.GridOff
                            } else {
                                Icons.Rounded.GridOn
                            },
                            contentDescription = gridLabel,
                        )
                    }

                    actionItem(
                        onClick = onShareClick,
                        label = shareLabel,
                        tooltipShape = tooltipShape,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = shareLabel,
                        )
                    }

                    actionItem(
                        onClick = onResetViewClick,
                        label = resetViewLabel,
                        tooltipShape = tooltipShape,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CenterFocusWeak,
                            contentDescription = resetViewLabel,
                        )
                    }

                    actionItem(
                        onClick = onCopySeedClick,
                        label = copySeedLabel,
                        tooltipShape = tooltipShape,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = copySeedLabel,
                        )
                    }

                    actionItem(
                        onClick = onExportSvgClick,
                        label = exportSvgLabel,
                        tooltipShape = tooltipShape,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Code,
                            contentDescription = exportSvgLabel,
                        )
                    }

                    actionItem(
                        onClick = onInputSeedClick,
                        label = inputSeedLabel,
                        tooltipShape = tooltipShape,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Draw,
                            contentDescription = inputSeedLabel,
                        )
                    }

                    actionItem(
                        onClick = onPrintClick,
                        label = printLabel,
                        tooltipShape = tooltipShape,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Print,
                            contentDescription = printLabel,
                        )
                    }
                }
            }
        }
    }
}

private fun AppBarScope.actionItem(
    onClick: () -> Unit,
    label: String,
    tooltipShape: Shape,
    enabled: Boolean = true,
    icon: @Composable () -> Unit,
) {
    customItem(
        appbarContent = {
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    TooltipAnchorPosition.Above
                ),
                tooltip = { PlainTooltip(shape = tooltipShape) { Text(label) } },
                state = rememberTooltipState(),
            ) {
                IconButton(
                    onClick = onClick,
                    enabled = enabled,
                    content = icon,
                )
            }
        },
        menuContent = { menuState ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = label,
                        style = typography.bodyLarge,
                    )
                },
                leadingIcon = icon,
                enabled = enabled,
                onClick = {
                    menuState.dismiss()
                    onClick()
                },
            )
        },
    )
}
