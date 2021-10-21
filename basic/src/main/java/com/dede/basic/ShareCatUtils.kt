@file:JvmName("ShareCatUtils")

package com.dede.basic

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


/**
 * Cat图片分享
 * @author hsh
 * @since 2020/10/28 1:40 PM
 */
object ShareCatUtils {

    private const val CATS_DIR = "Cats"
    private const val MIME_TYPE = "image/png"

    private suspend fun saveCat(context: Context, bitmap: Bitmap, catName: String): Uri? =
        withContext(Dispatchers.IO) {
            // https://developer.android.google.cn/training/data-storage/shared/media
            // 图片信息
            val imageValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, catName.toFileName())
                put(MediaStore.Images.Media.MIME_TYPE, MIME_TYPE)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imageValues.put(MediaStore.Audio.Media.IS_PENDING, 1)
                imageValues.put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/$CATS_DIR"
                )
                imageValues.put(MediaStore.Images.Media.IS_PENDING, 1)
            } else {
                val dir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    CATS_DIR
                )
                if (!dir.exists() && !dir.mkdirs()) {
                    Log.e("NekoLand", "save: error: can't create Pictures directory")
                    return@withContext null
                }
                val png = File(dir, catName.toFileName())
                Log.v("Neko", "cat file: $png")
                imageValues.put(MediaStore.Images.Media.DATA, png.absolutePath)
            }

            // 保存的位置
            val imageCollection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

            val resolver = context.contentResolver

            // 插入图片信息
            val imageUri = resolver.insert(imageCollection, imageValues)
            if (imageUri == null) {
                Log.w("NekoLand", "insert: error: uri == null")
                return@withContext null
            }
            Log.v("Neko", "cat uri: $imageUri")

            // 保存图片
            resolver.runCatching {
                openOutputStream(imageUri, "w").use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, it)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    imageValues.clear()
                    imageValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(imageUri, imageValues, null, null)
                }
            }.onFailure {
                Log.e("NekoLand", "save: error: $it")
            }
            imageUri
        }

    private fun String.toFileName(): String {
        return this.replace("[/ #:]+".toRegex(), "_") + ".png"
    }

    @JvmStatic
    fun share(activity: Activity, bitmap: Bitmap, catName: String) {

        GlobalScope.launch {
            val uri = saveCat(activity, bitmap, catName) ?: return@launch

            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.putExtra(Intent.EXTRA_SUBJECT, catName)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = MIME_TYPE
            activity.startActivity(
                Intent.createChooser(intent, null)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            )
        }
    }
}