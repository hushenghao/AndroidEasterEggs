@file:JvmName("ShareCatUtils")

package com.dede.basic.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.dede.basic.MIME_PNG
import com.dede.basic.androidLifecycle
import com.dede.basic.launch
import com.dede.basic.saveToAlbum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * Cat图片分享
 * @author hsh
 * @since 2020/10/28 1:40 PM
 */
object ShareCatUtils {

    private const val CATS_DIR = "Cats"

    private suspend fun saveCat(context: Context, bitmap: Bitmap, catName: String): Uri? =
        withContext(Dispatchers.IO) {
            bitmap.saveToAlbum(context, catName.toFileName(), CATS_DIR, 0)
        }

    private fun String.toFileName(): String {
        return this.replace("[/ #:]+".toRegex(), "_") + ".png"
    }

    @JvmStatic
    fun share(activity: Activity, bitmap: Bitmap, catName: String) {
        activity.androidLifecycle.launch {
            val uri = saveCat(activity, bitmap, catName) ?: return@launch
            Log.v("Neko", "cat uri: $uri")

            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.putExtra(Intent.EXTRA_SUBJECT, catName)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = MIME_PNG
            activity.startActivity(
                Intent.createChooser(intent, null)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            )
        }
    }
}