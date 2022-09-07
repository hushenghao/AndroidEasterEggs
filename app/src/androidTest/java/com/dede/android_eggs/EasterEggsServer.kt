package com.dede.android_eggs

import android.content.Context
import android.graphics.Bitmap
import android.net.wifi.WifiManager
import android.util.Log
import androidx.collection.ArrayMap
import androidx.core.graphics.drawable.toBitmap
import com.dede.basic.requireDrawable
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Easter Eggs server
 *
 * @author shhu
 * @since 2022/9/7
 */
class EasterEggsServer(private val context: Context) : NanoHTTPD(PORT) {

    companion object {
        private const val TAG = "EasterEggsServer"
        private const val PORT = 8888

        fun getAddress(context: Context): String {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val ipAddress = wifiManager.connectionInfo.ipAddress
            return String.format("%d.%d.%d.%d",
                ipAddress and 0xff, ipAddress shr 8 and 0xff, ipAddress shr 16 and 0xff,
                ipAddress shr 24 and 0xff)
        }
    }

    class WaitFinishLock(private val timeout: Long) {

        private val lock = Object()

        fun await() {
            synchronized(lock) {
                lock.wait(timeout)
            }
        }

        fun unlock() {
            synchronized(lock) {
                lock.notify()
            }
        }
    }

    abstract class Handler {
        private fun IHTTPSession.getFinishManager(): CallFinishTempFileManager? {
            try {
                val field = HTTPSession::class.java.getDeclaredField("tempFileManager")
                field.isAccessible = true
                return field.get(this) as? CallFinishTempFileManager
            } catch (e: Exception) {
            }
            return null
        }

        fun serve(session: IHTTPSession): Response? {
            val finishManager = session.getFinishManager()
            finishManager?.setOnFinishListener(this)
            return onHandler(session)
        }

        @Throws(IOException::class)
        abstract fun onHandler(session: IHTTPSession): Response?

        open fun onFinish() {
        }
    }

    private val handlers = ArrayMap<String?, Handler>()

    private var host: String = "http://localhost:$PORT"

    init {
        host = "http://${getAddress(context)}:$PORT"
        setTempFileManagerFactory {
            return@setTempFileManagerFactory CallFinishTempFileManager()
        }
        val homepage = object : Handler() {
            override fun onHandler(session: IHTTPSession): Response? {
                return newFixedLengthResponse("Hello from Easter Eggs server!")
            }
        }
        registerHandler(null, homepage, false)
        registerHandler("/", homepage, false)
        registerHandler("/favicon.ico", object : Handler() {
            override fun onHandler(session: IHTTPSession): Response? {
                val drawable = context.requireDrawable(R.mipmap.ic_launcher_round)
                val bitmap = drawable.toBitmap(48, 48)
                val output = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                val byte = output.toByteArray()
                val input = ByteArrayInputStream(byte)
                return newFixedLengthResponse(Response.Status.OK,
                    "image/*",
                    input,
                    input.available().toLong())
            }
        }, false)
    }

    override fun start() {
        start(0, false)
    }

    override fun start(timeout: Int, daemon: Boolean) {
        super.start(timeout, daemon)
        Log.i(TAG, "Open $host in your browser")
    }

    private fun registerHandler(uri: String?, handler: Handler, log: Boolean) {
        handlers[uri] = handler
        if (log)
            Log.i(TAG, "registerRoute: ${host}$uri")
    }

    fun registerHandler(uri: String, handler: Handler) {
        registerHandler(uri, handler, true)
    }

    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri
        val handler = handlers[uri]
        Log.i(TAG, "serve: $uri")
        if (handler != null) {
            try {
                val response = handler.serve(session)
                if (response != null) {
                    return response
                }
            } catch (e: IOException) {
                Log.w(TAG, "serve error.", e)
            }
        }
        return super.serve(session)
    }

    private class CallFinishTempFileManager : DefaultTempFileManager() {

        private var onFinished: Handler? = null

        fun setOnFinishListener(handler: Handler) {
            this.onFinished = handler
        }

        override fun clear() {
            super.clear()
            onFinished?.onFinish()
        }
    }

}