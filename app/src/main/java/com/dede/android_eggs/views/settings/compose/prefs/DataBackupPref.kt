package com.dede.android_eggs.views.settings.compose.prefs

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.FileDownloadDone
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import com.dede.android_eggs.views.settings.backup.DataBackupManager
import com.dede.android_eggs.views.settings.compose.basic.ExpandOptionsPref
import com.dede.android_eggs.views.settings.compose.basic.Option
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock
import com.dede.basic.Utils
import com.dede.basic.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.dede.android_eggs.resources.R as StringsR

@Composable
fun DataBackupPref() {
    val context = LocalContext.current
    val resources = LocalResources.current
    val scope = rememberCoroutineScope()
    var expanded by rememberSaveable { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip"),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val result = withContext(Dispatchers.IO) {
                    DataBackupManager.export(context, uri)
                }
                result.onSuccess {
                    context.toast(StringsR.string.toast_backup_success)
                }.onFailure { e ->
                    val msg = resources.getString(StringsR.string.toast_backup_failed, e.message ?: "")
                    context.toast(msg)
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val result = withContext(Dispatchers.IO) {
                    DataBackupManager.import(context, uri)
                }
                result.onSuccess {
                    context.toast(StringsR.string.toast_restore_success)
                    Utils.restartApp(context)
                }.onFailure { e ->
                    val msg = resources.getString(StringsR.string.toast_restore_failed, e.message ?: "")
                    context.toast(msg)
                }
            }
        }
    }

    ExpandOptionsPref(
        leadingIcon = Icons.Rounded.Backup,
        title = stringResource(StringsR.string.label_data_backup),
        desc = stringResource(StringsR.string.pref_data_backup_desc),
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        Option(
            leadingIcon = imageVectorIconBlock(Icons.Rounded.FileDownloadDone),
            title = stringResource(StringsR.string.action_export_backup),
            desc = stringResource(StringsR.string.action_export_backup_desc),
            shape = OptionShapes.firstShape(),
            onClick = {
                val fileName = "easter_eggs_backup_${
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                }.zip"
                exportLauncher.launch(fileName)
            },
        )
        Option(
            leadingIcon = imageVectorIconBlock(Icons.Rounded.Restore),
            title = stringResource(StringsR.string.action_import_backup),
            desc = stringResource(StringsR.string.action_import_backup_desc),
            shape = OptionShapes.lastShape(),
            onClick = {
                importLauncher.launch(arrayOf("application/zip", "application/octet-stream"))
            },
        )
    }
}
