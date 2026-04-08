package com.dede.android_eggs.neko_controls_widget

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
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

@Module
@InstallIn(SingletonComponent::class)
object NekoControlsWidgetPreviewProvider : WidgetPreviewProvider {
    override val order: Int = 1
    override val descriptionRes: Int = R.string.neko_widget_description

    @Provides
    @IntoSet
    @Singleton
    fun provider(): WidgetPreviewProvider = this

    @Composable
    override fun Preview(modifier: Modifier) {
        val parent = LocalView.current as? ViewGroup
        AndroidView(
            modifier = modifier.then(Modifier.size(width = 320.dp, height = 180.dp)),
            factory = { context ->
                LayoutInflater.from(context).inflate(
                    R.layout.neko_controls_widget_compact,
                    parent,
                    false
                ).apply {
                    disableCardClick()
                    bindPreviewCard(
                        statusView = findViewById(R.id.water_status),
                        iconView = findViewById(R.id.water_icon),
                        cardView = findViewById(R.id.water_card),
                        progressView = findViewById(R.id.water_progress),
                        status = context.getString(R.string.neko_widget_water_status, 124),
                        iconRes = com.android_r.egg.R.drawable.r_ic_water_filled,
                        backgroundRes = R.drawable.neko_card_water_low,
                        progress = 62,
                    )
                    bindPreviewCard(
                        statusView = findViewById(R.id.food_status),
                        iconView = findViewById(R.id.food_icon),
                        cardView = findViewById(R.id.food_card),
                        progressView = null,
                        status = context.getString(R.string.neko_widget_food_full),
                        iconRes = com.android_r.egg.R.drawable.r_ic_foodbowl_filled,
                        backgroundRes = R.drawable.neko_card_food_high,
                        progress = null,
                    )
                    bindPreviewCard(
                        statusView = findViewById(R.id.toy_status),
                        iconView = findViewById(R.id.toy_icon),
                        cardView = findViewById(R.id.toy_card),
                        progressView = null,
                        status = context.getString(R.string.neko_widget_toy_curious),
                        iconRes = com.android_r.egg.R.drawable.r_ic_toy_ball,
                        backgroundRes = R.drawable.neko_card_toy_mid,
                        progress = null,
                    )
                    bindPreviewCard(
                        statusView = findViewById(R.id.status_value),
                        iconView = findViewById(R.id.status_icon),
                        cardView = findViewById(R.id.status_card),
                        progressView = null,
                        status = context.getString(R.string.neko_widget_status_thriving),
                        iconRes = R.drawable.neko_card_cat,
                        backgroundRes = R.drawable.neko_card_info_high,
                        progress = null,
                    )
                }
            },
        )
    }

    override fun requestPin(context: Context) = AppWidgetPinUtils.requestPinWidget(context, NekoControlsAppWidget::class.java)
}

private fun bindPreviewCard(
    statusView: TextView,
    iconView: ImageView,
    cardView: android.view.View,
    progressView: ProgressBar?,
    status: String,
    iconRes: Int,
    backgroundRes: Int,
    progress: Int?,
) {
    statusView.text = status
    iconView.setImageResource(iconRes)
    cardView.setBackgroundResource(backgroundRes)
    if (progressView != null && progress != null) {
        progressView.progress = progress
    }
}

private fun android.view.View.disableCardClick() {
    findViewById<android.view.View>(R.id.water_card).isClickable = false
    findViewById<android.view.View>(R.id.food_card).isClickable = false
    findViewById<android.view.View>(R.id.toy_card).isClickable = false
    findViewById<android.view.View>(R.id.status_card).isClickable = false
}
