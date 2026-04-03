package com.dede.android_eggs.neko_controls_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.PendingIntentCompat
import kotlin.random.Random
import com.android_r.egg.R as NekoR

private const val ACTION_ITEM_CLICK =
    "com.dede.android_eggs.neko_controls_widget.ACTION_ITEM_CLICK"
private const val EXTRA_WIDGET_ID = "extra_widget_id"
private const val EXTRA_ITEM = "extra_item"

class NekoControlsAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        appWidgetIds.forEach { appWidgetId ->
            NekoControlsWidgetState.ensureState(context, appWidgetId)
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: android.os.Bundle,
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != ACTION_ITEM_CLICK) return

        val appWidgetId = intent.getIntExtra(EXTRA_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        val item = WidgetItem.fromName(intent.getStringExtra(EXTRA_ITEM)) ?: return
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return

        NekoControlsWidgetState.randomize(context, appWidgetId, item)
        updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        appWidgetIds.forEach { NekoControlsWidgetState.removeState(context, it) }
    }
}

private fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
) {
    val state = NekoControlsWidgetState.readState(context, appWidgetId)
    val views = RemoteViews(context.packageName, R.layout.neko_controls_widget)
    val compactContent = shouldUseCompactContent(appWidgetManager.getAppWidgetOptions(appWidgetId))

    bindCard(
        context = context,
        views = views,
        appWidgetId = appWidgetId,
        item = WidgetItem.Water,
        titleViewId = R.id.water_title,
        statusViewId = R.id.water_status,
        iconViewId = R.id.water_icon,
        cardViewId = R.id.water_card,
        progress = state.water,
    )
    bindCardLayoutMode(
        views = views,
        regularContentId = R.id.water_regular_content,
        compactContentId = R.id.water_compact_content,
        compactIconId = R.id.water_compact_icon,
        compactStatusId = R.id.water_compact_status,
        iconRes = WidgetItem.Water.iconRes(state.water),
        statusText = WidgetItem.Water.statusText(context, state.water),
        compact = compactContent,
    )
    bindCard(
        context = context,
        views = views,
        appWidgetId = appWidgetId,
        item = WidgetItem.Food,
        titleViewId = R.id.food_title,
        statusViewId = R.id.food_status,
        iconViewId = R.id.food_icon,
        cardViewId = R.id.food_card,
        progress = state.food,
    )
    bindCardLayoutMode(
        views = views,
        regularContentId = R.id.food_regular_content,
        compactContentId = R.id.food_compact_content,
        compactIconId = R.id.food_compact_icon,
        compactStatusId = R.id.food_compact_status,
        iconRes = WidgetItem.Food.iconRes(state.food),
        statusText = WidgetItem.Food.statusText(context, state.food),
        compact = compactContent,
    )
    bindCard(
        context = context,
        views = views,
        appWidgetId = appWidgetId,
        item = WidgetItem.Toy,
        titleViewId = R.id.toy_title,
        statusViewId = R.id.toy_status,
        iconViewId = R.id.toy_icon,
        cardViewId = R.id.toy_card,
        progress = state.toy,
    )
    bindCardLayoutMode(
        views = views,
        regularContentId = R.id.toy_regular_content,
        compactContentId = R.id.toy_compact_content,
        compactIconId = R.id.toy_compact_icon,
        compactStatusId = R.id.toy_compact_status,
        iconRes = WidgetItem.Toy.iconRes(state.toy),
        statusText = WidgetItem.Toy.statusText(context, state.toy),
        compact = compactContent,
    )
    bindMoodLayoutMode(
        views = views,
        compact = compactContent,
    )
    bindWaterProgress(views, state.water)
    bindStatusCard(context, views, state, compactContent)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun bindCard(
    context: Context,
    views: RemoteViews,
    appWidgetId: Int,
    item: WidgetItem,
    titleViewId: Int,
    statusViewId: Int,
    iconViewId: Int,
    cardViewId: Int,
    progress: Int,
) {
    views.setTextViewText(titleViewId, context.getString(item.titleRes))
    views.setTextViewText(statusViewId, item.statusText(context, progress))
    views.setImageViewResource(iconViewId, item.iconRes(progress))
    if (item != WidgetItem.Water) {
        views.setInt(cardViewId, "setBackgroundResource", item.backgroundRes(progress))
    }
    views.setOnClickPendingIntent(cardViewId, createClickIntent(context, appWidgetId, item))
}

private fun bindWaterProgress(views: RemoteViews, progress: Int) {
    views.setProgressBar(R.id.water_progress, 100, progress, false)
}

private fun bindCardLayoutMode(
    views: RemoteViews,
    regularContentId: Int,
    compactContentId: Int,
    compactIconId: Int,
    compactStatusId: Int,
    iconRes: Int,
    statusText: String,
    compact: Boolean,
) {
    views.setViewVisibility(regularContentId, if (compact) View.GONE else View.VISIBLE)
    views.setViewVisibility(compactContentId, if (compact) View.VISIBLE else View.GONE)
    views.setImageViewResource(compactIconId, iconRes)
    views.setTextViewText(compactStatusId, statusText)
}

private fun bindStatusCard(
    context: Context,
    views: RemoteViews,
    state: WidgetState,
    compact: Boolean,
) {
    val average = (state.water + state.food + state.toy) / 3
    val statusInfo = when {
        average >= 80 -> MoodInfo(
            textRes = R.string.neko_widget_status_thriving,
            backgroundRes = R.drawable.neko_card_info_high,
            titleColor = 0xFFE6F7FF.toInt(),
            valueColor = 0xFFD6F0FF.toInt(),
        )
        average >= 45 -> MoodInfo(
            textRes = R.string.neko_widget_status_playful,
            backgroundRes = R.drawable.neko_card_info_mid,
            titleColor = 0xFFF3FFF8.toInt(),
            valueColor = 0xFFDDEFE6.toInt(),
        )
        else -> MoodInfo(
            textRes = R.string.neko_widget_status_needs_attention,
            backgroundRes = R.drawable.neko_card_info_low,
            titleColor = 0xFFFFF1DD.toInt(),
            valueColor = 0xFFFFDFC2.toInt(),
        )
    }
    val statusText = context.getString(statusInfo.textRes)
    views.setTextViewText(R.id.status_value, statusText)
    views.setTextViewText(R.id.status_compact_value, statusText)
    views.setImageViewResource(R.id.status_icon, R.drawable.neko_card_cat)
    views.setImageViewResource(R.id.status_compact_icon, R.drawable.neko_card_cat)
    views.setInt(R.id.status_card, "setBackgroundResource", statusInfo.backgroundRes)
    views.setTextColor(R.id.status_title, statusInfo.titleColor)
    views.setTextColor(R.id.status_value, statusInfo.valueColor)
    if (compact) {
        views.setTextColor(R.id.status_compact_value, statusInfo.valueColor)
    }
}

private fun bindMoodLayoutMode(
    views: RemoteViews,
    compact: Boolean,
) {
    views.setViewVisibility(R.id.status_regular_content, if (compact) View.GONE else View.VISIBLE)
    views.setViewVisibility(R.id.status_compact_content, if (compact) View.VISIBLE else View.GONE)
}

private fun createClickIntent(
    context: Context,
    appWidgetId: Int,
    item: WidgetItem,
): PendingIntent {
    val intent = Intent(context, NekoControlsAppWidget::class.java)
        .setAction(ACTION_ITEM_CLICK)
        .putExtra(EXTRA_WIDGET_ID, appWidgetId)
        .putExtra(EXTRA_ITEM, item.name)

    return requireNotNull(
        PendingIntentCompat.getBroadcast(
            context,
            appWidgetId * 10 + item.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT,
            false
        )
    )
}

private fun shouldUseCompactContent(options: Bundle): Boolean {
    val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0)
    val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)
    return minHeight in 1..120 || minWidth in 1..235
}

