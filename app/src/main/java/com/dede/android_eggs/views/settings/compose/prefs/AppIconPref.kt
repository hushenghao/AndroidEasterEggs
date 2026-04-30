@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.dede.android_eggs.views.settings.compose.prefs

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.R
import com.dede.android_eggs.alterable_adaptive_icon.AlterableAdaptiveIcon
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.basic.provider.EasterEgg
import com.dede.basic.provider.toRange
import com.dede.android_eggs.resources.R as StringsR

internal enum class AppIcon(
    val aliasName: String,
    @DrawableRes val iconRes: Int,
    val apiLevel: Int,
    val manifestEnabled: Boolean,
) {
    Android17(
        aliasName = "Android17IconAlias",
        iconRes = R.mipmap.ic_launcher_17,
        apiLevel = EasterEgg.VERSION_CODES.CINNAMON_BUN,
        manifestEnabled = true,
    ),
    Android16(
        aliasName = "Android16IconAlias",
        iconRes = R.mipmap.ic_launcher_16,
        apiLevel = Build.VERSION_CODES.BAKLAVA,
        manifestEnabled = false,
    ),
    Android15(
        aliasName = "Android15IconAlias",
        iconRes = R.mipmap.ic_launcher_15,
        apiLevel = Build.VERSION_CODES.VANILLA_ICE_CREAM,
        manifestEnabled = false,
    );

    fun componentName(context: Context): ComponentName {
        return ComponentName(context, "${context.packageName}.$aliasName")
    }
}

internal object AppIconPrefUtil {

    fun ensureValidLauncherIcon(context: Context) {
        val current = getCurrentIcon(context)
        if (current != null) return
        setIcon(context, AppIcon.Android17)
    }

    private fun getCurrentIcon(context: Context): AppIcon? {
        val pm = context.packageManager
        val explicitEnabledIcon = AppIcon.entries.firstOrNull { icon ->
            pm.getComponentEnabledSetting(icon.componentName(context)) ==
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        }
        if (explicitEnabledIcon != null) return explicitEnabledIcon

        return AppIcon.entries.firstOrNull { icon ->
            val state = pm.getComponentEnabledSetting(icon.componentName(context))
            state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT && icon.manifestEnabled
        }
    }

    private fun setIcon(context: Context, icon: AppIcon) {
        val pm = context.packageManager
        val flags = PackageManager.DONT_KILL_APP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.setComponentEnabledSettings(
                AppIcon.entries.map {
                    PackageManager.ComponentEnabledSetting(
                        it.componentName(context),
                        if (it == icon) {
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        } else {
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                        },
                        flags,
                    )
                }
            )
            return
        }

        pm.setComponentEnabledSetting(
            icon.componentName(context),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            flags,
        )
        AppIcon.entries.filterNot { it == icon }.forEach {
            pm.setComponentEnabledSetting(
                it.componentName(context),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                flags,
            )
        }
    }

    fun currentOrDefault(context: Context): AppIcon {
        return getCurrentIcon(context) ?: AppIcon.Android17
    }

    fun switchIcon(context: Context, icon: AppIcon) {
        if (currentOrDefault(context) == icon) return
        setIcon(context, icon)
    }
}

@Composable
fun AppIconPref() {
    val context = LocalContext.current
    var currentIcon by remember { mutableStateOf(AppIconPrefUtil.currentOrDefault(context)) }
    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.Apps,
        title = stringResource(StringsR.string.pref_title_app_icon),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AppIcon.entries.forEach { icon ->
                AppIconOption(
                    modifier = Modifier.weight(1f),
                    icon = icon,
                    selected = currentIcon == icon,
                    onClick = {
                        AppIconPrefUtil.switchIcon(context, icon)
                        currentIcon = icon
                    },
                )
            }
        }
    }
}

@Composable
private fun AppIconOption(
    modifier: Modifier = Modifier,
    icon: AppIcon,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) colorScheme.primaryContainer else colorScheme.surface,
            contentColor = colorScheme.onSurface,
        ),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AlterableAdaptiveIcon(
                modifier = Modifier.size(48.dp),
                res = icon.iconRes,
                clipShape = IconShapePrefUtil.getIconShape(),
            )
            val context = LocalContext.current
            val title = remember(icon.apiLevel, context) {
                EasterEggHelp.VersionFormatter.create(icon.apiLevel.toRange())
                    .format(context)
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
