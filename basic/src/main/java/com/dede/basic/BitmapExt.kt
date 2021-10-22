@file:JvmName("BitmapExt")

package com.dede.basic

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.InputStream


private const val TAG = "BitmapExt"

const val MIME_PNG = "image/png"
const val MIME_JPG = "image/jpg"

/**
 * 复制图片文件到相册的Pictures文件夹
 *
 * @param context 上下文
 * @param fileName 文件名。 需要携带后缀
 * @param relativePath 相对于Pictures的路径
 */
fun File.copyToAlbum(context: Context, fileName: String, relativePath: String?): Uri? {
    val imageUri = context.insertMediaImage(fileName, relativePath)
    if (imageUri == null) {
        Log.w(TAG, "insert: error: uri == null")
        return null
    }

    this.inputStream().use { input ->
        context.contentResolver.openOutputStream(imageUri, "w")?.use { output ->
            input.copyTo(output)
        }
    }

    return imageUri
}

/**
 * 保存图片Stream到相册的Pictures文件夹
 *
 * @param context 上下文
 * @param fileName 文件名。 需要携带后缀
 * @param relativePath 相对于Pictures的路径
 */
fun InputStream.saveToAlbum(context: Context, fileName: String, relativePath: String?): Uri? {
    val imageUri = context.insertMediaImage(fileName, relativePath)
    if (imageUri == null) {
        Log.w(TAG, "insert: error: uri == null")
        return null
    }

    this.use { input ->
        context.contentResolver.openOutputStream(imageUri, "w")?.use { output ->
            input.copyTo(output)
        }
    }

    return imageUri
}


/**
 * 保存Bitmap到相册的Pictures文件夹
 *
 * https://developer.android.google.cn/training/data-storage/shared/media
 *
 * @param context 上下文
 * @param fileName 文件名。 需要携带后缀
 * @param relativePath 相对于Pictures的路径
 * @param quality 质量
 */
fun Bitmap.saveToAlbum(
    context: Context,
    fileName: String,
    relativePath: String? = null,
    quality: Int = 75
): Uri? {
    // 插入图片信息
    val imageUri = context.insertMediaImage(fileName, relativePath)
    if (imageUri == null) {
        Log.w(TAG, "insert: error: uri == null")
        return null
    }

    val resolver = context.contentResolver
    // 保存图片
    resolver.runCatching {
        openOutputStream(imageUri, "w").use {
            this@saveToAlbum.compress(fileName.getCompressFormat(), quality, it)
        }
    }.onFailure {
        Log.e(TAG, "save: error: $it")
    }.onSuccess {
        // Android Q添加了IS_PENDING状态，为0时其他应用才可见
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val imageValues = ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }
            resolver.update(imageUri, imageValues, null, null)
        }
    }
    return imageUri
}

private fun String.getMimeType(): String {
    return if (this.endsWith(".png")) MIME_PNG else MIME_JPG
}

private fun String.getCompressFormat(): Bitmap.CompressFormat {
    return if (this.endsWith(".png")) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
}

/**
 * 插入图片到媒体库
 */
private fun Context.insertMediaImage(
    fileName: String,
    relativePath: String?
): Uri? {
    // 图片信息
    val imageValues = ContentValues().apply {
        put(MediaStore.Images.Media.MIME_TYPE, fileName.getMimeType())
        put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
    }
    // 保存的位置
    val collection: Uri
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val path = if (relativePath != null) {
            "${Environment.DIRECTORY_PICTURES}/${relativePath}"
        } else {
            Environment.DIRECTORY_PICTURES
        }

        imageValues.apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.RELATIVE_PATH, path)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        // 高版本不用查重直接插入，会自动重命名防止重复
    } else {
        // 老版本
        val pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val saveDir = if (relativePath != null) File(pictures, relativePath) else pictures

        if (!saveDir.exists() && !saveDir.mkdirs()) {
            Log.e(TAG, "save: error: can't create Pictures directory")
            return null
        }

        // 文件路径查重，重复的话在文件名后拼接数字
        var imageFile = File(saveDir, fileName)
        val fileNameWithoutExtension = imageFile.nameWithoutExtension
        val fileExtension = imageFile.extension

        var queryUri = this.queryMediaImage(imageFile.absolutePath)
        var suffix = 0
        while (queryUri != null) {
            val newName = fileNameWithoutExtension + "(${suffix++})." + fileExtension
            imageFile = File(saveDir, newName)
            queryUri = this.queryMediaImage(imageFile.absolutePath)
        }

        imageValues.apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            // 保存路径
            val imagePath = imageFile.absolutePath
            Log.v(TAG, "save file: $imagePath")
            put(MediaStore.Images.Media.DATA, imagePath)
        }
        collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }
    // 插入图片信息
    return this.contentResolver.insert(collection, imageValues)
}

/**
 * 查询媒体库中当前路径是否存在
 */
private fun Context.queryMediaImage(imagePath: String): Uri? {
    val resolver = this.contentResolver
    // 保存的位置
    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    // 查询是否已经存在相同图片
    val query = resolver.query(
        collection,
        arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA
        ),
        "${MediaStore.Images.Media.DATA} == ?",
        arrayOf(imagePath), null
    )
    query?.use {
        while (it.moveToNext()) {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val id = it.getLong(idColumn)
            val existsUri = ContentUris.withAppendedId(collection, id)
            Log.v(TAG, "query: path: $imagePath exists uri: $existsUri")
            return existsUri
        }
    }
    return null
}