private enum class WidgetItem(val titleRes: Int) {
    Water(R.string.neko_widget_water_title) {
        override fun iconRes(progress: Int): Int {
            return if (progress >= 50) NekoR.drawable.r_ic_water_filled else NekoR.drawable.r_ic_water
        }

        override fun backgroundRes(progress: Int): Int {
            return R.drawable.neko_card_water_low
        }

        override fun statusText(context: Context, progress: Int): String {
            return context.getString(R.string.neko_widget_water_status, progress * 2)
        }
    },
    Food(R.string.neko_widget_food_title) {
        override fun iconRes(progress: Int): Int {
            return if (progress >= 50) NekoR.drawable.r_ic_foodbowl_filled else NekoR.drawable.r_ic_bowl
        }

        override fun backgroundRes(progress: Int): Int {
            return when {
                progress >= 70 -> R.drawable.neko_card_food_high
                progress >= 30 -> R.drawable.neko_card_food_mid
                else -> R.drawable.neko_card_food_low
            }
        }

        override fun statusText(context: Context, progress: Int): String {
            val label = when {
                progress >= 70 -> R.string.neko_widget_food_full
                progress >= 30 -> R.string.neko_widget_food_mid
                else -> R.string.neko_widget_food_empty
            }
            return context.getString(label)
        }
    },
    Toy(R.string.neko_widget_toy_title) {
        override fun iconRes(progress: Int): Int {
            return when {
                progress < 25 -> NekoR.drawable.r_ic_toy_mouse
                progress < 50 -> NekoR.drawable.r_ic_toy_fish
                progress < 75 -> NekoR.drawable.r_ic_toy_ball
                else -> NekoR.drawable.r_ic_toy_laser
            }
        }

        override fun backgroundRes(progress: Int): Int {
            return when {
                progress >= 70 -> R.drawable.neko_card_toy_high
                progress >= 30 -> R.drawable.neko_card_toy_mid
                else -> R.drawable.neko_card_toy_low
            }
        }

        override fun statusText(context: Context, progress: Int): String {
            val label = when {
                progress >= 80 -> R.string.neko_widget_toy_chaotic
                progress >= 40 -> R.string.neko_widget_toy_curious
                else -> R.string.neko_widget_toy_idle
            }
            return context.getString(label)
        }
    };

