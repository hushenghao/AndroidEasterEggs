@file:JvmName("ShareCatUtils")

package com.dede.basic.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
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

    suspend fun saveCat(context: Context, bitmap: Bitmap, catName: String): Uri? =
        withContext(Dispatchers.IO) {
            bitmap.saveToAlbum(context, catName.toFileName(), CATS_DIR, 0)
        }

    private fun String.toFileName(): String {
        return this.replace("[/ #:]+".toRegex(), "_") + ".png"
    }

    @JvmStatic
    fun share(activity: Activity, bitmap: Bitmap, catName: String) {
        activity.lifecycleCompat.launch {
            val uri = saveCat(activity, bitmap, catName) ?: return@launch
            Log.v("Neko", "cat uri: $uri")

            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.putExtra(Intent.EXTRA_SUBJECT, catName)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = MIME_PNG

            @SuppressLint("PrivateResource")
            val title = activity.getString(appCompatR.string.abc_shareactionprovider_share_with)
            val chooser = activity.createChooser(intent, title)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                activity.startActivity(chooser)
            } catch (_: ActivityNotFoundException) {
                activity.toast("Share failure!")
            }
        }
    }
}