package com.dede.android_eggs.views.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dede.android_eggs.views.settings.compose.widgets.AppWidgetPinUtils
import com.dede.android_eggs.views.settings.compose.widgets.WidgetPreviewProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton
import com.dede.android_eggs.resources.R as StringsR

@Module
@InstallIn(SingletonComponent::class)
object AnalogClockWidgetPreviewProvider : WidgetPreviewProvider {
    override val order: Int = 10
    override val descriptionRes: Int = StringsR.string.app_widget_description

    @Provides
    @IntoSet
    @Singleton
    fun provider(): WidgetPreviewProvider = this

    @Composable
    override fun Preview(modifier: Modifier) {
        val parent = LocalView.current as? ViewGroup
        AndroidView(
            modifier = modifier.then(Modifier.size(180.dp)),
            factory = { context ->
                LayoutInflater.from(context).inflate(
                    R.layout.widget_easter_egg_analog_clock,
                    parent,
                    false
                )
            },
        )
    }

    override fun requestPin(context: Context) {
        AppWidgetPinUtils.requestPinWidget(context, AnalogClockAppWidget::class.java)
    }
}
