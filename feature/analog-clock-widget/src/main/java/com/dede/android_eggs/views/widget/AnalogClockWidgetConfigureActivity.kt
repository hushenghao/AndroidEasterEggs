@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.EdgeToEdgeCompat
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TonalToggleButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.dede.android_eggs.views.theme.EasterEggsTheme
import com.dede.basic.requireDrawable
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.launch

/**
 * The configuration activity for [AnalogClockAppWidget].
 */
class AnalogClockWidgetConfigureActivity : ComponentActivity() {

    private val appWidgetId: Int by lazy {
        intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        EdgeToEdgeCompat.enable(this)
        super.onCreate(savedInstanceState)
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            onCancel()
            return
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsControllerCompat = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsControllerCompat.apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        setContent {
            EasterEggsTheme {
                AnalogClockWidgetConfigureSheet(
                    appWidgetId = appWidgetId,
                    onDismissRequest = ::onCancel,
                    onConfirm = ::onConfirm
                )
            }
        }
    }

    private fun onCancel() {
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun onConfirm(
        clickAction: AnalogClockWidgetClickAction,
        dialStyle: AnalogClockWidgetDialStyle,
    ) {
        lifecycleScope.launch {
            AnalogClockWidgetPrefs.setClickAction(
                this@AnalogClockWidgetConfigureActivity,
                appWidgetId,
                clickAction
            )
            AnalogClockWidgetPrefs.setDialStyle(
                this@AnalogClockWidgetConfigureActivity,
                appWidgetId,
                dialStyle
            )
            updateAppWidgetAsync(
                this@AnalogClockWidgetConfigureActivity,
                AppWidgetManager.getInstance(this@AnalogClockWidgetConfigureActivity),
                appWidgetId
            )

            val result = Intent()
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(RESULT_OK, result)
            finish()
        }
    }
}

@Composable
private fun AnalogClockWidgetConfigureSheet(
    appWidgetId: Int,
    onDismissRequest: () -> Unit,
    onConfirm: (AnalogClockWidgetClickAction, AnalogClockWidgetDialStyle) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedAction by remember { mutableStateOf(AnalogClockWidgetClickAction.OPEN_EGG) }
    var selectedDialStyle by remember {
        mutableStateOf(AnalogClockWidgetDialStyle.ANDROID_ICONS)
    }
    LaunchedEffect(context, appWidgetId) {
        selectedAction = AnalogClockWidgetPrefs.getClickAction(context, appWidgetId)
        selectedDialStyle = AnalogClockWidgetPrefs.getDialStyle(context, appWidgetId)
    }

    fun closeAfterAnimation(action: () -> Unit) {
        scope.launch {
            sheetState.hide()
            action()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            closeAfterAnimation(onDismissRequest)
        },
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.analog_clock_widget_config_title),
                style = MaterialTheme.typography.headlineMedium,
            )

            ConfigurationGroup(
                title = stringResource(R.string.analog_clock_widget_click_action_title),
                subtitle = stringResource(R.string.analog_clock_widget_config_summary),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AnalogClockWidgetClickAction.entries.forEachIndexed { index, action ->
                        TonalToggleButton(
                            checked = action == selectedAction,
                            onCheckedChange = { if (it) selectedAction = action },
                            modifier = Modifier.weight(1f),
                            shapes = when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                AnalogClockWidgetClickAction.entries.lastIndex ->
                                    ButtonGroupDefaults.connectedTrailingButtonShapes()

                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            },
                        ) {
                            Text(text = stringResource(action.labelRes))
                        }
                    }
                }
            }

            ConfigurationGroup(
                title = stringResource(R.string.analog_clock_widget_dial_style_title),
                subtitle = stringResource(R.string.analog_clock_widget_dial_style_summary),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AnalogClockWidgetDialStyle.entries.forEach { dialStyle ->
                        DialStyleCard(
                            dialStyle = dialStyle,
                            label = stringResource(dialStyle.nameRes),
                            selected = dialStyle == selectedDialStyle,
                            onClick = { selectedDialStyle = dialStyle },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { closeAfterAnimation(onDismissRequest) }) {
                    Text(text = stringResource(android.R.string.cancel))
                }
                TextButton(
                    onClick = {
                        closeAfterAnimation {
                            onConfirm(selectedAction, selectedDialStyle)
                        }
                    }
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            }
        }
    }
}

@Composable
private fun ConfigurationGroup(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        content()
    }
}

@Composable
private fun DialStyleCard(
    dialStyle: AnalogClockWidgetDialStyle,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val previewDrawable = remember(context, resources, dialStyle) {
        val dialDrawable = context.requireDrawable(dialStyle.dialRes)
        if (dialStyle == AnalogClockWidgetDialStyle.ANDROID_ICONS) {
            dialDrawable.toBitmap().toDrawable(resources)
        } else {
            dialDrawable
        }
    }
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = rememberDrawablePainter(previewDrawable),
                contentDescription = label,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}
