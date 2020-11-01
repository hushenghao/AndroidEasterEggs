package com.dede.basic

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 运行时权限申请
 * @author hsh
 * @since 2020/10/28 2:30 PM
 */

internal typealias PermissionCallback = (Boolean) -> Unit

class PF : Fragment() {

    companion object {

        private const val F_TAG = "PF"
        private const val EXTRA_P = "permissions"
        private const val REQUEST_CODE = 1

        private fun mapPermission(context: Context, vararg permissions: String): Array<String> {
            return permissions.filter {
                ContextCompat.checkSelfPermission(
                    context,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }.toTypedArray()
        }

        @JvmStatic
        suspend fun requestPermissions(activity: Activity, vararg permissions: String) =
            suspendCoroutine<Boolean> {
                requestPermissions(activity, *permissions) { r ->
                    it.resume(r)
                }
            }

        @JvmStatic
        fun requestPermissions(
            activity: Activity,
            vararg permissions: String,
            callback: PermissionCallback
        ) {
            val mapPermission = mapPermission(activity, *permissions)
            if (mapPermission.isEmpty()) {
                callback.invoke(true)
                return
            }
            val fragmentManager = activity.fragmentManager
            var fragment = fragmentManager.findFragmentByTag(F_TAG) as? PF
            if (fragment == null) {
                fragment = PF()
            }
            fragment.callback = callback
            fragment.arguments = Bundle().apply {
                putStringArray(EXTRA_P, permissions)
            }

            fragmentManager.beginTransaction()
                .add(fragment, F_TAG)
                .commit()
        }
    }

    internal var callback: PermissionCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val permissions = arguments.getStringArray(EXTRA_P) ?: return
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requestPermissions(permissions, REQUEST_CODE)
        } else {
            preformCallback(true)
        }
    }

    private fun finish() {
        fragmentManager.beginTransaction()
            .remove(this)
            .commit()
    }

    private fun preformCallback(result: Boolean) {
        this.callback?.invoke(result)
        finish()
    }

    override fun onDestroy() {
        this.callback = null
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE) {
            preformCallback(grantResults.none { it != PackageManager.PERMISSION_GRANTED })
        }
    }
}