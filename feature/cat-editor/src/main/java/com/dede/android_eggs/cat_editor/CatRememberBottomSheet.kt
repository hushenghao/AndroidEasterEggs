@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.cat_editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.ui.composes.icons.rounded.Cat
import kotlinx.coroutines.launch

/**
 * A bottom sheet for selecting remembered cats.
 */
@Composable
fun CatRememberBottomSheet(
    visibleState: MutableState<Boolean>,
    onCatSelected: (cat: Cat) -> Unit
) {
    var visible by visibleState
    if (!visible) {
        return
    }
    val allCats = remember { mutableStateListOf<Cat>() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        launch {
            allCats.clear()
            allCats.addAll(CatRememberDataStore.getAllCats())
        }
    }
    var isDeletedMode by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { visible = false },
        sheetState = sheetState,
    ) {
        Crossfade(targetState = allCats.isEmpty()) { isEmpty ->
            if (isEmpty) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null,
                    )
                    Icon(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(60.dp),
                        imageVector = Icons.Rounded.Cat,
                        contentDescription = null,
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(76.dp),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 30.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.animateContentSize()
                ) {
                    items(allCats, key = { it.id }) { cat ->
                        CatItem(
                            cat = cat,
                            isDeletedMode = isDeletedMode,
                            modifier = Modifier.animateItem(),
                            onClick = {
                                onCatSelected(cat)
                                visible = false
                            },
                            onLongClick = {
                                isDeletedMode = !isDeletedMode
                            },
                            onCatDeleteClick = {
                                scope.launch {
                                    CatRememberDataStore.forgetById(cat.id)
                                    allCats.remove(cat)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CatItem(
    cat: Cat,
    isDeletedMode: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onCatDeleteClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .combinedClickable(
                onLongClick = onLongClick,
                onClick = onClick,
            )
            .then(modifier)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.aspectRatio(1f),
                painter = rememberCatPainter(cat),
                contentDescription = null,
            )
            Text(
                text = stringResource(R.string.label_cat_seed, cat.seed),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }

        AnimatedVisibility(
            visible = isDeletedMode,
            modifier = Modifier.align(Alignment.TopEnd),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Icon(
                modifier = Modifier
                    .clip(RoundedCornerShape(topEnd = 6.dp, bottomStart = 6.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    .clickable {
                        onCatDeleteClick()
                    },
                imageVector = Icons.Rounded.Clear,
                contentDescription = null
            )
        }
    }
}
