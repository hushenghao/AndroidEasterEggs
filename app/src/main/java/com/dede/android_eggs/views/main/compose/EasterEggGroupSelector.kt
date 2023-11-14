package com.dede.android_eggs.views.main.compose

import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import com.dede.android_eggs.main.EggActionHelp
import com.dede.basic.provider.BaseEasterEgg
import com.dede.basic.provider.EasterEggGroup
import kotlin.math.roundToInt


@Composable
fun Modifier.withEasterEggGroupSelector(
    base: BaseEasterEgg,
    onSelected: (index: Int) -> Unit,
): Modifier {
    if (base !is EasterEggGroup) {
        return this
    }

    val activity = LocalHost.current
    var popupAnchorBounds by remember { mutableStateOf(Rect.Zero) }
    return clickable {
            // DropdownMenu style error, use native popup
            if (activity == null) return@clickable
            val parent = activity.findViewById<FrameLayout>(android.R.id.content)
            val fakeView = View(activity).apply {
                x = popupAnchorBounds.left
                y = popupAnchorBounds.top
            }
            val params = FrameLayout.LayoutParams(
                popupAnchorBounds.width.roundToInt(),
                popupAnchorBounds.height.roundToInt()
            )
            parent.addView(fakeView, parent.childCount, params)
            EggActionHelp.showEggGroupMenu(
                activity,
                fakeView,
                base,
                onSelected = onSelected,
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
}
