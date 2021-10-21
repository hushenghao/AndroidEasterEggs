@file:JvmName("ShareCatUtils")

package com.dede.basic

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
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

    private const val CATS_DIR = "Cats"

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveCatAtQ(context: Context, bitmap: Bitmap, catName: String): Uri? {
        val resolver = context.contentResolver
        val imageCollection =
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val imageDetail = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, catName.toFileName())
            put(MediaStore.Images.Media.RELATIVE_PATH, CATS_DIR)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val imageContentUri = resolver.insert(imageCollection, imageDetail)
        if (imageContentUri == null) {
            Log.w("NekoLand", "insert: error: uri == null")
            return null
        }

        try {
            val os = resolver.openOutputStream(imageContentUri, "w")
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, os)
        } catch (e: IOException) {
            Log.e("NekoLand", "save: error: $e")
        }

        imageDetail.clear()
        imageDetail.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(imageContentUri, imageDetail, null, null)
        return imageContentUri
    }

    private fun saveCatPath(catName: String):String {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            CATS_DIR
        )
        return File(dir, catName.toFileName()).absolutePath
    }

    private fun saveCat(context: Context, bitmap: Bitmap, catName: String): Uri? {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            CATS_DIR
        )
        if (!dir.exists() && !dir.mkdirs()) {
            Log.e("NekoLand", "save: error: can't create Pictures directory")
            return null
        }
        val png = File(dir, catName.toFileName())
        Log.v("Neko", "cat file: $png")
        try {
            val os: OutputStream = FileOutputStream(png)
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, os)
            os.close()
        } catch (e: IOException) {
            Log.e("NekoLand", "save: error: $e")
            return null
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            FileProvider.getUriForFile(
                context,
                context.packageName + ".fileprovider", png
            )
        } else {
            Uri.fromFile(png)
        }
    }

    private fun String.toFileName(): String {
        return this.replace("[/ #:]+".toRegex(), "_") + ".png"
    }

    @JvmStatic
    fun share(activity: Activity, bitmap: Bitmap, catName: String) {

        GlobalScope.launch {
            val uri = withContext(Dispatchers.IO) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveCatAtQ(activity, bitmap, catName)
                } else {
                    saveCat(activity, bitmap, catName)
                }
            } ?: return@launch
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                MediaScannerConnection.scanFile(
                    activity,
                    arrayOf(saveCatPath(catName)),
                    arrayOf("image/png"),
                    null
                )
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