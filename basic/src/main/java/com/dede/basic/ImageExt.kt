@file:JvmName("ImageExt")

package com.dede.basic

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresPermission
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.OutputStream


private const val TAG = "ImageExt"

const val MIME_PNG = "image/png"
const val MIME_JPG = "image/jpg"
const val MIME_WEBP = "image/webp"

private val ALBUM_DIR = Environment.DIRECTORY_PICTURES

private fun getImageMime(fileName: String): String {
    return when (File(fileName).extension.lowercase()) {
        "webp" -> MIME_WEBP
        "png" -> MIME_PNG
        "jpg", "jpeg" -> MIME_JPG
        else -> {
            Log.w(TAG, "Unknown mime_type, fileName: $fileName")
            MIME_JPG
        }
    }
}

private fun getImageFormat(fileName: String): Bitmap.CompressFormat {
    return when (File(fileName).extension.lowercase()) {
        "webp" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            @Suppress("DEPRECATION")
            Bitmap.CompressFormat.WEBP
        }

        "png" -> Bitmap.CompressFormat.PNG
        "jpg", "jpeg" -> Bitmap.CompressFormat.JPEG
        else -> {
            Log.w(TAG, "Unknown image format, fileName: $fileName")
            Bitmap.CompressFormat.JPEG
        }
    }
}

private class OutputFileTaker(var file: File? = null)

/**
 * 复制图片文件到相册的Pictures文件夹
 *
 * @param context 上下文
 * @param fileName 文件名。 需要携带后缀
 * @param relativePath 相对于Pictures的路径
 */
@RequiresPermission(
    allOf = [Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE],
    conditional = true
)
fun File.copyToAlbum(context: Context, fileName: String, relativePath: String?): Uri? {
    if (!this.canRead() || !this.exists()) {
        Log.w(TAG, "check: read file error: $this")
        return null
    }
    return this.inputStream().use {
        it.saveToAlbum(context, fileName, relativePath)
    }
}

/**
 * 保存图片Stream到相册的Pictures文件夹
 *
 * @param context 上下文
 * @param fileName 文件名。 需要携带后缀
 * @param relativePath 相对于Pictures的路径
 */
@RequiresPermission(
    allOf = [Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE],
    conditional = true
)
fun InputStream.saveToAlbum(context: Context, fileName: String, relativePath: String?): Uri? {
    val resolver = context.contentResolver
    val outputFile = OutputFileTaker()
    val imageUri = resolver.insertMediaImage(fileName, relativePath, outputFile)
    if (imageUri == null) {
        Log.w(TAG, "insert: error: uri == null")
        return null
    }

    (imageUri.outputStream(resolver) ?: return null).use { output ->
        this.use { input ->
            input.copyTo(output)
            imageUri.finishPending(context, resolver, outputFile.file)
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
@RequiresPermission(
    allOf = [Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE],
    conditional = true
)
fun Bitmap.saveToAlbum(
    context: Context,
    fileName: String,
    relativePath: String? = null,
    quality: Int = 75,
): Uri? {
    // 插入图片信息
    val resolver = context.contentResolver
    val outputFile = OutputFileTaker()
    val imageUri = resolver.insertMediaImage(fileName, relativePath, outputFile)
    if (imageUri == null) {
        Log.w(TAG, "insert: error: uri == null")
        return null
    }

    // 保存图片
    (imageUri.outputStream(resolver) ?: return null).use {
        val format = getImageFormat(fileName)
        this@saveToAlbum.compress(format, quality, it)
        imageUri.finishPending(context, resolver, outputFile.file)
    }
    return imageUri
}

private fun Uri.outputStream(resolver: ContentResolver): OutputStream? {
    return try {
        resolver.openOutputStream(this)
    } catch (e: FileNotFoundException) {
        Log.e(TAG, "save: open stream error: $e")
        null
    }
}

private fun Uri.finishPending(
    context: Context,
    resolver: ContentResolver,
    outputFile: File?,
) {
    val imageValues = ContentValues()
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        if (outputFile != null) {
            imageValues.put(MediaStore.Images.Media.SIZE, outputFile.length())
        }
        resolver.update(this, imageValues, null, null)
        // 通知媒体库更新
        @Suppress("DEPRECATION")
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, this)
        context.sendBroadcast(intent)
    } else {
        // Android Q添加了IS_PENDING状态，为0时其他应用才可见
        imageValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(this, imageValues, null, null)
    }
}

/**
 * 插入图片到媒体库
 */
private fun ContentResolver.insertMediaImage(
    fileName: String,
    relativePath: String?,
    outputFileTaker: OutputFileTaker? = null,
): Uri? {
    // 图片信息
    val imageValues = ContentValues().apply {
        val mimeType = getImageMime(fileName)
        put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        val date = System.currentTimeMillis() / 1000
        put(MediaStore.Images.Media.DATE_ADDED, date)
        put(MediaStore.Images.Media.DATE_MODIFIED, date)
    }
    // 保存的位置
    val collection: Uri
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val path = if (relativePath != null) "${ALBUM_DIR}/${relativePath}" else ALBUM_DIR
        imageValues.apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.RELATIVE_PATH, path)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        // 高版本不用查重直接插入，会自动重命名
    } else {
        // 老版本
        val pictures = Environment.getExternalStoragePublicDirectory(ALBUM_DIR)
        val saveDir = if (relativePath != null) File(pictures, relativePath) else pictures

        if (!saveDir.exists() && !saveDir.mkdirs()) {
            Log.e(TAG, "save: error: can't create Pictures directory")
            return null
        }

        // 文件路径查重，重复的话在文件名后拼接数字
        var imageFile = File(saveDir, fileName)
        val fileNameWithoutExtension = imageFile.nameWithoutExtension
        val fileExtension = imageFile.extension

        var queryUri = this.queryMediaImage28(imageFile.absolutePath)
        var suffix = 1
        while (queryUri != null) {
            val newName = fileNameWithoutExtension + "(${suffix++})." + fileExtension
            imageFile = File(saveDir, newName)
            queryUri = this.queryMediaImage28(imageFile.absolutePath)
        }

        imageValues.apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.name)
            // 保存路径
            val imagePath = imageFile.absolutePath
            Log.v(TAG, "save file: $imagePath")
            put(MediaStore.Images.Media.DATA, imagePath)
        }
        outputFileTaker?.file = imageFile// 回传文件路径，用于设置文件大小
        collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }
    // 插入图片信息
    return this.insert(collection, imageValues)
}

/**
 * Android Q以下版本，查询媒体库中当前路径是否存在
 * @return Uri 返回null时说明不存在，可以进行图片插入逻辑
 */
private fun ContentResolver.queryMediaImage28(imagePath: String): Uri? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return null

    val imageFile = File(imagePath)
    if (imageFile.canRead() && imageFile.exists()) {
        Log.v(TAG, "query: path: $imagePath exists")
        // 文件已存在，返回一个file://xxx的uri
        return Uri.fromFile(imageFile)
    }
    // 保存的位置
    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    // 查询是否已经存在相同图片
    val query = this.query(
        collection,
        arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA),
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
