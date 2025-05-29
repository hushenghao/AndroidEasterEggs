@file:JvmName("ShareCatUtils")

package com.dede.basic.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresPermission
import com.dede.basic.MIME_PNG
import com.dede.basic.createChooser
import com.dede.basic.launch
import com.dede.basic.lifecycleCompat
import com.dede.basic.saveToAlbum
import com.dede.basic.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.appcompat.R as appCompatR


/**
 * Cat图片分享
 * @author hsh
 * @since 2020/10/28 1:40 PM
 */
object ShareCatUtils {

    private const val CATS_DIR = "Cats"

    @JvmStatic
    @ChecksSdkIntAtLeast(Build.VERSION_CODES.Q)
    val isNotRequireStoragePermissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    @JvmStatic
    val storagePermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @RequiresPermission(
        allOf = [Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE],
        conditional = true
    )
    suspend fun saveCat(context: Context, bitmap: Bitmap, catName: String): Uri? =
        withContext(Dispatchers.IO) {
            try {
                bitmap.saveToAlbum(context, catName.toFileName(), CATS_DIR, 0)
            } catch (e: Throwable) {
                null
            }
        }

    private fun String.toFileName(): String {
        return this.replace("[/ #:]+".toRegex(), "_") + ".png"
    }

    @JvmStatic
    @RequiresPermission(
        allOf = [Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE],
        conditional = true
    )
    fun shareCat(activity: Activity, bitmap: Bitmap, catName: String) {
        activity.lifecycleCompat.launch {
            val uri = saveCat(activity, bitmap, catName) ?: return@launch
            Log.v("Neko", "cat uri: $uri")

            shareCatOnly(activity, uri, catName)
        }
    }

    fun shareCatOnly(context: Context, uri: Uri, catName: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.putExtra(Intent.EXTRA_SUBJECT, catName)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = MIME_PNG

        @SuppressLint("PrivateResource")
        val title = context.getString(appCompatR.string.abc_shareactionprovider_share_with)
        val chooser = context.createChooser(intent, title)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            context.startActivity(chooser)
        } catch (_: ActivityNotFoundException) {
            context.toast("Share failure!")
        }
    }
}