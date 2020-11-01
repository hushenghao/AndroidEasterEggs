@file:JvmName("ShareCatUtils")

package com.dede.basic

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


/**
 * Cat图片分享
 * @author hsh
 * @since 2020/10/28 1:40 PM
 */
object ShareCatUtils {

    @JvmStatic
    fun share(activity: Activity, bitmap: Bitmap, catName: String) {

        fun saveCat(bitmap: Bitmap, catName: String): File? {
            val dir: File = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Cats"
            )
            if (!dir.exists() && !dir.mkdirs()) {
                Log.e("NekoLand", "save: error: can't create Pictures directory")
                return null
            }
            try {
                val png = File(dir, catName.replace("[/ #:]+".toRegex(), "_") + ".png")
                val os: OutputStream = FileOutputStream(png)
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, os)
                os.close()
                return png
            } catch (e: IOException) {
                Log.e("NekoLand", "save: error: $e")
            }
            return null
        }

        GlobalScope.launch {
            val png = withContext(Dispatchers.IO) { saveCat(bitmap, catName) } ?: return@launch
            MediaScannerConnection.scanFile(
                activity,
                arrayOf(png.toString()),
                arrayOf("image/png"),
                null
            )
            Log.v("Neko", "cat file: $png")
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                FileProvider.getUriForFile(
                    activity,
                    activity.packageName + ".fileprovider", png
                )
            } else {
                Uri.fromFile(png)
            }
            Log.v("Neko", "cat uri: $uri")
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.putExtra(Intent.EXTRA_SUBJECT, catName)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "image/png"
            activity.startActivity(
                Intent.createChooser(intent, null)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            )
        }
    }
}