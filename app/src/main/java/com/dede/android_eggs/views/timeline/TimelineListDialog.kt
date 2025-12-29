@file:OptIn(ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.timeline

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.dede.android_eggs.R
import com.dede.android_eggs.alterable_adaptive_icon.PathShape
import com.dede.android_eggs.navigation.EasterEggsDestination
import com.dede.android_eggs.views.main.compose.EasterEggLogo
import com.dede.android_eggs.views.main.util.AndroidLogoMatcher
import com.dede.android_eggs.views.settings.compose.prefs.IconShapePrefUtil
import com.dede.android_eggs.views.timeline.TimelineEventHelp.eventAnnotatedString
import com.dede.android_eggs.views.timeline.TimelineEventHelp.isNewGroup
import com.dede.android_eggs.views.timeline.TimelineEventHelp.localMonth
import com.dede.android_eggs.views.timeline.TimelineEventHelp.localYear
import com.dede.basic.isAdaptiveIconDrawable
import com.dede.basic.provider.TimelineEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

private const val TIMELINE_HORIZONTAL_BIAS = 0.3f

object TimelineListDialog : EasterEggsDestination {
    override val route: String = "timeline_list_dialog"
}

@Composable
fun TimelineListDialog(
    visibleState: MutableState<Boolean> = remember { mutableStateOf(true) },
    viewModel: TimelineViewModel = hiltViewModel(),
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    onDismiss: () -> Unit = {},
) {
    var visible by visibleState
    if (!visible) {
        return
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val paddingValues = WindowInsets.safeDrawing.asPaddingValues()

    val lazyListState = rememberLazyListState()
    // https://issuetracker.google.com/issues/353304855
    val sheetGesturesEnabled by remember {
        // disable sheet gestures when can scroll backward
        derivedStateOf { !lazyListState.canScrollBackward }
    }
    ModalBottomSheet(
        onDismissRequest = {
            visible = false
            onDismiss()
        },
        scrimColor = scrimColor,
        sheetState = sheetState,
        contentWindowInsets = {
            WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
        },
        sheetGesturesEnabled = sheetGesturesEnabled,
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding())
        ) {
            item {
                TimelineHeader()
            }
            items(viewModel.timelines) {
                TimelineItem(
                    event = it,
                    logoRes = AndroidLogoMatcher.findAndroidLogo(it.apiLevel),
                    isNewGroup = it.isNewGroup(viewModel.timelines)
                )
            }
            item {
                TimelineFooter()
            }
        }
    }
}

@Composable
private fun TimelineFooter() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        val (lineStart, line) = createRefs()
        Box(
            modifier = Modifier
                .size(2.dp)
                .background(colorScheme.secondary)
                .constrainAs(line) {
                    top.linkTo(parent.top)
                    centerHorizontallyTo(parent, TIMELINE_HORIZONTAL_BIAS)
                }
        )
        Box(
            modifier = Modifier
                .width(8.dp)
                .height(8.dp)
                .background(colorScheme.secondary, CircleShape)
                .constrainAs(lineStart) {
                    top.linkTo(parent.top)
                    centerHorizontallyTo(line)
                }
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun TimelineHeader() {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (arrow, line) = createRefs()
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(2.dp)
                .background(colorScheme.secondary, RoundedCornerShape(50, 50, 0, 0))
                .constrainAs(line) {
                    bottom.linkTo(parent.bottom)
                    centerHorizontallyTo(parent, TIMELINE_HORIZONTAL_BIAS)
                }
        )
        Icon(
            imageVector = Icons.Outlined.RocketLaunch,
            contentDescription = null,
            modifier = Modifier
                .size(36.dp)
                .padding(6.dp)
                .rotate(-45f)
                .constrainAs(arrow) {
                    bottom.linkTo(parent.bottom)
                    centerHorizontallyTo(line)
                },
            tint = colorScheme.secondary
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun TimelineItem(
    event: TimelineEvent = TimelineEvent(2025, Calendar.SEPTEMBER, 99, "Demo event name"),
    @DrawableRes logoRes: Int = R.mipmap.ic_launcher,
    isNewGroup: Boolean = true
) {
    val context = LocalContext.current
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (year, img, month, desc, line) = createRefs()

        Box(
            modifier = Modifier
                .width(2.dp)
                .background(colorScheme.secondary)
                .constrainAs(line) {
                    height = Dimension.fillToConstraints
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    centerHorizontallyTo(parent, TIMELINE_HORIZONTAL_BIAS)
                }
        )

        var imageModifier = Modifier
            .size(40.dp)
            .constrainAs(img) {
                top.linkTo(parent.top, 16.dp)
                centerHorizontallyTo(line)
            }
        val isAdaptiveIcon = context.isAdaptiveIconDrawable(logoRes)
        if (!isAdaptiveIcon) {
            val iconShape = IconShapePrefUtil.getMaskPath(context)
            imageModifier = Modifier
                .background(
                    colorScheme.secondaryContainer,
                    PathShape(iconShape)
                )
                .then(imageModifier)
                .padding(6.dp)
        }
        EasterEggLogo(
            res = logoRes,
            contentDescription = null,
            modifier = imageModifier
        )
        if (isNewGroup) {
            Text(
                text = event.localYear,
                style = typography.titleLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.constrainAs(year) {
                    end.linkTo(img.start, 12.dp)
                    centerVerticallyTo(img)
                }
            )
        }
        Text(
            text = event.localMonth,
            style = typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.constrainAs(month) {
                start.linkTo(img.end, 12.dp)
                top.linkTo(img.top)
            }
        )
        Text(
            text = event.eventAnnotatedString,
            style = typography.bodySmall,
            modifier = Modifier
                .padding(bottom = 12.dp)
                .constrainAs(desc) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        start = month.start,
                        end = parent.end,
                        endMargin = 16.dp,
                        bias = 0f,
                    )
                    top.linkTo(month.bottom)
                }
        )
    }
}

@HiltViewModel
class TimelineViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var timelines: List<TimelineEvent>
}
