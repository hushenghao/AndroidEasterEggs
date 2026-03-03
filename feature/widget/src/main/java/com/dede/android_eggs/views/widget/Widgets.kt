package com.dede.android_eggs.views.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.android_eggs.resources.R as StringR

@Composable
fun Widgets() {
    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.Widgets,
        title = "Widget",
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val parent = LocalView.current as? ViewGroup
            AndroidView(
                modifier = Modifier.size(110.dp),
                factory = { context ->
                    val inflater = LayoutInflater.from(context)
                    val view = inflater.inflate(
                        R.layout.widget_easter_egg_analog_clock,
                        parent, false
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        view.setOnClickListener {
                            requestPinWidget(it.context)
                        }
                    }
                    view
                })
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(StringR.string.app_widget_description),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun requestPinWidget(context: Context) {
    val appWidgetManager = AppWidgetManager.getInstance(context)
    if (appWidgetManager.isRequestPinAppWidgetSupported) {
        val myProvider = ComponentName(context, AnalogClockAppWidget::class.java)
        appWidgetManager.requestPinAppWidget(myProvider, null, null)
    }
}
