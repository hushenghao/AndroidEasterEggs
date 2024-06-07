package com.dede.android_eggs.fake_test.utils

import android.content.Context
import android.util.Log
import androidx.collection.ArrayMap
import com.dede.android_eggs.R
import fi.iki.elonen.NanoHTTPD
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

        fun disposable(
            context: Context,
            uri: String,
            timeout: Long = 30 * 1000L,
            onHandler: IHTTPSession.() -> Response?,
        ) {
            val lock = WaitFinishLock(timeout)
            val server = EasterEggsServer(context)
            lock.withServer(server)
            server.registerHandler(uri, onHandler)

            server.start()
            lock.await()
            server.stop()
        }

        fun EasterEggsServer.registerHandler(uri: String, onHandler: IHTTPSession.() -> Response?) {
            registerHandler(uri, object : Handler() {
                override fun onHandler(session: IHTTPSession): Response? {
                    return onHandler.invoke(session)
                }
            })
        }

        fun start(context: Context, function: EasterEggsServer.() -> Unit) {
            val lock = WaitFinishLock(0)
            val server = EasterEggsServer(context)
            lock.withServer(server)
            function.invoke(server)

            server.start()
            lock.await()
            server.stop()
        }
    }

    class WaitFinishLock(private val timeout: Long) {

        private val lock = Object()

        fun withServer(server: EasterEggsServer) {
            server.setOnShutdownListener(object : OnShutdownListener {
                override fun onShutdown() {
                    unlock()
                }
            })
        }

        fun await() {
            synchronized(lock) {
                if (timeout > 0) {
                    lock.wait(timeout)
                } else {
                    lock.wait()
                }
            }
        }

        fun unlock() {
            synchronized(lock) {
                lock.notify()
            }
        }
    }

    interface OnShutdownListener {
        fun onShutdown()
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

    private var shutdownListener: OnShutdownListener? = null

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
                val sb = StringBuilder()
                sb.append("<html>")
                sb.append("<head>")
                sb.append("<title>Easter Eggs server</title>")
                sb.append("</head>")
                sb.append("<body>")
                sb.append("<h1>Hello from Easter Eggs server!</h1>")
                for (route in routes) {
                    sb.append("<a href='")
                        .append(route)
                        .append("'>")
                        .append(route)
                        .append("</a><br/>")
                }
                sb.append("<br/><form action='/shutdown' method='get'>")
                sb.append("<button type='submit'>Shut Down</button>")
                sb.append("</form>")
                sb.append("</body>")
                sb.append("</html>")
                return newFixedLengthResponse(sb.toString())
            }
        }
        registerHandler(null, homepage, false)
        registerHandler("/", homepage, false)
        registerHandler("/favicon.ico", object : Handler() {
            override fun onHandler(session: IHTTPSession): Response {
                return ResponseUtils.createDrawableResponse(
                    context, R.mipmap.ic_launcher_round, 64, 64
                )
            }
        }, true)
        registerHandler("/shutdown", object : Handler() {
            override fun onHandler(session: IHTTPSession): Response {
                return newFixedLengthResponse("Shutting Down...")
            }

            override fun onFinish() {
                stop()
            }
        }, false)
    }

    override fun stop() {
        super.stop()
        shutdownListener?.onShutdown()
    }

    override fun start() {
        start(0, false)
    }

    override fun start(timeout: Int, daemon: Boolean) {
        super.start(timeout, daemon)
        Log.i(TAG, "Open $host in your browser")
    }

    fun setOnShutdownListener(listener: OnShutdownListener) {
        shutdownListener = listener
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
                return newFixedLengthResponse(
                    Response.Status.INTERNAL_ERROR,
                    MIME_HTML, e.toString()
                )
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