package com.android.launcher2

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread
import com.android.launcher2.RocketLauncher.Board.RocketLauncherEntryPoint
import com.dede.basic.provider.EasterEgg
import com.dede.basic.requireDrawable
import dagger.hilt.android.EntryPointAccessors.fromApplication

internal object Utils {

    @JvmStatic
    @RequiresPermission(Manifest.permission.QUERY_ALL_PACKAGES)
    @WorkerThread
    fun getComponentNameDrawableIcons(context: Context): HashMap<ComponentName, Drawable> {
        val sourceValue = RocketLauncherPrefUtil.getCurrentIconsSourceValue(context)
        return getComponentNameDrawableIconsBySourceValue(context, sourceValue)
    }

    private fun getComponentNameDrawableIconsBySourceValue(
        context: Context, sourceValue: Int
    ): HashMap<ComponentName, Drawable> {
        return when (sourceValue) {
            RocketLauncherPrefUtil.VALUE_EASTER_EGG_ICONS -> {
                // Inject in DreamService and Activity
                val easterEggs = fromApplication<RocketLauncherEntryPoint>(context).easterEggs
                convertComponentNameDrawableIcons(context, easterEggs)
            }
            RocketLauncherPrefUtil.VALUE_ALL_APP_ICONS -> {
                queryAllPackagesComponentNameDrawableIcons(context)
            }
            RocketLauncherPrefUtil.VALUE_ALL_ICONS -> {
                HashMap(
                    getComponentNameDrawableIconsBySourceValue(
                        context,
                        RocketLauncherPrefUtil.VALUE_EASTER_EGG_ICONS
                    ) + getComponentNameDrawableIconsBySourceValue(
                        context,
                        RocketLauncherPrefUtil.VALUE_ALL_APP_ICONS
                    )
                )
            }
            else -> throw IllegalStateException("Unknown icons source value: $sourceValue")
        }
    }

    @JvmStatic
    @RequiresPermission(Manifest.permission.QUERY_ALL_PACKAGES)
    private fun queryAllPackagesComponentNameDrawableIcons(context: Context): HashMap<ComponentName, Drawable> {
        val packageManager = context.packageManager
        val list = packageManager
            .getInstalledApplications(PackageManager.GET_ACTIVITIES)
        val icons = HashMap<ComponentName, Drawable>()
        for (info in list) {
            if (!info.enabled) {
                continue
            }
            val launchComponent = packageManager
                .getLaunchIntentForPackage(info.packageName)?.component ?: continue
            val drawable: Drawable
            try {
                drawable = packageManager.getApplicationIcon(info.packageName);
            } catch (ignore: PackageManager.NameNotFoundException) {
                continue
            }
            icons[launchComponent] = drawable;
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