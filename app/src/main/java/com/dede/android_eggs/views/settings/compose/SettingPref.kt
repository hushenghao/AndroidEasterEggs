package com.dede.android_eggs.views.settings.compose

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.util.pref

object SettingPref {
    const val ON = 1
    const val OFF = 0

    const val EXTRA_VALUE = "extra_value"

    const val ACTION_CLOSE_SETTING = "com.dede.easter_eggs.CloseSetting"

    fun getValue(context: Context, key: String, default: Int): Int {
        return context.pref.getInt(key, default)
    }

    fun setValue(context: Context, key: String, value: Int) {
        context.pref.edit().putInt(key, value).apply()
    }
}

@Composable
fun SettingPref(
    leadingIcon: ImageVector,
    title: String,
    desc: String? = null,
    trailingContent: @Composable () -> Unit = {},
    onClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    SettingPref(
        leadingIcon = {
            Box(Modifier.padding(start = 14.dp)) {
                Icon(imageVector = leadingIcon, contentDescription = title)
            }
        },
        title = title,
        desc = desc,
        trailingContent = trailingContent,
        onClick = onClick,
        shape = shape,
        content = content
    )
}

@Composable
fun SettingPref(
    leadingIcon: @Composable () -> Unit,
    title: String,
    desc: String? = null,
    trailingContent: @Composable () -> Unit = {},
    onClick: () -> Unit = {},
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Card(
        onClick = onClick,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
        ),
    ) {
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                leadingIcon()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp)
                        .animateContentSize()
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    if (desc != null) {
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                trailingContent()
            }
            Column {
                content()
            }
        }
    }
}

