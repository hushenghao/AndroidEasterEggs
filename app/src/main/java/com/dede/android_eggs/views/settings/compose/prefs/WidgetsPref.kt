package com.dede.android_eggs.views.settings.compose.prefs

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.dede.android_eggs.ui.composes.LoopHorizontalPager
import com.dede.android_eggs.ui.composes.LoopPagerIndicator
import com.dede.android_eggs.ui.composes.rememberLoopPagerState
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.android_eggs.views.settings.compose.widgets.WidgetPreviewProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.dede.android_eggs.resources.R as StringsR

@Composable
fun WidgetsPref(viewModel: WidgetsPrefViewModel = hiltViewModel()) {
    val widgets = viewModel.widgets
    if (widgets.isEmpty()) {
        return
    }

    val pagerState = rememberLoopPagerState(pageCount = { widgets.size })
    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.Widgets,
        title = stringResource(StringsR.string.label_widgets),
    ) {
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LoopHorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                state = pagerState,
                autoLoop = false,
                pageSpacing = 10.dp,
            ) { page ->
                val widget = widgets[page]
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { widget.requestPin(context) }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier.widthIn(max = 320.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        widget.Preview()
                    }
                    Text(
                        text = stringResource(widget.descriptionRes),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            if (widgets.size > 1) {
                LoopPagerIndicator(
                    state = pagerState,
                    modifier = Modifier.height(12.dp),
                    indicatorModifier = Modifier
                        .clip(CircleShape)
                        .height(6.dp)
                        .widthIn(min = 6.dp, max = 10.dp)
                )
            }
        }
    }
}

@HiltViewModel
class WidgetsPrefViewModel @Inject constructor(
    widgetSet: Set<@JvmSuppressWildcards WidgetPreviewProvider>,
) : ViewModel() {
    val widgets: List<WidgetPreviewProvider> = widgetSet.sortedBy { it.order }
}
