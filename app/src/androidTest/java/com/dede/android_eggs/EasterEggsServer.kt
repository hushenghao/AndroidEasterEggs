package com.dede.android_eggs

import android.content.Context
import android.graphics.Bitmap
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
    private val routes = ArrayList<String>()

    private var host: String = "http://localhost:$PORT"

    private fun initHost(context: Context) {
        val ipv4AddressRequest = Ipv4AddressRequest()
        ipv4AddressRequest.request(context, object : Ipv4AddressRequest.Callback {
            override fun onResult(ipv4Address: String?) {
                if (ipv4Address != null) {
                    host = "http://${ipv4Address}:$PORT"
                }
            }
        })
    }

    init {
        initHost(context)
        setTempFileManagerFactory {
            return@setTempFileManagerFactory CallFinishTempFileManager()
        }
        val homepage = object : Handler() {
            override fun onHandler(session: IHTTPSession): Response? {
                routes.sort()
                val sb = StringBuilder("<h1>Hello from Easter Eggs server!</h1>")
                for (route in routes) {
                    sb.append("<a href='")
                        .append(route)
                        .append("'>")
                        .append(route)
                        .append("</a></br>")
                }
                return newFixedLengthResponse(sb.toString())
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
        }, true)
    }

    override fun start() {
        start(0, false)
    }

    override fun start(timeout: Int, daemon: Boolean) {
        super.start(timeout, daemon)
        Log.i(TAG, "Open $host in your browser")
    }

    private fun registerHandler(uri: String?, handler: Handler, route: Boolean) {
        handlers[uri] = handler
        if (route && uri != null) {
            routes.add(uri)
        }
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