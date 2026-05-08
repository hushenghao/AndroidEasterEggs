package com.dede.android_eggs.neko_controls_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.SizeF
import android.widget.RemoteViews
import androidx.core.app.PendingIntentCompat
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dede.basic.cachedExecutor
import com.dede.basic.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlin.random.Random

private const val ACTION_ITEM_CLICK =
    "com.dede.android_eggs.neko_controls_widget.ACTION_ITEM_CLICK"
private const val EXTRA_WIDGET_ID = "extra_widget_id"
private const val EXTRA_ITEM = "extra_item"

private const val COMPACT_WIDGET_HEIGHT_DP = 180f
private const val EXPANDED_WIDGET_HEIGHT_DP = 260f
private const val LARGE_WIDGET_HEIGHT_DP = 400f
private const val WIDGET_WIDTH_DP = 300f
private const val EXPANDED_LAYOUT_HEIGHT_THRESHOLD_DP = 200
private const val LARGE_LAYOUT_HEIGHT_THRESHOLD_DP = 340

class NekoControlsAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidgetAsync(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle,
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        updateAppWidgetAsync(context, appWidgetManager, appWidgetId)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != ACTION_ITEM_CLICK) return

        val appWidgetId = intent.getIntExtra(EXTRA_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        val item = WidgetItem.fromName(intent.getStringExtra(EXTRA_ITEM)) ?: return
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return

        val pendingResult = goAsync()
        cachedExecutor.launch(Dispatchers.IO) {
            try {
                NekoControlsWidgetState.randomize(context, appWidgetId, item)
                updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId)
            } finally {
                pendingResult.finish()
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        val pendingResult = goAsync()
        cachedExecutor.launch(Dispatchers.IO) {
            try {
                appWidgetIds.forEach { NekoControlsWidgetState.removeState(context, it) }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

private fun updateAppWidgetAsync(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
) {
    cachedExecutor.launch(Dispatchers.IO) {
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }
}

private suspend fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
) {
    val state = NekoControlsWidgetState.readState(context, appWidgetId)
    val compactViews = buildRemoteViews(
        context = context,
        appWidgetId = appWidgetId,
        state = state,
        mode = WidgetLayoutMode.Compact,
    )
    val expandedViews = buildRemoteViews(
        context = context,
        appWidgetId = appWidgetId,
        state = state,
        mode = WidgetLayoutMode.Expanded,
    )
    val largeViews = buildRemoteViews(
        context = context,
        appWidgetId = appWidgetId,
        state = state,
        mode = WidgetLayoutMode.Large,
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val sizedViews = RemoteViews(
            mapOf(
                SizeF(WIDGET_WIDTH_DP, COMPACT_WIDGET_HEIGHT_DP) to compactViews,
                SizeF(WIDGET_WIDTH_DP, EXPANDED_WIDGET_HEIGHT_DP) to expandedViews,
                SizeF(WIDGET_WIDTH_DP, LARGE_WIDGET_HEIGHT_DP) to largeViews,
            )
        )
        appWidgetManager.updateAppWidget(appWidgetId, sizedViews)
        return
    }

    val minHeight = appWidgetManager
        .getAppWidgetOptions(appWidgetId)
        .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)
    val fallbackViews = when {
        minHeight >= LARGE_LAYOUT_HEIGHT_THRESHOLD_DP -> largeViews
        minHeight >= EXPANDED_LAYOUT_HEIGHT_THRESHOLD_DP -> expandedViews
        else -> compactViews
    }
    appWidgetManager.updateAppWidget(appWidgetId, fallbackViews)
}

private fun buildRemoteViews(
    context: Context,
    appWidgetId: Int,
    state: WidgetState,
    mode: WidgetLayoutMode,
): RemoteViews {
    val views = RemoteViews(context.packageName, mode.layoutRes)

    bindCard(
        context = context,
        views = views,
        appWidgetId = appWidgetId,
        item = WidgetItem.Water,
        ids = mode.waterIds,
        progress = state.water,
    )
    bindCard(
        context = context,
        views = views,
        appWidgetId = appWidgetId,
        item = WidgetItem.Food,
        ids = mode.foodIds,
        progress = state.food,
    )
    bindCard(
        context = context,
        views = views,
        appWidgetId = appWidgetId,
        item = WidgetItem.Toy,
        ids = mode.toyIds,
        progress = state.toy,
    )
    bindWaterProgress(views, state.water)
    bindStatusCard(context, views, state, mode.statusIds)
    return views
}

private fun bindCard(
    context: Context,
    views: RemoteViews,
    appWidgetId: Int,
    item: WidgetItem,
    ids: CardViewIds,
    progress: Int,
) {
    ids.titleViewId?.let { views.setTextViewText(it, context.getString(item.titleRes)) }
    views.setTextViewText(ids.statusViewId, item.statusText(context, progress))
    views.setImageViewResource(ids.iconViewId, item.iconRes(progress))
    if (item != WidgetItem.Water) {
        views.setInt(ids.cardViewId, "setBackgroundResource", item.backgroundRes(progress))
    }
    views.setOnClickPendingIntent(ids.cardViewId, createClickIntent(context, appWidgetId, item))
}

private fun bindWaterProgress(views: RemoteViews, progress: Int) {
    views.setProgressBar(R.id.water_progress, 100, progress, false)
}

private fun bindStatusCard(
    context: Context,
    views: RemoteViews,
    state: WidgetState,
    ids: StatusViewIds,
) {
    val average = (state.water + state.food + state.toy) / 3
    val statusInfo = when {
        average >= 80 -> MoodInfo(
            textRes = R.string.neko_widget_status_thriving,
            backgroundRes = R.drawable.neko_card_info_high,
        )
        average >= 45 -> MoodInfo(
            textRes = R.string.neko_widget_status_playful,
            backgroundRes = R.drawable.neko_card_info_mid,
        )
        else -> MoodInfo(
            textRes = R.string.neko_widget_status_needs_attention,
            backgroundRes = R.drawable.neko_card_info_low,
        )
    }
    val statusText = context.getString(statusInfo.textRes)
    views.setTextViewText(ids.valueViewId, statusText)
    views.setImageViewResource(ids.iconViewId, R.drawable.neko_ic_cat)
    views.setInt(ids.cardViewId, "setBackgroundResource", statusInfo.backgroundRes)
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

private enum class WidgetItem(val titleRes: Int) {
    Water(R.string.neko_widget_water_title) {
        override fun iconRes(progress: Int): Int {
            return if (progress >= 50) R.drawable.neko_ic_water_filled else R.drawable.neko_ic_water
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
            return if (progress >= 50) R.drawable.neko_ic_food_bowl_filled else R.drawable.neko_ic_bowl
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
                progress < 25 -> R.drawable.neko_ic_toy_mouse
                progress < 50 -> R.drawable.neko_ic_toy_fish
                progress < 75 -> R.drawable.neko_ic_toy_ball
                else -> R.drawable.neko_ic_toy_laser
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

private enum class WidgetLayoutMode(
    val layoutRes: Int,
    val waterIds: CardViewIds,
    val foodIds: CardViewIds,
    val toyIds: CardViewIds,
    val statusIds: StatusViewIds,
) {
    Compact(
        layoutRes = R.layout.neko_controls_widget_compact,
        waterIds = CardViewIds(
            titleViewId = null,
            statusViewId = R.id.water_status,
            iconViewId = R.id.water_icon,
            cardViewId = R.id.water_card,
        ),
        foodIds = CardViewIds(
            titleViewId = null,
            statusViewId = R.id.food_status,
            iconViewId = R.id.food_icon,
            cardViewId = R.id.food_card,
        ),
        toyIds = CardViewIds(
            titleViewId = null,
            statusViewId = R.id.toy_status,
            iconViewId = R.id.toy_icon,
            cardViewId = R.id.toy_card,
        ),
        statusIds = StatusViewIds(
            titleViewId = null,
            valueViewId = R.id.status_value,
            iconViewId = R.id.status_icon,
            cardViewId = R.id.status_card,
        ),
    ),
    Expanded(
        layoutRes = R.layout.neko_controls_widget_expanded,
        waterIds = CardViewIds(
            titleViewId = R.id.water_title,
            statusViewId = R.id.water_status,
            iconViewId = R.id.water_icon,
            cardViewId = R.id.water_card,
        ),
        foodIds = CardViewIds(
            titleViewId = R.id.food_title,
            statusViewId = R.id.food_status,
            iconViewId = R.id.food_icon,
            cardViewId = R.id.food_card,
        ),
        toyIds = CardViewIds(
            titleViewId = R.id.toy_title,
            statusViewId = R.id.toy_status,
            iconViewId = R.id.toy_icon,
            cardViewId = R.id.toy_card,
        ),
        statusIds = StatusViewIds(
            titleViewId = R.id.status_title,
            valueViewId = R.id.status_value,
            iconViewId = R.id.status_icon,
            cardViewId = R.id.status_card,
        ),
    ),
    Large(
        layoutRes = R.layout.neko_controls_widget_large,
        waterIds = CardViewIds(
            titleViewId = R.id.water_title,
            statusViewId = R.id.water_status,
            iconViewId = R.id.water_icon,
            cardViewId = R.id.water_card,
        ),
        foodIds = CardViewIds(
            titleViewId = R.id.food_title,
            statusViewId = R.id.food_status,
            iconViewId = R.id.food_icon,
            cardViewId = R.id.food_card,
        ),
        toyIds = CardViewIds(
            titleViewId = R.id.toy_title,
            statusViewId = R.id.toy_status,
            iconViewId = R.id.toy_icon,
            cardViewId = R.id.toy_card,
        ),
        statusIds = StatusViewIds(
            titleViewId = R.id.status_title,
            valueViewId = R.id.status_value,
            iconViewId = R.id.status_icon,
            cardViewId = R.id.status_card,
        ),
    ),
}

private data class CardViewIds(
    val titleViewId: Int?,
    val statusViewId: Int,
    val iconViewId: Int,
    val cardViewId: Int,
)

private data class StatusViewIds(
    val titleViewId: Int?,
    val valueViewId: Int,
    val iconViewId: Int,
    val cardViewId: Int,
)

private data class WidgetState(
    val water: Int,
    val food: Int,
    val toy: Int,
)

private data class MoodInfo(
    val textRes: Int,
    val backgroundRes: Int,
)

private object NekoControlsWidgetState {
    private const val PREFS_NAME = "neko_controls_widget"
    private const val MISSING_VALUE = -1

    private val Context.nekoControlsWidgetDataStore by preferencesDataStore(
        name = PREFS_NAME,
        produceMigrations = { context ->
            listOf(SharedPreferencesMigration(context, PREFS_NAME))
        },
    )

    suspend fun ensureState(context: Context, appWidgetId: Int): WidgetState {
        val preferences = context.nekoControlsWidgetDataStore.data.first()
        val current = WidgetState(
            water = preferences[key(appWidgetId, WidgetItem.Water)] ?: MISSING_VALUE,
            food = preferences[key(appWidgetId, WidgetItem.Food)] ?: MISSING_VALUE,
            toy = preferences[key(appWidgetId, WidgetItem.Toy)] ?: MISSING_VALUE,
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

    suspend fun readState(context: Context, appWidgetId: Int): WidgetState {
        return ensureState(context, appWidgetId)
    }

    suspend fun randomize(context: Context, appWidgetId: Int, item: WidgetItem): WidgetState {
        val current = ensureState(context, appWidgetId)
        val next = when (item) {
            WidgetItem.Water -> current.copy(water = randomProgress())
            WidgetItem.Food -> current.copy(food = randomProgress())
            WidgetItem.Toy -> current.copy(toy = randomProgress())
        }
        writeState(context, appWidgetId, next)
        return next
    }

    suspend fun removeState(context: Context, appWidgetId: Int) {
        context.nekoControlsWidgetDataStore.edit { preferences ->
            preferences.remove(key(appWidgetId, WidgetItem.Water))
            preferences.remove(key(appWidgetId, WidgetItem.Food))
            preferences.remove(key(appWidgetId, WidgetItem.Toy))
        }
    }

    private suspend fun writeState(context: Context, appWidgetId: Int, state: WidgetState) {
        context.nekoControlsWidgetDataStore.edit { preferences ->
            preferences[key(appWidgetId, WidgetItem.Water)] = state.water
            preferences[key(appWidgetId, WidgetItem.Food)] = state.food
            preferences[key(appWidgetId, WidgetItem.Toy)] = state.toy
        }
    }

    private fun key(appWidgetId: Int, item: WidgetItem) = intPreferencesKey(
        "widget_${appWidgetId}_${item.name.lowercase()}"
    )

    private fun WidgetState.isInitialized(): Boolean {
        return water != MISSING_VALUE && food != MISSING_VALUE && toy != MISSING_VALUE
    }

    private fun randomProgress(): Int = Random.nextInt(from = 0, until = 101)
}
