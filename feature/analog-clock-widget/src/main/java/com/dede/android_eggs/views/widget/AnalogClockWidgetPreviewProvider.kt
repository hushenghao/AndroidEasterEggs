package com.dede.android_eggs.views.widget

import android.content.Context
import android.os.Build
import android.util.Xml
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.XmlRes
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.withStyledAttributes
import com.dede.android_eggs.views.settings.compose.widgets.AppWidgetPinUtils
import com.dede.android_eggs.views.settings.compose.widgets.WidgetPreviewProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import org.xmlpull.v1.XmlPullParser
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
                    context.readWidgetPreviewLayout(
                        R.xml.analog_clock_widget_info,
                        R.layout.widget_easter_egg_analog_clock_cinnamon_bun
                    ),
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

@LayoutRes
private fun Context.readWidgetPreviewLayout(@XmlRes xml: Int, @LayoutRes default: Int): Int {
    resources.getXml(xml).use { parser ->
        var type = parser.next()
        while (type != XmlPullParser.START_TAG && type != XmlPullParser.END_DOCUMENT) {
            type = parser.next()
        }
        if (type != XmlPullParser.START_TAG) {
            throw IllegalStateException("No start tag found in widget info xml")
        }

        val attrArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            intArrayOf(android.R.attr.previewLayout, android.R.attr.initialLayout)
        } else {
            intArrayOf(android.R.attr.initialLayout)
        }
        var previewLayout = 0
        withStyledAttributes(Xml.asAttributeSet(parser), attrArray) {
            for (i in 0..<attrArray.size) {
                previewLayout = getResourceId(i, 0)
                if (previewLayout != 0) {
                    break
                }
            }
        }
        if (previewLayout == 0) {
            previewLayout = default
        }
        return previewLayout
    }
}
