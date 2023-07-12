package com.android_u.egg.landroid

import android.content.Context
import android.content.Intent
import android.util.Log
import dalvik.system.DexClassLoader
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Dynamic loading Android 14 Easter Egg main.
 *
 * @author shhu
 * @since 2023/7/12
 */
object EasterEggLauncher {

    private const val TAG = "EasterEggLauncher"

    var classLoader: ClassLoader? = null
        private set

    @JvmStatic
    fun launch(context: Context) {
        var dexPath: File? = null
        var input: InputStream? = null
        var output: OutputStream? = null
        try {
            dexPath = File.createTempFile("android_u_EasterEgg", ".apk")
            input = context.assets.open("plugins/android_u_EasterEgg.apk")
            output = dexPath.outputStream()
            input.copyTo(output)
            dexPath.setReadOnly()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            close(output)
            close(input)
        }
        if (dexPath == null || !dexPath.exists()) {
            return
        }
        Log.i(TAG, "launch: $dexPath")
        val dexOutputPath = context.getDir("dex", Context.MODE_PRIVATE)
        val classLoader = DexClassLoader(
            dexPath.absolutePath,
            dexOutputPath.absolutePath,
            null,
            javaClass.classLoader
        )
        this.classLoader = classLoader
        try {
            val clazz0 = classLoader.loadClass("androidx.compose.runtime.SnapshotStateKt")
            val clazz1 = Class.forName("androidx.compose.runtime.SnapshotStateKt")
            Log.i(TAG, "launch: " + clazz0.hashCode())
            Log.i(TAG, "launch: " + clazz1.hashCode())
            val clazz = classLoader.loadClass("com.android.egg.landroid.MainActivity")
            val instance = clazz.newInstance()
            Log.i(TAG, "launch: " + instance)
            val intent = Intent(Intent.ACTION_VIEW)
                .setClassName(context.packageName, "com.android.egg.landroid.MainActivity")
            context.startActivity(intent)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun close(closeable: Closeable?) {
        try {
            closeable?.close()
        } catch (_: IOException) {
        }
    }
}