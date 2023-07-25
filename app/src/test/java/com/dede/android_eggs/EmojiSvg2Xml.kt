package com.dede.android_eggs

import com.android.ide.common.vectordrawable.Svg2Vector
import com.dede.basic.getEmojiUnicode
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.io.File

/**
 * Downlad emoji svg and convert to android drawable xml.
 * For Android T Easter Egg.
 *
 * 🤫    \u1f92b    emoji_u1f92b.xml
 * 🐟    \u1f41f    emoji_u1f41f.xml
 *
 * @author shhu
 * @since 2023/7/24
 */
@Ignore
class EmojiSvg2Xml {

    companion object {
        private const val EMOJI_SVG_URL =
            "https://github.com/googlefonts/noto-emoji/raw/main/svg/%s"

        private val EMOJI_SETS = arrayOf(
            "🍇", "🍈", "🍉", "🍊", "🍋", "🍌", "🍍", "🥭", "🍎", "🍏", "🍐", "🍑",//
            "🍒", "🍓", "🫐", "🥝",//
            "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾",//
            "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "🙃", "🫠", "😉", "😊",
            "😇", "🥰", "😍", "🤩", "😘", "😗", "☺️", "😚", "😙", "🥲", "😋", "😛", "😜",
            "🤪", "😝", "🤑", "🤗", "🤭", "🫢", "🫣", "🤫", "🤔", "🫡", "🤐", "🤨", "😐",
            "😑", "😶", "🫥", "😏", "😒", "🙄", "😬", "🤥", "😌", "😔", "😪", "🤤", "😴", "😷",//
            "🤩", "😍", "🥰", "😘", "🥳", "🥲", "🥹",//
            "🫠",//
            "💘", "💝", "💖", "💗", "💓", "💞", "💕", "❣", "💔", "❤", "🧡", "💛",//
            "💚", "💙", "💜", "🤎", "🖤", "🤍",//
            "👁", "️🫦", "👁️",//
            "👽", "🛸", "✨", "🌟", "💫", "🚀", "🪐", "🌙", "⭐", "🌍",//
            "🌑", "🌒", "🌓", "🌔", "🌕", "🌖", "🌗", "🌘",//
            "🐙", "🪸", "🦑", "🦀", "🦐", "🐡", "🦞", "🐠", "🐟", "🐳", "🐋", "🐬", "🫧", "🌊", "🦈",//
            "🙈", "🙉", "🙊", "🐵", "🐒",//
            "♈", "♉", "♊", "♋", "♌", "♍", "♎", "♏", "♐", "♑", "♒", "♓",//
            "🕛", "🕧", "🕐", "🕜", "🕑", "🕝", "🕒", "🕞", "🕓", "🕟", "🕔", "🕠", "🕕", "🕡",//
            "🕖", "🕢", "🕗", "🕣", "🕘", "🕤", "🕙", "🕥", "🕚", "🕦",//
            "🌺", "🌸", "💮", "🏵️", "🌼", "🌿",//
            "🐢", "✨", "🌟", "👑"//
        )

        private const val DIR_SVG = "svg"
        private const val DIR_XML = "xml"
    }

    private val buildDir = File("build")
    private val svgDir = File(buildDir, DIR_SVG)
    private val xmlDir = File(buildDir, DIR_XML)

    private lateinit var httpClient: OkHttpClient

    @Before
    fun prepare() {
        httpClient = OkHttpClient.Builder().build()
        if (!xmlDir.exists()) {
            xmlDir.mkdirs()
        }
        if (!svgDir.exists()) {
            svgDir.mkdirs()
        }
    }

    @Test
    fun action() {
        var c = 0
        val size = EMOJI_SETS.size
        for (emoji in EMOJI_SETS) {
            println("Start process emoji: $emoji")
            val svgFile = downloadSvg(emoji) ?: continue
            println("Download emoji svg success: $svgFile")

            println("Convert svg 2 xml: $emoji")
            val xmlFile = svg2xml(svgFile)
            println("Convert svg 2 xml success: $xmlFile")
            println("Finish process emoji: $emoji, [${++c} : $size]")
        }
    }

    private fun svg2xml(svgFile: File): File? {
        val xmlFile = File(xmlDir, "t_" + svgFile.nameWithoutExtension + ".xml")
        val error = Svg2Vector.parseSvgToXml(svgFile.toPath(), xmlFile.outputStream())
        if (!error.isNullOrEmpty()) {
            if (xmlFile.exists()) {
                xmlFile.delete()
            }
            println("Convert svg 2 xml failure: $error")
            return null
        }
        return xmlFile
    }

    private fun downloadSvg(emoji: CharSequence): File? {
        val svgFileName = getEmojiUnicode(
            emoji,
            separator = "_",
            prefix = "emoji_u",
            postfix = ".svg"
        ).toString()
        val url = EMOJI_SVG_URL.format(svgFileName)
        val request = createHttpRequest(url)
        val response = httpClient.newCall(request).execute()
        val responseBody = response.body
        if (response.code != 200 || responseBody == null) {
            println("Download emoji svg failure: ${response.code} -> $url")
            return null
        }

        val svgFile = File(svgDir, svgFileName)
        responseBody.byteStream().use { input ->
            svgFile.outputStream().use {
                input.copyTo(it)
            }
        }
        return svgFile
    }

    private fun createHttpRequest(url: String): Request {
        return Request.Builder()
            .url(url)
            .get()
            .build()
    }
}