    abstract fun iconRes(progress: Int): Int
    abstract fun backgroundRes(progress: Int): Int
    abstract fun statusText(context: Context, progress: Int): String

    companion object {
        fun fromName(name: String?): WidgetItem? = entries.firstOrNull { it.name == name }
    }
}

private data class WidgetState(
    val water: Int,
    val food: Int,
    val toy: Int,
)

private data class MoodInfo(
    val textRes: Int,
    val backgroundRes: Int,
    val titleColor: Int,
    val valueColor: Int,
)

private object NekoControlsWidgetState {
    private const val PREFS_NAME = "neko_controls_widget"
    private const val MISSING_VALUE = -1

    fun ensureState(context: Context, appWidgetId: Int): WidgetState {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = WidgetState(
            water = prefs.getInt(key(appWidgetId, WidgetItem.Water), MISSING_VALUE),
            food = prefs.getInt(key(appWidgetId, WidgetItem.Food), MISSING_VALUE),
            toy = prefs.getInt(key(appWidgetId, WidgetItem.Toy), MISSING_VALUE),
        )
        if (current.isInitialized()) return current

        val seeded = WidgetState(
            water = randomProgress(),
            food = randomProgress(),
            toy = randomProgress(),
        )
        writeState(context, appWidgetId, seeded)
        return seeded
    }

    fun readState(context: Context, appWidgetId: Int): WidgetState {
        return ensureState(context, appWidgetId)
    }

    fun randomize(context: Context, appWidgetId: Int, item: WidgetItem): WidgetState {
        val current = ensureState(context, appWidgetId)
        val next = when (item) {
            WidgetItem.Water -> current.copy(water = randomProgress())
            WidgetItem.Food -> current.copy(food = randomProgress())
            WidgetItem.Toy -> current.copy(toy = randomProgress())
        }
        writeState(context, appWidgetId, next)
        return next
    }

    fun removeState(context: Context, appWidgetId: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(key(appWidgetId, WidgetItem.Water))
            .remove(key(appWidgetId, WidgetItem.Food))
            .remove(key(appWidgetId, WidgetItem.Toy))
            .apply()
    }

    private fun writeState(context: Context, appWidgetId: Int, state: WidgetState) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(key(appWidgetId, WidgetItem.Water), state.water)
            .putInt(key(appWidgetId, WidgetItem.Food), state.food)
            .putInt(key(appWidgetId, WidgetItem.Toy), state.toy)
            .apply()
    }

    private fun key(appWidgetId: Int, item: WidgetItem): String {
        return "widget_${appWidgetId}_${item.name.lowercase()}"
    }

    private fun WidgetState.isInitialized(): Boolean {
        return water != MISSING_VALUE && food != MISSING_VALUE && toy != MISSING_VALUE
    }

    private fun randomProgress(): Int = Random.nextInt(from = 0, until = 101)
}
