package com.dede.android_eggs.cat_editor

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@ExperimentalPermissionsApi
@Composable
internal fun rememberMultiplePermissionsStateCompat(
    permissions: Array<String>,
    onPermissionsResult: (Map<String, Boolean>) -> Unit = {},
    previewPermissionStatuses: Map<String, PermissionStatus> = emptyMap(),
    isNotRequire: Boolean = false
): MultiplePermissionsState {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || isNotRequire) {
        return remember { AllPermissionsGrantedState(permissions) }
    }
    return rememberMultiplePermissionsState(
        permissions.toList(),
        onPermissionsResult,
        previewPermissionStatuses
    )
}

@ExperimentalPermissionsApi
@Stable
private class AllPermissionsGrantedState(permissions: Array<String>) : MultiplePermissionsState {
    override val allPermissionsGranted: Boolean = true
    override val permissions: List<PermissionState> =
        permissions.map { PermissionsGrantedState(it) }
    override val revokedPermissions: List<PermissionState> = emptyList()
    override val shouldShowRationale: Boolean = false
    override fun launchMultiplePermissionRequest() {}
}

@ExperimentalPermissionsApi
@Stable
private class PermissionsGrantedState(override val permission: String) : PermissionState {
    override val status: PermissionStatus = PermissionStatus.Granted
    override fun launchPermissionRequest() {}
}
