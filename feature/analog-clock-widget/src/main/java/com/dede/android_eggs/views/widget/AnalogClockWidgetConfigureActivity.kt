@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.EdgeToEdgeCompat
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonGroupDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.dede.android_eggs.views.theme.EasterEggsTheme
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
                var initialAction by remember { mutableStateOf(AnalogClockWidgetClickAction.OPEN_EGG) }
                LaunchedEffect(Unit) {
                    initialAction = AnalogClockWidgetPrefs.getClickAction(
                        this@AnalogClockWidgetConfigureActivity,
                        appWidgetId
                    )
                }
                AnalogClockWidgetConfigureSheet(
                    initialAction = initialAction,
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

    private fun onConfirm(clickAction: AnalogClockWidgetClickAction) {
        lifecycleScope.launch {
            AnalogClockWidgetPrefs.setClickAction(
                this@AnalogClockWidgetConfigureActivity,
                appWidgetId,
                clickAction
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
    initialAction: AnalogClockWidgetClickAction,
    onDismissRequest: () -> Unit,
    onConfirm: (AnalogClockWidgetClickAction) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

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
                .padding(horizontal = 24.dp)
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.analog_clock_widget_config_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            var selectedAction by remember { mutableStateOf(initialAction) }
            LaunchedEffect(initialAction) {
                selectedAction = initialAction
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.analog_clock_widget_click_action_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.analog_clock_widget_config_summary),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val buttons = remember {
                    listOf(
                        Pair(
                            AnalogClockWidgetClickAction.OPEN_EGG,
                            R.string.analog_clock_widget_action_open_egg,
                        ),
                        Pair(
                            AnalogClockWidgetClickAction.OPEN_APP,
                            R.string.analog_clock_widget_action_open_app,
                        ),
                        Pair(
                            AnalogClockWidgetClickAction.NONE,
                            R.string.analog_clock_widget_action_none,
                        )
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    buttons.forEachIndexed { index, (action, labelRes) ->
                        TonalToggleButton(
                            checked = action == selectedAction,
                            onCheckedChange = { if (it) selectedAction = action },
                            modifier = Modifier.weight(1f),
                            shapes = when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                buttons.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            },
                        ) {
                            Text(text = stringResource(labelRes))
                        }
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
                TextButton(onClick = { closeAfterAnimation { onConfirm(selectedAction) } }) {
                    Text(text = stringResource(android.R.string.ok))
                }
            }
        }
    }
}
