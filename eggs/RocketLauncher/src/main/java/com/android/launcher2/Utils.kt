package com.android.launcher2

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.text.TextUtils
import androidx.annotation.WorkerThread
import com.android.launcher2.RocketLauncher.Board.RocketLauncherEntryPoint
import com.android.launcher2.RocketLauncherPrefUtil.VALUE_ALL_APP_ICONS
import com.android.launcher2.RocketLauncherPrefUtil.VALUE_ALL_ICONS
import com.android.launcher2.RocketLauncherPrefUtil.VALUE_EASTER_EGG_ICONS
import com.dede.basic.provider.EasterEgg
import com.dede.basic.requireDrawable
import dagger.hilt.android.EntryPointAccessors.fromApplication

internal object Utils {

    @JvmStatic
    @WorkerThread
    fun getComponentNameDrawableIcons(context: Context): HashMap<ComponentName, Drawable> {
        val sourceValue = RocketLauncherPrefUtil.getCurrentIconsSourceValue(context)
        return getComponentNameDrawableIconsBySourceValue(context, sourceValue)
    }

    private fun getComponentNameDrawableIconsBySourceValue(
        context: Context, sourceValue: Int
    ): HashMap<ComponentName, Drawable> {
        return when (sourceValue) {
            VALUE_EASTER_EGG_ICONS -> {
                // Inject in DreamService and Activity
                val easterEggs = fromApplication<RocketLauncherEntryPoint>(context).easterEggs
                convertComponentNameDrawableIcons(context, easterEggs)
            }
            VALUE_ALL_APP_ICONS -> {
                queryAppLaunchComponentNameDrawableIcons(context)
            }
            VALUE_ALL_ICONS -> {
                HashMap(
                    getComponentNameDrawableIconsBySourceValue(context, VALUE_EASTER_EGG_ICONS) +
                            getComponentNameDrawableIconsBySourceValue(context, VALUE_ALL_APP_ICONS)
                )
            }
            else -> throw IllegalStateException("Unknown icons source value: $sourceValue")
        }
    }

    private fun queryAppLaunchComponentNameDrawableIcons(context: Context): HashMap<ComponentName, Drawable> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
        val activities = pm.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES)
        val icons = HashMap<ComponentName, Drawable>()
        for (info in activities) {
            val packageName = info?.activityInfo?.packageName
            if (packageName == null || TextUtils.isEmpty(packageName)) continue

            val launchComponent = pm.getLaunchIntentForPackage(packageName)?.component
                ?: createNotFoundComponent(context, packageName.hashCode())
            val drawable = info.loadIcon(pm)
            icons[launchComponent] = drawable
        }
        return icons
    }

    @JvmStatic
    private fun convertComponentNameDrawableIcons(
        context: Context,
        easterEggs: List<EasterEgg>
    ): HashMap<ComponentName, Drawable> {
        val icons = HashMap<ComponentName, Drawable>()
        for (egg in easterEggs) {
            val drawable: Drawable = context.requireDrawable(egg.iconRes)
            val aClass = egg.actionClass
            val componentName: ComponentName = if (aClass != null) {
                ComponentName(context, aClass)
            } else {
                createNotFoundComponent(context, egg.hashCode())
            }
            icons[componentName] = drawable
        }
        return icons
    }

    private fun createNotFoundComponent(context: Context, hash: Int): ComponentName {
        return ComponentName(context, "NotFound%d".format(hash))
    }
}